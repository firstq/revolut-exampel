package utils

import akka.http.scaladsl.model.StatusCodes
import com.github.swagger.akka._
import com.github.swagger.akka.model.Info
import rest.{AccountRoutes, TransferRoutes}

import scala.collection.immutable.Set

object SwaggerDocService extends SwaggerHttpService  {
  override val apiClasses: Set[Class[_]] = Set(classOf[AccountRoutes],classOf[TransferRoutes])
  override val host = "localhost:8080"
  override val info = Info(version = "2.0")

  def assets = pathPrefix("swagger") {
    getFromResourceDirectory("swagger") ~ pathSingleSlash(get(redirect("index.html", StatusCodes.PermanentRedirect))) }

}