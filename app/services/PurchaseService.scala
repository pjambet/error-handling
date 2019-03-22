package services

import io.github.hamsters.FutureEither
import io.github.hamsters.MonadTransformers._
import javax.inject.{Inject, Singleton}
import services.PurchaseService.{
  PurchaseCreated,
  PurchaseCreationResult,
  PurchaseError,
  PurchaseFailed
}
import stripe.StripeApiClient
import stripe.StripeApiClient.{
  CardError,
  ChargeCreated,
  ChargeCreationResult,
  StripeSource
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

@Singleton
class PurchaseService @Inject()(stripeApiClient: StripeApiClient,
                                inventoryService: InventoryService) {

  def createCharge_async(source: StripeSource): Future[ChargeCreationResult] = {
    stripeApiClient.createCharge_async(source)
  }

  def createCharge_sync(source: StripeSource): ChargeCreationResult = {
    try {

      val result = stripeApiClient.createCharge_sync(source)
      result match {
        case ChargeCreated => ???
      }
    } catch {
      case e =>
        ???
    }
  }

  def createPurchaseAndReserveInventory_async(
      source: StripeSource): Future[PurchaseCreationResult] = {

    Future {
      createPurchaseAndReserveInventory_sync(source)
    }
  }

  def createPurchaseAndReserveInventory_sync(
      source: StripeSource): PurchaseCreationResult = {

    stripeApiClient.createCharge_sync(source) match {
      case CardError => PurchaseFailed("card_error")
      case ChargeCreated =>
        inventoryService.reserveInventory_sync()
        PurchaseCreated
    }
  }

  def createPurchaseAndReserveInventory_async_either(
      source: StripeSource): Future[Either[String, PurchaseCreated.type]] = {

    val f = stripeApiClient.createCharge_async_either(source)

    f.map { either =>
      either.map { _ =>
        inventoryService.reserveInventory_sync()
        PurchaseCreated
      }
//      case Left(error) => Left(error)
//      case Right(_) =>
//        inventoryService.reserveInventory_sync()
//        Right(PurchaseCreated)
    }
  }

  def createPurchaseAndReserveInventory_async_either2(
      source: StripeSource): Future[Either[String, PurchaseCreated.type]] = {

    FutureEither(stripeApiClient.createCharge_async_either(source)).map { _ =>
      inventoryService.reserveInventory_sync()
      PurchaseCreated
    }
  }

  def createPurchaseAndReserveInventory_async_either3(source: StripeSource)
    : Future[Either[PurchaseFailed, PurchaseCreated.type]] = {

    stripeApiClient.createCharge_async_either2(source).map { either =>
//      either.map { _ =>
//        inventoryService.reserveInventory_sync()
//        PurchaseCreated
//      }
      either match {
        case Left(_: CardError.type) => Left(PurchaseFailed("error"))
        case Right(_) =>
          inventoryService.reserveInventory_sync()
          Right(PurchaseCreated)
      }
    }
  }

  def createPurchaseAndReserveInventory_either(
      source: StripeSource): Either[String, PurchaseCreated.type] = {

    stripeApiClient.createCharge_sync_either(source).map { _ =>
      inventoryService.reserveInventory_sync()
      PurchaseCreated
    }
  }

}

object PurchaseService {
  sealed trait PurchaseCreationResult

  final case object PurchaseCreated extends PurchaseCreationResult
  final case class PurchaseFailed(reason: String) extends PurchaseCreationResult

  final case class PurchaseError(message: String)
}
