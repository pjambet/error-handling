package stripe

import javax.inject.{Inject, Singleton}
import stripe.StripeApiClient._

import scala.concurrent.Future

@Singleton
class StripeApiClient @Inject()() {

//  @throws(classOf[InvalidCredentials])
  def createCharge_async(source: StripeSource): Future[ChargeCreationResult] = {
    source match {
      case StripeSource("valid")   => Future.successful(ChargeCreated)
      case StripeSource("invalid") => Future.successful(CardError)
      case _ =>
        Future.failed(
          new Exception(s"Unknown error with stripe source: $source"))
    }
  }

  def createCharge_sync(source: StripeSource): ChargeCreationResult = {
    source match {
      case StripeSource("valid")   => ChargeCreated
      case StripeSource("invalid") => CardError
      case _ =>
        throw new Exception(s"Unknown error with stripe source: $source")
    }
  }

  def createCharge_async_either(
      source: StripeSource): Future[Either[String, ChargeCreated.type]] = {
    source match {
      case StripeSource("valid")   => Future.successful(Right(ChargeCreated))
      case StripeSource("invalid") => Future.successful(Left("card_error"))
      case _ =>
        Future.failed(
          new Exception(s"Unknown error with stripe source: $source"))
    }
  }

  def createCharge_async_either2(source: StripeSource)
    : Future[Either[CardError.type, ChargeCreated.type]] = {
    source match {
      case StripeSource("valid")   => Future.successful(Right(ChargeCreated))
      case StripeSource("invalid") => Future.successful(Left(CardError))
      case _ =>
        Future.failed(
          new Exception(s"Unknown error with stripe source: $source"))
    }
  }

  def createCharge_sync_either(
      source: StripeSource): Either[String, ChargeCreated.type] = {
    source match {
      case StripeSource("valid")   => Right(ChargeCreated)
      case StripeSource("invalid") => Left("card_error")
      case _ =>
        throw new Exception(s"Unknown error with stripe source: $source")
    }
  }

}

object StripeApiClient {
  sealed trait ChargeCreationResult

  case object ChargeCreated extends ChargeCreationResult
  case object CardError extends ChargeCreationResult

  class CardException extends Throwable

  final case class StripeSource(value: String)

//  final class InvalidCredentials extends Throwable
}
