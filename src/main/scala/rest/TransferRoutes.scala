package rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import entities.JsonProtocol._
import io.swagger.annotations._
import javax.ws.rs.Path
import persistence.entities.{SimpleTransfer, Transfer}
import processing.TransfersExecutor
import utils.{ActorModule, Configuration, DbModule, PersistenceModule}

import scala.util.{Failure, Success}

@Path("/transfer")
@Api(value = "/transfer", produces = "application/json")
class TransferRoutes(modules: Configuration with PersistenceModule with DbModule with ActorModule, transferExecutor: TransfersExecutor)  extends Directives {
  import modules.executeOperation
  import modules.system.dispatcher

  @Path("/{id}")
  @ApiOperation(value = "Return Transfer", notes = "", nickname = "", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Transfer Id", required = false, dataType = "int", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Transfer", response = classOf[Transfer]),
    new ApiResponse(code = 400, message = "The transfer id should be greater than zero"),
    new ApiResponse(code = 404, message = "Return Transfer Not Found"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def transferGetRoute = path("transfer" / IntNumber) { (supId) =>
    get {
      validate(supId > 0,"The transfer id should be greater than zero") {
        onComplete(modules.transfersDal.findOne(supId)) {
          case Success(transferOpt) => transferOpt match {
            case Some(sup) => complete(sup)
            case None => complete(NotFound, s"The transfer doesn't exist")
          }
          case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }

  @ApiOperation(value = "Add Transfer", notes = "", nickname = "", httpMethod = "POST", produces = "text/plain")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "Transfer Object", required = true,
      dataType = "persistence.entities.SimpleTransfer", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 400, message = "Bad Request"),
    new ApiResponse(code = 201, message = "Entity Created")
  ))
 def transferPostRoute = path("transfer") {
    post {
      entity(as[SimpleTransfer]) { transferToInsert =>
        validate((transferToInsert.from != null
          && transferToInsert.to != null
          && transferToInsert.value > 0
          && transferToInsert.from != transferToInsert.to), "Wrong parameters for transfer") {
            transferExecutor.enquequeToExecute(transferToInsert)
            complete(Created)
        }
      }
    }
  }
  val routes: Route = transferPostRoute ~ transferGetRoute

}

