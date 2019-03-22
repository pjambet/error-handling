package services

import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class InventoryService @Inject()() {

  def reserveInventory(): Future[Unit] = {
    Future {
      ()
    }
  }

  def reserveInventory_sync(): Unit = ()

}
