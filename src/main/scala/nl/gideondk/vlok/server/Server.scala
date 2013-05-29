package nl.gideondk.vlok.server

import nl.spotdog.bark.server._
import nl.spotdog.bark.protocol._
import ETF._

import scalaz._
import Scalaz._
import effect._

import BarkServerModule._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._
import akka.actor._
import akka.util.Timeout
import akka.pattern.ask
import akka.routing._

import java.net.{ NetworkInterface, InetAddress }

class VlokServer(interface: Long)(implicit system: ActorSystem) extends BarkRouting {
  implicit val timeout = Timeout(5 second)

  val generator = system.actorOf(Props(new VlokGenerator(interface)))

  val modules = module("vlok") {
    call("generateID")(() ⇒ (generator ? GenerateID).mapTo[BigInt])
  }

  private val barkServer = BarkServer("Vlok Server")(modules)

  def start(port: Int) = barkServer.run(port)
  def stop = barkServer.stop

}

object VlokServer {
  def apply(interface: String)(implicit system: ActorSystem) = new VlokServer(macAddressAsLong(interface))

  def macAddressAsLong(interface: String) = try {
    val mac = NetworkInterface.getByName(interface).getHardwareAddress
    (for (i ← 0 to mac.length - 1) yield ((mac(i).toLong & 0xff) << (mac.length - i.toLong - 1L) * 8L)).foldLeft(0: Long) { (b: Long, a: Long) ⇒ b | a }
  } catch {
    case e: Throwable ⇒ throw new Exception("Please check if the supplied interface: '" + interface + "' is correct and available.")
  }
}