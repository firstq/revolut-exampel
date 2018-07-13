package rest

import java.util.concurrent.{ConcurrentLinkedQueue, TimeUnit}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import entities.JsonProtocol
import persistence.entities.{Account, SimpleAccount, SimpleTransfer, Transfer}
import akka.http.scaladsl.model.StatusCodes._
import JsonProtocol._
import SprayJsonSupport._
import akka.http.scaladsl.server.ValidationRejection
import processing.TransfersExecutor
import slick.dbio.DBIOAction

import scala.concurrent.Future
import scala.concurrent.duration.Duration

class RoutesSpec extends AbstractRestTest {
  def actorRefFactory = system
  val modules = new Modules {}
  val accounts = new AccountRoutes(modules)
  val transfersExecutor = mock[TransfersExecutor]
  val transfers = new TransferRoutes(modules,transfersExecutor)

  "Account Routes" should {

    "return an empty array of accounts" in {
      val dbAction = DBIOAction.from(Future(None))
      modules.accountsDal.findOne(1) returns dbAction
      Get("/account/1") ~> accounts.routes ~> check {
        handled shouldEqual true
        status shouldEqual NotFound
      }
    }

    "return an empty array of accounts when ask for account Bad Request when the account is < 1" in {
      val dbAction = DBIOAction.from(Future(None))
      modules.accountsDal.findOne(1) returns dbAction
      Get("/account/0") ~> accounts.routes ~> check {
        handled shouldEqual false
        rejection shouldEqual ValidationRejection("The account id should be greater than zero", None)
      }
    }

    "return an array with 1 accounts" in {
      val dbAction = DBIOAction.from(Future(Some(Account(Some(1), "name", 1500))))
      modules.accountsDal.findOne(1) returns dbAction
      Get("/account/1") ~> accounts.routes ~> check {
        handled shouldEqual true
        status shouldEqual OK
        responseAs[Option[Account]].isEmpty shouldEqual false
      }
    }

    "create a account with the json in post" in {
      val dbAction = DBIOAction.from(Future.successful(Account(Some(1),"name 1",1500)))
      modules.accountsDal.save(Account(None,"name 1",1500)) returns dbAction
      Post("/account",SimpleAccount("name 1",1500)) ~> accounts.routes ~> check {
        handled shouldEqual true
        status shouldEqual Created
      }
    }

    "create a transfer betwen 2 accounts with the json in post" in {
      Post("/transfer",SimpleTransfer(1,BigDecimal(99.5),2)) ~> transfers.routes ~> check {
        handled shouldEqual true
        status shouldEqual Created
      }
    }

    "not handle the invalid json" in {
      Post("/account","{\"name\":\"1\"}") ~> accounts.routes ~> check {
        handled shouldEqual false
      }
    }

    "not handle an empty post" in {
      Post("/account") ~> accounts.routes ~> check {
        handled shouldEqual false
      }
    }

  }

}