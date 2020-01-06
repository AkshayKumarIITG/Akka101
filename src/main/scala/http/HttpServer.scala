package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import http.routes.{DonutRoute, ServerVersion}

import scala.io.StdIn
import scala.util.{Failure, Success}
object HttpServer extends App with LazyLogging{

  //“implicits” allow you to omit calling methods or referencing variables directly but instead rely on the compiler to
  // make the connections for you

  implicit val system = ActorSystem("akka-http-rest-server")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val host = "127.0.0.1"
  val port = 8080

  val serverUpRoute: Route = get {
    complete("Akka HTTP Server is UP.")
  }

  val serverVersion = new ServerVersion()
  val serverVersionRoute = serverVersion.route()
  val serverVersionRouteAsJson = serverVersion.routeAsJson()
  val serverVersionJsonEncoding = serverVersion.routeAsJsonEncoding()

  val donutRoutes = new DonutRoute().route()


  val routes: Route =  donutRoutes ~ serverVersionRoute ~ serverVersionRouteAsJson ~ serverVersionJsonEncoding~
    serverUpRoute

  val httpServerFuture = Http().bindAndHandle(routes, host, port)

  httpServerFuture.onComplete {
    case Success(binding) =>
      logger.info(s"Akka Http Server is UP and is bound to ${binding.localAddress}")

    case Failure(e) =>
      logger.error(s"Akka Http server failed to start", e)
      system.terminate()
  }


  StdIn.readLine() //let it run until user passes return
  httpServerFuture
    .flatMap(_.unbind()) //trigger unbinding from the port
    .onComplete(_ => system.terminate()) //and shutdown when done
}
