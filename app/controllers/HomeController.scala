package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import services.PurchaseService
import services.PurchaseService.{PurchaseCreated, PurchaseFailed}
import stripe.StripeApiClient.{CardError, ChargeCreated, StripeSource}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(
    cc: ControllerComponents,
    purchaseService: PurchaseService
) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def createPurchase_sync(source: String) = Action {
    implicit request: Request[AnyContent] =>
      purchaseService
        .createPurchaseAndReserveInventory_sync(StripeSource(source)) match {
        case PurchaseCreated        => Created("")
        case PurchaseFailed(reason) => UnprocessableEntity(reason)
      }
  }

  def createPurchase(source: String) = Action.async {
    implicit request: Request[AnyContent] =>
      purchaseService
        .createPurchaseAndReserveInventory_async(StripeSource(source))
        .map {
          case PurchaseCreated        => Created("")
          case PurchaseFailed(reason) => UnprocessableEntity(reason)
        }
  }

  def createPurchase_async_either(source: String) = Action.async {
    implicit request: Request[AnyContent] =>
      purchaseService
        .createPurchaseAndReserveInventory_async_either(StripeSource(source))
        .map {
          case Right(PurchaseCreated) => Created("")
          case Left(reason)           => UnprocessableEntity(reason)
        }
  }

  def createPurchase_async_either2(source: String) = Action.async {
    implicit request: Request[AnyContent] =>
      purchaseService
        .createPurchaseAndReserveInventory_async_either2(StripeSource(source))
        .map {
          case Right(PurchaseCreated) => Created("")
          case Left(reason)           => UnprocessableEntity(reason)
        }
  }

  def createPurchase_either(source: String) = Action {
    implicit request: Request[AnyContent] =>
      purchaseService
        .createPurchaseAndReserveInventory_either(StripeSource(source)) match {
        case Right(PurchaseCreated) => Created("")
        case Left(reason)           => UnprocessableEntity(reason)
      }
  }
}
