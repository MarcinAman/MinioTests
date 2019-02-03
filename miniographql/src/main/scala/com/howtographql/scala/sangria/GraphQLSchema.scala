package com.howtographql.scala.sangria

import model.model._
import sangria.macros.derive._
import sangria.schema.{Field, ObjectType}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

import sangria.schema._


object GraphQLSchema {
  implicit val LinkType: ObjectType[Unit, Link] = deriveObjectType[Unit, Link]()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  implicit val bucketContentType: ObjectType[Unit, BucketContent] = deriveObjectType[Unit, BucketContent]()

  val QueryType = ObjectType(
    "Query",
    fields[MinioContext, Unit](
      Field("bucketContent",
        OptionType(bucketContentType),
        arguments = List(Argument("bucket", StringType)),
        resolve = c => c.ctx.minio.listBucketContent(c.args.arg("bucket"))),
      Field("url",
        OptionType(LinkType),
        arguments = List(Argument("bucket", StringType), Argument("fileName", StringType)),
        resolve = c =>
          c.ctx.minio.getDownloadableURL(
            LinkRequest(bucket = c.args.arg("bucket"), fileName = c.args.arg("fileName")))
      )
    )
  )

  val MutationType = ObjectType(
    "Mutation",
    fields[MinioContext, Unit](
      Field(
        "saveFile",
        OptionType(LinkType),
        arguments = List(Argument("bucket", StringType), Argument("url", StringType), Argument("fileName", StringType)),
        resolve =  c =>
          c.ctx.minio.uploadFile(
            UploadRequest(bucketName = c.args.arg("bucket"),
              url = c.args.arg("url"),
              fileName = c.args.arg("fileName"))
          )
      )
    )
  )

  val SchemaDefinition = Schema(QueryType, Some(MutationType))
}