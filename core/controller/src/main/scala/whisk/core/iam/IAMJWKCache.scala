package whisk.core.iam

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, MediaTypes, Uri}
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.nimbusds.jose.jwk.JWKSet

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

//import spray.json._

//import com.nimbusds.jose.crypto._
import com.nimbusds.jose.jwk._

object IAMJWKCache {
  //val requiredProperties = WhiskConfig.kafkaHosts ++ WhiskConfig.zookeeperHosts ++ WhiskConfig.invokerHosts
}

//class KafkaQueueSizeObserver(implicit val whiskConfig: WhiskConfig, implicit val statsd: NonBlockingStatsDClient) {
class IAMJWKCache(implicit val executionContext: ExecutionContextExecutor) {

  private var keys: JWKSet = new JWKSet()
  private val iam_jwk_url = Uri("https://iam.ng.bluemix.net/identity/keys")

  def getPublicKey(kid: String) = {

    while (keys.getKeys.size() == 0) {
      println(s"sleeping for 1 sec ...")
      Thread.sleep(1000)
    }

    //val publicKey = keys.getKeyByKeyId(kid.asInstanceOf[JsString].value).asInstanceOf[RSAKey].toRSAPublicKey
    val publicKey = keys.getKeyByKeyId(kid).asInstanceOf[RSAKey].toRSAPublicKey
    println(s"found public key: $publicKey")
    publicKey
  }

  def refreshJWKCache(): Future[Unit] = {

    implicit val actorSystem = ActorSystem()
    //implicit val executionContext = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    Future.fromTry(Try {
      println(s"refreshing cache ...")

      val baseHeaders: List[HttpHeader] =
        List(Accept(MediaTypes.`application/json`))

      val request = HttpRequest(method = GET, uri = iam_jwk_url, baseHeaders)

      val response = Http().singleRequest(request)

      response.onComplete {
        case Success(r) =>
          Unmarshal(r.entity).to[String].onComplete {
            case Success(s) =>
              println(s"iam jwk payload received: $s")
              val keys_new = JWKSet.parse(s)
              keys = keys_new
              println(s"cache refreshed ...")
            case Failure(e) => println(s"unmarshal failed with error: $e")
          }
        case Failure(e) => println(s"iam returned error: $e")
      }
    })
  }
}
