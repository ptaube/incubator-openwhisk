package whisk.core.entitlement

import akka.actor.ActorSystem

import scala.util.{Failure, Success}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.ExecutionContext.Implicits.global


class IAMKezsClient {

  def fetchKezs() = {

    implicit val actorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val url = Uri("https://iam.ng.bluemix.net/identity/keys")

    val baseHeaders: List[HttpHeader] =
      List(Accept(MediaTypes.`application/json`))

    val request = HttpRequest(
      method = GET,
      uri = url,
      baseHeaders)

    val response = Http().singleRequest(request)

    response.onComplete {
      case Success(r) => Unmarshal(r.entity).to[String].map { json =>
        println(json)
      }
      case Failure(_) => println("Fehler")
    }

  }

}

object IAMKezsClient {

  def main(args: Array[String]): Unit = {
    println("hallo")
    new IAMKezsClient().fetchKezs()
    println("hallo3")

  }
}