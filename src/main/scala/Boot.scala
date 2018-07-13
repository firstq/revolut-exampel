import java.util.concurrent.{ConcurrentLinkedQueue, TimeUnit}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import rest.{AccountRoutes, TransferRoutes}
import utils._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import persistence.entities.SimpleTransfer

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn
import processing.TransfersExecutor

object Main extends App with RouteConcatenation {
  val modules = new ConfigurationModuleImpl  with ActorModuleImpl with PersistenceModuleImpl
  implicit val system = modules.system
  implicit val materializer = ActorMaterializer()
  implicit val ec = modules.system.dispatcher

  val scheduler = ActorSystem().scheduler;
  val transfersQueue = new ConcurrentLinkedQueue[SimpleTransfer]
  val transfersExecutor = new TransfersExecutor(transfersQueue,modules)
  scheduler.schedule(Duration(5000, TimeUnit.MILLISECONDS), Duration(1000, TimeUnit.MILLISECONDS),transfersExecutor)

  import modules.profile.api._
  Await.result(modules.db.run(modules.accountsDal.tableQuery.schema.create), Duration.Inf)
  Await.result(modules.db.run(modules.transfersDal.tableQuery.schema.create), Duration.Inf)

  val bindingFuture = Http().bindAndHandle(
    cors()(new AccountRoutes(modules).routes ~
    new TransferRoutes(modules,transfersExecutor).routes ~
    SwaggerDocService.assets ~
    SwaggerDocService.routes), "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}