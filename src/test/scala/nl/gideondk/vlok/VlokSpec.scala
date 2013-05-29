package nl.gideondk.vlok

import org.specs2.mutable.Specification
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import nl.gideondk.vlok.client._
import nl.gideondk.vlok.server._

import scalaz._
import Scalaz._

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

import nl.gideondk.sentinel.Task._
import nl.gideondk.sentinel._

class VlokSpec extends Specification {
  sequential

  lazy val (client, server) = {
    val serverSystem = ActorSystem("server-system")
    val server = VlokServer("en0")(serverSystem)
    server.start(9999)
    Thread.sleep(1000)
    val clientSystem = ActorSystem("client-system")
    val client = VlokClient("localhost", 9999, 32)(clientSystem)
    (client, server)
  }

  "Vloks" should {
    "should be able to be generated correctly" in {
      implicit val timeout = Duration(20, SECONDS)
      client.generateID.copoint > 0
    }

    "should be unique" in {
      implicit val timeout = Duration(20, SECONDS)
      val tasks = for (i ← 0 to 5000) yield client.generateID
      val ids = Task.sequence(tasks.toList).copoint
      ids.length == ids.distinct.length
    }

    "should be timely ordered" in {
      val system = ActorSystem("direct-test-system")
      val generator = system.actorOf(Props(new VlokGenerator(123)))

      implicit val timeout = Timeout(Duration(20, SECONDS))
      val tasks = for (i ← 0 to 5000) yield (generator ? GenerateID).mapTo[BigInt]
      val ids = Await.result(Future.sequence(tasks), timeout.duration)
      var allSorted = true
      for (i ← 0 to ids.length - 2) allSorted = ids(i) < ids(i + 1)
      allSorted
    }
  }
}
