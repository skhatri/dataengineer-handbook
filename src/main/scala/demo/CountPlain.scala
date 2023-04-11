package demo

import com.datastax.oss.driver.api.core.CqlSession
import com.typesafe.config.ConfigFactory

import java.io.FileInputStream
import java.net.InetSocketAddress
import java.security.KeyStore
import javax.net.ssl.{SSLContext, TrustManagerFactory}

case class Country (name:String, code:String, latitude:BigDecimal, longitude:BigDecimal) extends Serializable {
  override def toString: String = s"$name,$code,$latitude,$longitude"
}

object CountPlain extends App {
  val config = ConfigFactory.load
  val contact = new InetSocketAddress(config.getString("connections.cassandra.host"), 9042)

  val sessionBuilder = CqlSession.builder().addContactPoint(contact)
    .withAuthCredentials(config.getString("connections.cassandra.username"), config.getString("connections.cassandra.password"))

   val session = (config.getBoolean("connections.cassandra.tls.enabled") match {
     case true => sessionBuilder.withSslContext(createContext(
       config.getString("connections.cassandra.tls.trustStorePath"),
       config.getString("connections.cassandra.tls.trustStorePassword")))
     case false => sessionBuilder
   }).withLocalDatacenter("dc1")
     .build()



  val bound = session.prepare("select name, code, latitude, longitude from countries.country").bind();

  val items = session.execute(bound).all()

  import scala.collection.JavaConverters._
  items.asScala.map(row => {
    Country(row.getString("name"), row.getString("code"), row.getBigDecimal("latitude"), row.getBigDecimal("longitude"))
  }).foreach(println)

 def createContext(trustPath:String, trustPass:String):SSLContext = {
    val truststoreFile = new FileInputStream (trustPath)
    val keystore = KeyStore.getInstance ("JKS")
    keystore.load(truststoreFile, trustPass.toCharArray)
    val tmf = TrustManagerFactory.getInstance ("SunX509")
    tmf.init (keystore)
    val sslContext = SSLContext.getInstance ("TLS")
    sslContext.init (null, tmf.getTrustManagers, null)
    sslContext
  }


  session.close()
}