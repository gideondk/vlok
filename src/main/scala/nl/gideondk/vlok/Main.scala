package nl.gideondk.vlok

import nl.gideondk.vlok.server.VlokServer
import akka.actor.ActorSystem

object Main {
  def main(args: Array[String]) = {
    if (args.length < 2) {
      println("Usage: ./sbt \"run interface port\"")
    } else {
      implicit val serverSystem = ActorSystem("server-system")
      try {
        val networkInterface = args(0)
        val port = args(1).toInt
        val server = VlokServer(networkInterface)
        server.start(port)
      } catch {
        case e: Throwable ⇒
          println(e.getMessage + "\n")
          serverSystem.shutdown()
      }
    }
  }
}