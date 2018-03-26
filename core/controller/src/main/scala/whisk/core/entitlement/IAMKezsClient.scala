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
//import spray.json._
import com.nimbusds.jose.jwk._

import scala.concurrent.ExecutionContext.Implicits.global

class IAMKezsClient {

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
            println(s)
            keys = s
          case Failure(_) => println("Fehler")
        }
      case Failure(_) => println("Fehler")
    }

    println(s"sleeping ...")
    Thread.sleep(7000)

    /*
    response.onComplete {
      case Success(r) =>
        Unmarshal(r.entity).to[String].map { json =>
          println(json)
        }
      case Failure(_) => println("Fehler")
    }
     */

    val token =
      "eyJraWQiOiIyMDE3MTAzMC0wMDowMDowMCIsImFsZyI6IlJTMjU2In0.eyJpYW1faWQiOiJJQk1pZC0yNzAwMDZBQ0tWIiwiaWQiOiJJQk1pZC0yNzAwMDZBQ0tWIiwicmVhbG1pZCI6IklCTWlkIiwiaWRlbnRpZmllciI6IjI3MDAwNkFDS1YiLCJnaXZlbl9uYW1lIjoiU3RlZmZlbiIsImZhbWlseV9uYW1lIjoiUm9zdCIsIm5hbWUiOiJTdGVmZmVuIFJvc3QiLCJlbWFpbCI6InNyb3N0QGRlLmlibS5jb20iLCJzdWIiOiJzcm9zdEBkZS5pYm0uY29tIiwiYWNjb3VudCI6eyJic3MiOiIyYjg5OTRiZWZiYzhjMjQyYzRkZmZmMjc2YTJkMGRhMiIsImltcyI6IjE1OTAwMTkifSwiaWF0IjoxNTIyMDc0MDcxLCJleHAiOjE1MjIwNzc2NzEsImlzcyI6Imh0dHBzOi8vaWFtLmJsdWVtaXgubmV0L2lkZW50aXR5IiwiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwic2NvcGUiOiJvcGVuaWQiLCJjbGllbnRfaWQiOiJieCIsImFjciI6MSwiYW1yIjpbInB3ZCJdfQ.CzaKtOQc_Ng9uZM6ZsR969Q7kPTKwUvj9IDUakEahipBygigGNin12CWd7i8yj3cQvuwCM5NuinKaA1JBP2Zshcx606gUK3pkGhnKnkxBYej1z7d_RJ6Sa6q1UCV7fsBYZeR-ydvN2rPaXP9TXDGGgMishJ07XDOA7yZilnOoWMpLDPRXoTB65s4zueehN42-ZKfWqH4zkP8iolYjVJ8OXUvRJJwNI7idOsBCck-TpNk-hZ4KzDswZ7OZNVl-O5MNXWur7ZxouOaSUBqR1FKI4aL7-BjAZbXuX0Q3OQE8y7n8O5Z2PFAmFdSsTFfEcTzxEE59rDFe114joN_-T717Q"
    val token2 = "Ich.BinEinDummyBearer.Token"
    val key =
      "i55ngm-ipjWKZJosYsuMIAR7ef_UJ0eWeO1GNIsCwrr3-5wveyV6Lc9qs0eG4KaETXougfCcFdFuPL4ZM-OZ8sfgcypuwysSMYXkVgtb_omMckXJTORaRiH5b8j30UFEL4nahdKAPS3iJuVXgExhUmWOOKAzPNOtBPSHY61eidfYHa9OPA76cL2g1UjZ9jT14O2zJok7Kdp256PwyCjrDyJ_-rbdHT59C4_FWRj-2vejp55QBFxXJ7viarI8G00f4Rqvz3jDiKmBLK-XY0AWsYXv0I9bKmc3w4U-owy8BNcQN6HVUFKcRSq6bxu--MjpLbDfY4XGgND3NdbnBMxCxQ"
    val key2 =
      "i55ngmipjWKZJosYsuMIAR7ef"
    val key3 =
      """{"kty":"RSA","n":"pZ1Cllfua867Z3RuoYRM4IfweO7Hqy3gQhJPEnMYxPl8cVIqgtCZQXWravyBTAmBWgFeeeUwkQVNJd54GuBjs7ra-YiYcLUD0JZQDRLOE3bQRrJm0AH5wUPBa9kf1VW_NCCBzMMwMXryAYGHEeOk83bjym3V44XwADsUzjD4ssoJ8d439SGMcvibAO1JFdFQDNigzslylenwYCIPOUxQLhtn7UTvSSm8nOxb8aq1mRvnh8b47_AVeqRbP_DZJy0IYtU2KsJcmjP1oCRnv6pS8WpTVvrsIkxavPHaaQQXZHvqor-eS3J3QUG9Im7mbdb12og0GGIIX5VEHcIYyT3s5w","e":"AQAB","alg":"RS256","kid":"20171129"}"""
    val key3e =
      "eyJrdHkiOiJSU0EiLCJuIjoicFoxQ2xsZnVhODY3WjNSdW9ZUk00SWZ3ZU83SHF5M2dRaEpQRW5NWXhQbDhjVklxZ3RDWlFYV3JhdnlCVEFtQldnRmVlZVV3a1FWTkpkNTRHdUJqczdyYS1ZaVljTFVEMEpaUURSTE9FM2JRUnJKbTBBSDV3VVBCYTlrZjFWV19OQ0NCek1Nd01YcnlBWUdIRWVPazgzYmp5bTNWNDRYd0FEc1V6akQ0c3NvSjhkNDM5U0dNY3ZpYkFPMUpGZEZRRE5pZ3pzbHlsZW53WUNJUE9VeFFMaHRuN1VUdlNTbThuT3hiOGFxMW1Sdm5oOGI0N19BVmVxUmJQX0RaSnkwSVl0VTJLc0pjbWpQMW9DUm52NnBTOFdwVFZ2cnNJa3hhdlBIYWFRUVhaSHZxb3ItZVMzSjNRVUc5SW03bWJkYjEyb2cwR0dJSVg1VkVIY0lZeVQzczV3IiwiZSI6IkFRQUIiLCJhbGciOiJSUzI1NiIsImtpZCI6IjIwMTcxMTI5In0K"
    val key4 =
      """{
        |      "kty": "RSA",
        |      "n":    "aTU1bmdtLWlwaldLWkpvc1lzdU1JQVI3ZWZfVUowZVdlTzFHTklzQ3dycjMtNXd2ZXlWNkxjOXFzMGVHNEthRVRYb3VnZkNjRmRGdVBMNFpNLU9aOHNmZ2N5cHV3eXNTTVlYa1ZndGJfb21NY2tYSlRPUmFSaUg1YjhqMzBVRkVMNG5haGRLQVBTM2lKdVZYZ0V4aFVtV09PS0F6UE5PdEJQU0hZNjFlaWRmWUhhOU9QQTc2Y0wyZzFValo5alQxNE8yekpvazdLZHAyNTZQd3lDanJEeUpfLXJiZEhUNTlDNF9GV1JqLTJ2ZWpwNTVRQkZ4WEo3dmlhckk4RzAwZjRScXZ6M2pEaUttQkxLLVhZMEFXc1lYdjBJOWJLbWMzdzRVLW93eThCTmNRTjZIVlVGS2NSU3E2Ynh1LS1NanBMYkRmWTRYR2dORDNOZGJuQk14Q3hRCg==",
        |      "e": "QVFBQgo=",
        |      "alg": "RS256",
        |      "kid": "20171030-00:00:00"
        |}"""

    println(s"keys: $keys")

    val ttoken = Jwt.encode("""{"user":1}""", "secretKey", JwtAlgorithm.HS256)
    println(s"ttoken: ${ttoken}")
    val iisvalid = Jwt.isValid(ttoken, "secretKey", Seq(JwtAlgorithm.HS256))
    println(s"iisvalid: ${iisvalid}")
    Jwt.validate(ttoken, "secretKey", Seq(JwtAlgorithm.HS256))
    println(s"token validated ...")

    val publickeys = JWKSet.parse(keys)
    val iter = publickeys.getKeys().iterator()
    while (iter.hasNext()) {
      val keyy = iter.next()
      val alg = keyy.getAlgorithm
      val keyid = keyy.getKeyID
      val keyyjson = keyy.toJSONObject
      val keyyn = keyyjson.get("n")
      println(s"keyid: ${keyid.toString}")
      println(s"alg: ${alg.toString}")
      println(s"keyy: $keyy")
      println(s"keyyjson: ${keyyjson}")
      println(s"keyyn: ${keyyn}")

      val keyynn =
        "cFoxQ2xsZnVhODY3WjNSdW9ZUk00SWZ3ZU83SHF5M2dRaEpQRW5NWXhQbDhjVklxZ3RDWlFYV3JhdnlCVEFtQldnRmVlZVV3a1FWTkpkNTRHdUJqczdyYS1ZaVljTFVEMEpaUURSTE9FM2JRUnJKbTBBSDV3VVBCYTlrZjFWV19OQ0NCek1Nd01YcnlBWUdIRWVPazgzYmp5bTNWNDRYd0FEc1V6akQ0c3NvSjhkNDM5U0dNY3ZpYkFPMUpGZEZRRE5pZ3pzbHlsZW53WUNJUE9VeFFMaHRuN1VUdlNTbThuT3hiOGFxMW1Sdm5oOGI0N19BVmVxUmJQX0RaSnkwSVl0VTJLc0pjbWpQMW9DUm52NnBTOFdwVFZ2cnNJa3hhdlBIYWFRUVhaSHZxb3ItZVMzSjNRVUc5SW03bWJkYjEyb2cwR0dJSVg1VkVIY0lZeVQzczV3Cg"

      //val keyasstring_e = Jwt.encode(keyasstring)
      val isvalid = Jwt.isValid(token, keyyn.toString, Seq(JwtAlgorithm.RS256))
      println(isvalid)
      Jwt.validate(token, keyynn.toString, Seq(JwtAlgorithm.RS256))
      //Jwt.validate(token, keyasstring_e2, Seq(JwtAlgorithm.RS256))
      //val validate = Jwt.validate(token, key4, Seq(JwtAlgorithm.RS256))
    }

    /*    publickeys.getKeys.forEach {
      case (k) =>
        println(k.toString())
      //Jwt.validate(token, k.toString(), Seq(JwtAlgorithm.RS256))
    }
     */
    //val validate = Jwt.isValid(token, key, Seq(JwtAlgorithm.RS256))
    //val validate = Jwt.validate(token, key4, Seq(JwtAlgorithm.RS256))
    //println(s"validate: $validate")

    //val tokend = Jwt.decode(token, JwtOptions(signature = false));
    //println(s"tokend: $tokend")

  }

}

object IAMKezsClient {

  def main(args: Array[String]): Unit = {
    println("hallo")
    new IAMKezsClient().fetchKezs()
    println("hallo3")

  }
}
