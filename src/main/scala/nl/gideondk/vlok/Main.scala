package nl.gideondk.vlok

import nl.gideondk.vlok.server.VlokServer
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]) = {
    val config = ConfigFactory.load()
    val networkInterface = config.getString("vlok.network-interface")
    val port = config.getInt("vlok.port")
    implicit val serverSystem = ActorSystem("server-system")

    try {
      val server = VlokServer(networkInterface)
      server.start(port)
    } catch {
      case e: Throwable ⇒
        println(e.getMessage + "\n")
        serverSystem.shutdown()
    }
  }
}
