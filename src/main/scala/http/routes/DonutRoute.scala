package http.routes

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import akka.http.scaladsl.server.Directives._
import http.jsonsupport.{Donut, Donuts, JsonSupport}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class DonutRoute extends JsonSupport with LazyLogging{

  val donutDao = new DonutDao()

  def route(): Route = {
    path("create-donut") {
      post {
        entity(as[Donut]) { donut =>
          logger.info(s"creating donut = $donut")
          complete(StatusCodes.Created, s"Created donut = $donut")
        }
      } ~delete {
        complete(StatusCodes.MethodNotAllowed, "The HTTP DELETE operation is not allowed for the create-donut path")
      }
    } ~path("donuts") {
      get {
        onSuccess(donutDao.fetchDonuts()) { donuts =>
          complete(StatusCodes.OK, donuts)
        }
      }
    } ~path("donuts-with-future-success-failure") {
      get {
        onComplete(donutDao.fetchDonuts()) {
          case Success(donuts) => complete(StatusCodes.OK, donuts)
          case Failure(ex) => complete(s"Failed to fetch donuts = ${ex.getMessage}")
        }
      } ~ path("complete-with-http-response") {
        get {
          complete(HttpResponse(status = StatusCodes.OK, entity = "Using an HttpResponse object"))
        }
      }
    }
  }


  class DonutDao {
    import scala.concurrent.ExecutionContext.Implicits.global

    val donutsFromDb = Vector(
      Donut("Plain Donut", 1.50),
      Donut("Chocolate Donut", 2),
      Donut("Glazed Donut", 2.50)
    )

    def fetchDonuts() : Future[Donuts] = Future {
        Donuts(donutsFromDb)
    }
  }

}
