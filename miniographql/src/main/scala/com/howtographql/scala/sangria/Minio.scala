package com.howtographql.scala.sangria
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import model.model.{BucketContent, Link, LinkRequest, UploadRequest}
import util.Util._

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

case class MinioConnectionProperties(endpoint: String, accessKey: String, secretKey: String)

class Minio(private val connectionProperties: MinioConnectionProperties){
  import io.minio.MinioClient

  private val minioClient = new MinioClient(connectionProperties.endpoint,
    connectionProperties.accessKey, connectionProperties.secretKey)

  def listBucketContent(bucketName: String)(implicit materializer: ActorMaterializer, ec: ExecutionContext): Future[BucketContent] = {
    Source(
      minioClient.listObjects(bucketName)
        .asScala
        .map(e => e.get().objectName())
        .toList
    ).runWith(Sink.seq).map(e => BucketContent(bucketName, e))
  }

  def getDownloadableURL(request: LinkRequest)(implicit ec: ExecutionContext): Future[Option[Link]] = {
    try {
      val url = Future(minioClient.presignedGetObject(request.bucket, request.fileName))

      url.map(url =>  Option(Link(url = url, bucket = request.bucket, fileName = request.fileName)))
    } catch {
      case e: Exception =>
        println(e.toString)
        Future(Option.empty)
    }
  }

  def uploadFile(request: UploadRequest)(implicit ec: ExecutionContext): Future[Option[Link]] = {
    try {
      val file = downloadFile(request.url)
      minioClient.putObject(request.bucketName,request.fileName, file,"application/octet-stream")

      this.getDownloadableURL(LinkRequest(request.bucketName, request.fileName))
    } catch {
      case e: Exception =>
        println(e.toString)
        Future(Option.empty)
    }
  }
}
