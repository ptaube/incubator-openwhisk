package whisk.core.iam

import akka.actor.ActorSystem
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.SignedJWT
import pdi.jwt.{Jwt, JwtBase64, JwtOptions}
import spray.json._
import whisk.common.{AkkaLogging, Logging, Scheduler}

import scala.concurrent.duration.DurationInt

class IAMJWKService {
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

  def validateIAMToken(iamJWKCache: IAMJWKCache) = {

    val token =
      "eyJraWQiOiIyMDE3MTAzMC0wMDowMDowMCIsImFsZyI6IlJTMjU2In0.eyJpYW1faWQiOiJJQk1pZC0yNzAwMDZBQ0tWIiwiaWQiOiJJQk1pZC0yNzAwMDZBQ0tWIiwicmVhbG1pZCI6IklCTWlkIiwiaWRlbnRpZmllciI6IjI3MDAwNkFDS1YiLCJnaXZlbl9uYW1lIjoiU3RlZmZlbiIsImZhbWlseV9uYW1lIjoiUm9zdCIsIm5hbWUiOiJTdGVmZmVuIFJvc3QiLCJlbWFpbCI6InNyb3N0QGRlLmlibS5jb20iLCJzdWIiOiJzcm9zdEBkZS5pYm0uY29tIiwiYWNjb3VudCI6eyJic3MiOiIyYjg5OTRiZWZiYzhjMjQyYzRkZmZmMjc2YTJkMGRhMiIsImltcyI6IjE1OTAwMTkifSwiaWF0IjoxNTIyMjM5NTAwLCJleHAiOjE1MjIyNDMxMDAsImlzcyI6Imh0dHBzOi8vaWFtLmJsdWVtaXgubmV0L2lkZW50aXR5IiwiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwic2NvcGUiOiJvcGVuaWQiLCJjbGllbnRfaWQiOiJieCIsImFjciI6MSwiYW1yIjpbInB3ZCJdfQ.bUKjnODqWbH0qlUA-GTiIr_xGvluWqnHlu6SLqSYPt-WFnaWZfVAgm5luv1gLHq3igLajbrcS-AE2KKiFc1W4gvC52GGqM8XeWaa5W3AwiMCaV5hQRiWGU6H8PaNBADuV4qfayvv42qsv_LroDVWNk5QPUX_FLi7L58vqURgTS1-UCoHt9XAMkQzw7F_XOl5hk3Insa-y3ODQann8TV87Tm6XrCEeY9la1UGQn_HLb0m50o1ZUVLxWfsvAoeOLR3P_oCUBVsq2TxjaA9blGxUWQtz5NIoQvVqFQky7E4AP0UX3O0VeNqDO9_eaPwPUOK2-bIAjT72edei2SL6bFLdg"
    val token_expired =
      "eyJraWQiOiIyMDE3MTAzMC0wMDowMDowMCIsImFsZyI6IlJTMjU2In0.eyJpYW1faWQiOiJJQk1pZC0yNzAwMDZBQ0tWIiwiaWQiOiJJQk1pZC0yNzAwMDZBQ0tWIiwicmVhbG1pZCI6IklCTWlkIiwiaWRlbnRpZmllciI6IjI3MDAwNkFDS1YiLCJnaXZlbl9uYW1lIjoiU3RlZmZlbiIsImZhbWlseV9uYW1lIjoiUm9zdCIsIm5hbWUiOiJTdGVmZmVuIFJvc3QiLCJlbWFpbCI6InNyb3N0QGRlLmlibS5jb20iLCJzdWIiOiJzcm9zdEBkZS5pYm0uY29tIiwiYWNjb3VudCI6eyJic3MiOiIyYjg5OTRiZWZiYzhjMjQyYzRkZmZmMjc2YTJkMGRhMiIsImltcyI6IjE1OTAwMTkifSwiaWF0IjoxNTIyMTQ2NTE3LCJleHAiOjE1MjIxNTAxMTcsImlzcyI6Imh0dHBzOi8vaWFtLmJsdWVtaXgubmV0L2lkZW50aXR5IiwiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwic2NvcGUiOiJvcGVuaWQiLCJjbGllbnRfaWQiOiJieCIsImFjciI6MSwiYW1yIjpbInB3ZCJdfQ.R5aIENlyAvsEhVP4APrfzzYW70rDBhDknctGTEdUaIwGjPwiVbTpwrB9yry0LdB_D_69cC674rLweZNd2rQgetp3MNpdNEbLhCKRQXJumYM01yzFeH5CGeDJyZKLjqpxfekvOvkZ6qaIxCtVvpaiX0LvvBWaobyvNZpdDM62iXMA0Hhar4B6D-3wLybUT0bBNKt2Kni6wMLn4V5-xa1CLmcthOsnzTONbKb1ViexQ6icC3nKpCpeAXKXMGhdrnV0LqhHG6Y52ERMGg8fD-r3x0TV2CrsceCchP_A9qlp-nIrwn3IKsW3-DXRHJ4Qt8V527EcOhsvHneKj2tjg2DSFw"

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

      val publicKey = iamJWKCache.getPublicKey(kid.asInstanceOf[JsString].value)
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

object IAMJWKService {

  def main(args: Array[String]): Unit = {

    val cacheInterval = "30".toInt.seconds
    implicit val system = ActorSystem()
    implicit val executionContext = system.dispatcher
    implicit val logging: Logging = new AkkaLogging(akka.event.Logging.getLogger(system, this))

    // Monitor queue size from Kafka
    var iamJWKCache = new IAMJWKCache
    Scheduler.scheduleWaitAtMost(cacheInterval) { () =>
      iamJWKCache.refreshJWKCache().recover {
        case t =>
          println(s"IAM JWK Cache crashed with: ${t}")
          iamJWKCache = new IAMJWKCache
      }
    }

    new IAMJWKService().validateIAMToken(iamJWKCache)
  }
}
