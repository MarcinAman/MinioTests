package com.howtographql.scala.sangria

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Route
import sangria.parser.QueryParser
import spray.json.{JsObject, JsString, JsValue}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.ActorMaterializer

class GraphQLServer(implicit materializer: ActorMaterializer) {
  val minioClient = new Minio(loadConnectionProperites)
  val graphQLSchema = new GraphQLSchema()

  def endpoint(requestJSON: JsValue)(implicit ec: ExecutionContext): Route = {

    val JsObject(fields) = requestJSON

    val JsString(query) = fields("query")

    QueryParser.parse(query) match {
      case Success(queryAst) =>
        val operation = fields.get("operationName") collect {
          case JsString(op) => op
        }

        val variables = fields.get("variables") match {
          case Some(obj: JsObject) => obj
          case _ => JsObject.empty
        }
        complete(executeGraphQLQuery(queryAst, operation, variables))
      case Failure(error) =>
        complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }

  }

  private def executeGraphQLQuery(query: Document, operation: Option[String], vars: JsObject)
                                 (implicit ec: ExecutionContext): Future[(StatusCode, JsValue)] = {
    Executor.execute(
      graphQLSchema.SchemaDefinition,
      query,
      MinioContext(minioClient),
      variables = vars,
      operationName = operation
    ).map(OK -> _)
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
  }

  private def loadConnectionProperites = MinioConnectionProperties(
    ConfigFactory.load().getString("minio.connection.endpoint"),
    ConfigFactory.load().getString("minio.connection.login"),
    ConfigFactory.load().getString("minio.connection.password")
  )

}