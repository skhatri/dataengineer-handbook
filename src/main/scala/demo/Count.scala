package demo

import com.datastax.oss.driver.api.core.CqlSessionBuilder
import org.apache.spark.sql.{SaveMode, SparkSession}
import com.datastax.spark.connector._
import com.typesafe.config.ConfigFactory
import org.apache.spark.{SparkConf, SparkEnv}

import scala.collection.mutable

//read from s3
//write to cassandra
//enable tls 1.2
object Count extends App {

  import org.apache.hadoop.security.UserGroupInformation

  UserGroupInformation.setLoginUser(UserGroupInformation.createRemoteUser("root"))


  val config = ConfigFactory.load

  import scala.collection.JavaConverters._

  val items: mutable.Map[String, AnyRef] = config.getObject("spark").unwrapped().asScala

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

  val keys = flattenMap("spark", items.toMap)
  val master = Option(System.getenv("SPARK_MASTER")).getOrElse("local[1]")
  val spark = keys.foldLeft(
    SparkSession.builder().appName("hello-app").master(master))((agg, kv) => agg.config(kv._1, kv._2)
  ).getOrCreate()


  /*

  key exchange, auth, encryption, encryption_mode, mac

  TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384

   */
  spark.range(10).show(5, false)

  import spark.implicits._

  val country = spark.sparkContext.cassandraTable("countries", "country")

  val countryDf = country.map(row => Country(row.getString("name"),
    row.getString("code"), row.getDecimal("latitude"), row.getDecimal("longitude")))
    .toDF()
    .repartition(1)

  countryDf.show(5, false)
  val totalRecords = countryDf.count()
  println(s"total records ${totalRecords}")

  countryDf.write.option("header", "true").format("csv")
    .mode(SaveMode.Overwrite)
    .save("s3a://spark-job-build/output/csv/countries.csv")

  countryDf.write.option("header", "true").format("csv")
    .mode(SaveMode.Overwrite)
    .save("s3a://spark-job-cql/output/csv/countries.csv")

  countryDf.write.format("parquet")
    .mode(SaveMode.Overwrite)
    .save("s3a://spark-job-build/output/parquet/countries")

  countryDf.write.option("header", "true").format("parquet")
    .mode(SaveMode.Overwrite)
    .save("s3a://spark-job-cql/output/parquet/countries")



  spark.close()
}
