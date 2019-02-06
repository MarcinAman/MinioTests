package com.howtographql.scala.sangria

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import spray.json.JsValue

import scala.concurrent.Await
import scala.language.postfixOps

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

object Server extends App {

  val PORT = 8080

  implicit val actorSystem: ActorSystem = ActorSystem("graphql-server")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import actorSystem.dispatcher
  import scala.concurrent.duration._

  val graphQLServer = new GraphQLServer()

  scala.sys.addShutdownHook(() -> shutdown())

  val route: Route =
    (post & path("graphql")) {
      entity(as[JsValue]) { requestJson =>
        graphQLServer.endpoint(requestJson)
      }
    } ~ {
      getFromResource("graphiql.html")
    }

  Http().bindAndHandle(route, "0.0.0.0", PORT)
  println(s"open a browser with URL: http://localhost:$PORT")


  def shutdown(): Unit = {
    actorSystem.terminate()
    Await.result(actorSystem.whenTerminated, 30 seconds)
  }
}

/*
Co to ma robic:
- wylistowac content z bucketow
- udostepnic url z wybranego pliku po buckecie i nazwie pliku
- upload pliku?

Jak zrobic typowy actor pattern w dockerze?
tak zeby polaczenie zawsze bylo po jednym sockecie
 */
