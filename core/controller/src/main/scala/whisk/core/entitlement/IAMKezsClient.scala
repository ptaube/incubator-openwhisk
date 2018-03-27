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
import pdi.jwt._
import spray.json._
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jose.jwk._
import com.nimbusds.jose.crypto._
//import java.nio.Buffer

import scala.concurrent.ExecutionContext.Implicits.global
//import scala.util.parsing.json._

class IAMKezsClient {

  // val (header64, header, claim64, claim, signature) = mysplitToken(token)
  def mysplitToken(token: String): (String, String, String, String, String) = {
    val parts = token.split("\\.")

    val signature = parts.length match {
      case 2 => ""
      case 3 => parts(2)
      //case _ => throw new JwtLengthException(s"Expected token [$token] to be composed of 2 or 3 parts separated by dots.")
    }

    (parts(0), JwtBase64.decodeString(parts(0)), parts(1), JwtBase64.decodeString(parts(1)), signature)

  }

  def fetchKezs() = {

    implicit val actorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val url = Uri("https://iam.ng.bluemix.net/identity/keys")

    val baseHeaders: List[HttpHeader] =
      List(Accept(MediaTypes.`application/json`))

    val request = HttpRequest(method = GET, uri = url, baseHeaders)

    val response = Http().singleRequest(request)
    var keys: String = ""

    response.onComplete {
      case Success(r) =>
        Unmarshal(r.entity).to[String].onComplete {
          case Success(s) =>
            //println(s)
            keys = s
          case Failure(_) => println("Fehler")
        }
      case Failure(_) => println("Fehler")
    }

    println(s"sleeping ...")
    Thread.sleep(7000)

    val token =
      "eyJraWQiOiIyMDE3MTAzMC0wMDowMDowMCIsImFsZyI6IlJTMjU2In0.eyJpYW1faWQiOiJJQk1pZC0yNzAwMDZBQ0tWIiwiaWQiOiJJQk1pZC0yNzAwMDZBQ0tWIiwicmVhbG1pZCI6IklCTWlkIiwiaWRlbnRpZmllciI6IjI3MDAwNkFDS1YiLCJnaXZlbl9uYW1lIjoiU3RlZmZlbiIsImZhbWlseV9uYW1lIjoiUm9zdCIsIm5hbWUiOiJTdGVmZmVuIFJvc3QiLCJlbWFpbCI6InNyb3N0QGRlLmlibS5jb20iLCJzdWIiOiJzcm9zdEBkZS5pYm0uY29tIiwiYWNjb3VudCI6eyJic3MiOiIyYjg5OTRiZWZiYzhjMjQyYzRkZmZmMjc2YTJkMGRhMiIsImltcyI6IjE1OTAwMTkifSwiaWF0IjoxNTIyMTcwMTA5LCJleHAiOjE1MjIxNzM3MDksImlzcyI6Imh0dHBzOi8vaWFtLmJsdWVtaXgubmV0L2lkZW50aXR5IiwiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwic2NvcGUiOiJvcGVuaWQiLCJjbGllbnRfaWQiOiJieCIsImFjciI6MSwiYW1yIjpbInB3ZCJdfQ.ipom6k_BMSAYisXdPdJDT-kRJ7A1yX9dVYT1Rfpe__g5MCewu4rvwSGPuPdmS87E3w6hYAbkWwnsXYliohYJD1LRz2A8Dqm1WY0_wH0esfPAVVVQ94MCBnpO8PWcpR0bkw7vntCu3OGeXBzNYfu6WBEgbTr71XQ1K4ZQ0Y0QgnKDVPs7aQYICJG_seEkHjYrshuqeX-YRyZOOF5kEbvlaJW9u7C3VFPb5qNDohdtrS-W8IkK1xLSDB8W7EHA1kq6F6mHwDWCL0pk2XvO_I3g_gg4jMYSHXbUNRby742z1nIumyZJOAawwoSRTz3q8ZKq_-Qa9BTEPKwzm5FPOBYRqw"
    val token_expired =
      "eyJraWQiOiIyMDE3MTAzMC0wMDowMDowMCIsImFsZyI6IlJTMjU2In0.eyJpYW1faWQiOiJJQk1pZC0yNzAwMDZBQ0tWIiwiaWQiOiJJQk1pZC0yNzAwMDZBQ0tWIiwicmVhbG1pZCI6IklCTWlkIiwiaWRlbnRpZmllciI6IjI3MDAwNkFDS1YiLCJnaXZlbl9uYW1lIjoiU3RlZmZlbiIsImZhbWlseV9uYW1lIjoiUm9zdCIsIm5hbWUiOiJTdGVmZmVuIFJvc3QiLCJlbWFpbCI6InNyb3N0QGRlLmlibS5jb20iLCJzdWIiOiJzcm9zdEBkZS5pYm0uY29tIiwiYWNjb3VudCI6eyJic3MiOiIyYjg5OTRiZWZiYzhjMjQyYzRkZmZmMjc2YTJkMGRhMiIsImltcyI6IjE1OTAwMTkifSwiaWF0IjoxNTIyMTQ2NTE3LCJleHAiOjE1MjIxNTAxMTcsImlzcyI6Imh0dHBzOi8vaWFtLmJsdWVtaXgubmV0L2lkZW50aXR5IiwiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwic2NvcGUiOiJvcGVuaWQiLCJjbGllbnRfaWQiOiJieCIsImFjciI6MSwiYW1yIjpbInB3ZCJdfQ.R5aIENlyAvsEhVP4APrfzzYW70rDBhDknctGTEdUaIwGjPwiVbTpwrB9yry0LdB_D_69cC674rLweZNd2rQgetp3MNpdNEbLhCKRQXJumYM01yzFeH5CGeDJyZKLjqpxfekvOvkZ6qaIxCtVvpaiX0LvvBWaobyvNZpdDM62iXMA0Hhar4B6D-3wLybUT0bBNKt2Kni6wMLn4V5-xa1CLmcthOsnzTONbKb1ViexQ6icC3nKpCpeAXKXMGhdrnV0LqhHG6Y52ERMGg8fD-r3x0TV2CrsceCchP_A9qlp-nIrwn3IKsW3-DXRHJ4Qt8V527EcOhsvHneKj2tjg2DSFw"

    println(s"keys: $keys")

    val (header64, header, claim64, claim, signature) = mysplitToken(token)
    val headerj = header.parseJson
    val kid = headerj.asJsObject().fields("kid")
    //println(s"header64: ${header64}")
    println(s"header: ${header}")
    //println(s"claim64: ${claim64}")
    println(s"claim: ${claim}")
    println(s"kid: ${kid}")
    println(s"signature: ${signature}")

    val tokend = Jwt.decode(token, JwtOptions(signature = false))
    if (tokend.isFailure) {
      println(s"token not valid: ${tokend}")
    } else {

      println(s"tokend: ${tokend.get}")

      val tokenj = tokend.get.parseJson
      println(s"tokenj: $tokenj")

      val bss = tokenj.asJsObject().fields("account").asJsObject().fields("bss")
      println(s"bss: $bss")

      val publickeys = JWKSet.parse(keys)

      //val kidasstring = kid.toString()
      //val kidasstring = kid.asInstanceOf[JsString].value

      val publicKey = publickeys.getKeyByKeyId(kid.asInstanceOf[JsString].value).asInstanceOf[RSAKey].toRSAPublicKey
      //println(s"publicKey: $publicKey")

      val cSignedJWT = SignedJWT.parse(token)
      val verifier = new RSASSAVerifier(publicKey)
      println(s"verifying token ...")
      println(s" ... ${cSignedJWT.verify(verifier)}")

      println(s"validating (isValid() ...")
      val isValid = Jwt.isValid(token, publicKey, JwtOptions(signature = true))
      println(s" ... ${isValid}")

      println(s"validating (validate() ...")
      //val validatevar = Jwt.validate(token, publicKey, JwtOptions(signature = true))
      println(s" ... ${Jwt.validate(token, publicKey, JwtOptions(signature = true))}")
    }
  }
}

object IAMKezsClient {

  def main(args: Array[String]): Unit = {
    new IAMKezsClient().fetchKezs()
  }
}
