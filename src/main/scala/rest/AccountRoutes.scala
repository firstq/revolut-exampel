package rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import entities.JsonProtocol._
import io.swagger.annotations._
import javax.ws.rs.Path
import persistence.entities.{Account, SimpleAccount}
import utils.{ActorModule, Configuration, DbModule, PersistenceModule}

import scala.util.{Failure, Success}

@Path("/account")
@Api(value = "/account", produces = "application/json")
class AccountRoutes(modules: Configuration with PersistenceModule with DbModule with ActorModule)  extends Directives {
  import modules.executeOperation
  import modules.system.dispatcher

  @Path("/{id}")
  @ApiOperation(value = "Return Account", notes = "", nickname = "", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Account Id", required = false, dataType = "int", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Account", response = classOf[Account]),
    new ApiResponse(code = 400, message = "The account id should be greater than zero"),
    new ApiResponse(code = 404, message = "Return Account Not Found"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def accountGetRoute = path("account" / IntNumber) { (supId) =>
    get {
      validate(supId > 0,"The account id should be greater than zero") {

        onComplete(modules.accountsDal.findOne(supId)) {
          case Success(accountOpt) => accountOpt match {
            case Some(sup) => complete(sup)
            case None => complete(NotFound, s"The account doesn't exist")
          }
          case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }

  @ApiOperation(value = "Add Account", notes = "", nickname = "", httpMethod = "POST", produces = "text/plain")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "Account Object", required = true,
      dataType = "persistence.entities.SimpleAccount", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 400, message = "Bad Request"),
    new ApiResponse(code = 201, message = "Entity Created")
  ))
 def accountPostRoute = path("account") {
    post {
      entity(as[SimpleAccount]) { accountToInsert =>
          onComplete(modules.accountsDal.save(Account(None, accountToInsert.name, accountToInsert.balance))) {
          case Success(_) => complete(Created)
          case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }

  val routes: Route = accountPostRoute ~ accountGetRoute

}

