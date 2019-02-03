package com.howtographql.scala.sangria
import model.model.{BucketContent, Link, LinkRequest, UploadRequest}
import util.Util._

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

case class MinioConnectionProperties(endpoint: String, accessKey: String, secretKey: String)

class Minio(private val connectionProperties: MinioConnectionProperties){
  import io.minio.MinioClient

  private val minioClient = new MinioClient(connectionProperties.endpoint,
    connectionProperties.accessKey, connectionProperties.secretKey)

  def listBucketContent(bucketName: String)(implicit ec: ExecutionContext): Future[Option[BucketContent]] = {
    try {
      val content = minioClient.listObjects(bucketName).asScala.map(e => e.get().objectName()).toList

      Future(Option(BucketContent(bucketName, content)))
    } catch {
      case e: Exception =>
        println(e.toString)
        Future(Option.empty)
    }
  }

  def getDownloadableURL(request: LinkRequest)(implicit ec: ExecutionContext): Future[Option[Link]] = {
    try {
      val url = minioClient.presignedGetObject(request.bucket, request.fileName)

      Future(Option(Link(url = url, bucket = request.bucket, fileName = request.fileName)))
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
