package com.howtographql.scala.sangria

import model.model._
import sangria.macros.derive._
import sangria.schema.{Field, ListType, ObjectType}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

import sangria.schema._


object GraphQLSchema {
  implicit val LinkType: ObjectType[Unit, Link] = deriveObjectType[Unit, Link]()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  implicit val bucketContentType: ObjectType[Unit, BucketContent] = deriveObjectType[Unit, BucketContent]()

  val QueryType = ObjectType(
    "Query",
    fields[MinioContext, Unit](
      Field("allLinks",
        OptionType(bucketContentType),
        arguments = List(Argument("bucket", StringType)),
        resolve = c => c.ctx.minio.listBucketContent(c.args.arg("bucket")))
    )
  )

  // 3
  val SchemaDefinition = Schema(QueryType)
}