package nl.gideondk.vlok.client

import nl.gideondk.nucleus._
import nl.gideondk.nucleus.protocol._
import ETF._
import scalaz._
import Scalaz._
import nl.gideondk.sentinel.Task
import akka.actor.ActorSystem

class VlokClient(host: String, port: Int, numberOfWorkers: Int = 4)(implicit system: ActorSystem) {
  def generateID: Task[BigInt] = ((vlokService |/| "generateID") ? ()).as[BigInt]

  private val vlokService = Client(host, port, numberOfWorkers, "Vlok Client") |?| "vlok"
}

object VlokClient {
  def apply(host: String, port: Int, numberOfWorkers: Int = 4)(implicit system: ActorSystem) = new VlokClient(host, port, numberOfWorkers)
}