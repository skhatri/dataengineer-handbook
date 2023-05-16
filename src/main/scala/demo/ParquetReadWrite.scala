package demo

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.sql.Date
import java.time.LocalDate
import scala.collection.mutable

case class OpensourceProject(name: String, year: Int, language: String, releaseDate: Date) extends Serializable {
  override def toString: String = s"$name,$year,$language,$releaseDate"
}


object ParquetReadWrite extends App {

  import org.apache.hadoop.security.UserGroupInformation

  UserGroupInformation.setLoginUser(UserGroupInformation.createRemoteUser("root"))


  val config = ConfigFactory.load

  import scala.collection.JavaConverters._

  val items: mutable.Map[String, AnyRef] = config.getObject("parquet").unwrapped().asScala

  def flattenMap(prefix: String, mp: Map[String, AnyRef]): Map[String, String] = {
    mp.foldLeft(Map.empty[String, String])((agg, item) => {
      val mp: Map[String, String] = item._2 match {
        case m: Map[String, String] => flattenMap(s"$prefix.${item._1}", m)
        case m: java.util.HashMap[String, AnyRef] => flattenMap(s"$prefix.${item._1}", m.asScala.toMap)
        case s => Map(s"$prefix.${item._1}" -> s.toString)
      }
      agg ++ mp
    })
  }

  val keys = flattenMap("parquet", items.toMap)
  val master = Option(System.getenv("SPARK_MASTER")).getOrElse("local[1]")
  val spark = keys.foldLeft(
    SparkSession.builder().appName("hello-app").master(master))((agg, kv) => agg.config(kv._1, kv._2)
  ).getOrCreate()

  spark.range(10).show(5, false)

  import spark.implicits._

  val projects = spark.createDataset(Seq(
    OpensourceProject("kafka", 2011, "java",
      Date.valueOf(LocalDate.of(2011, 1, 11))
    ),
    OpensourceProject("cassandra", 2008, "java",
      Date.valueOf(LocalDate.of(2008, 7, 12))
  ))).toDF().repartition(1)

  projects.show(5, false)
  val totalRecords = projects.count()
  println(s"total records ${totalRecords}")

  val bucketName = spark.conf.get("parquet.bucket.name")
  val csvPath = s"s3a://$bucketName/output/projects/csv/projects"
  val parquetPath = s"s3a://$bucketName/output/projects/parquet/projects"
  val parquetAppendPath = s"s3a://$bucketName/output/projects/parquet-append/projects"

  projects.write.option("header", "true").format("csv")
    .mode(SaveMode.Overwrite)
    .save(csvPath)


  projects.write.option("header", "true").format("parquet")
    .mode(SaveMode.Overwrite)
    .save(parquetPath)

  spark.read.format("parquet")
    .load(parquetPath)
    .write.format("parquet")
    .mode(SaveMode.Append)
    .save(parquetAppendPath)

  val totalAppend = spark.read.format("parquet")
    .load(parquetAppendPath)
    .count()

  println(s"total in append ${totalAppend}")

  spark.close()
}
