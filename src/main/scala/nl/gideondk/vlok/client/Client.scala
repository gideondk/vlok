package nl.gideondk.vlok.client

import nl.spotdog.bark.client._
import nl.spotdog.bark.protocol._
import ETF._
import scalaz._
import Scalaz._
import nl.gideondk.sentinel.Task
import akka.actor.ActorSystem

class VlokClient(host: String, port: Int, numberOfWorkers: Int = 4)(implicit system: ActorSystem) {
  def generateID: Task[BigInt] = ((vlokService |/| "generateID") <<? ()).as[BigInt]

  private val vlokService = BarkClient(host, port, numberOfWorkers, "Vlok Client") |?| "vlok"
}

object VlokClient {
  def apply(host: String, port: Int, numberOfWorkers: Int = 4)(implicit system: ActorSystem) = new VlokClient(host, port, numberOfWorkers)
}