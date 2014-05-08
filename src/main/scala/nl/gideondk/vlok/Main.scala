package nl.gideondk.vlok

import nl.gideondk.vlok.server.VlokServer
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.slf4j.LazyLogging

object Main extends LazyLogging {

  def main(args: Array[String]) = {
    sys.addShutdownHook({
      logger.info("Vlok shutdown")
    })

    val config = ConfigFactory.load()
    val networkInterface = config.getString("vlok.network-interface")
    val port = config.getInt("vlok.port")
    implicit val serverSystem = ActorSystem("server-system")

    logger.info(s"Starting vlok on port: ${port}, using interface: ${networkInterface}")
    try {
      val server = VlokServer(networkInterface)
      server.start(port)
    } catch {
      case e: Throwable ⇒
        println(e.getMessage + "\n")
        serverSystem.shutdown()
        logger.info(s"Vlok terminated: ${e.getMessage}")
    }
  }
}
