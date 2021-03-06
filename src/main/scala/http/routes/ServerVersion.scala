package http.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import http.jsonsupport.{AkkaHttpRestServer, JsonSupport}

class ServerVersion extends JsonSupport {

  def route(): Route = {
      path("server-version") {
        get {
          val serverVersion = "1.0.0.0"
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, serverVersion))        }
      }
  }


  def routeAsJson(): Route = {
    path("server-version-json") {
      get {
        val jsonResponse =
          """
            |{
            | "app": "Akka HTTP REST Server",
            | "version": "1.0.0.0"
            |}
          """.stripMargin
        complete(HttpEntity(ContentTypes.`application/json`, jsonResponse))
      }
    }
  }


  def routeAsJsonEncoding(): Route = {
    path("server-version-json-encoding") {
      get {
        val server = AkkaHttpRestServer("Akka HTTP REST Server", "1.0.0.0")
        complete(server)
      }
    }
  }


}
