package com.howtographql.scala.sangria
import model.model.{BucketContent, Link, LinkRequest}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

case class MinioConnectionProperties(endpoint: String, accessKey: String, secretKey: String)

class Minio(private val connectionProperties: MinioConnectionProperties){
  val logger = LoggerFactory.getLogger(this.getClass)

  import io.minio.MinioClient

  private val minioClient = new MinioClient(connectionProperties.endpoint,
    connectionProperties.accessKey, connectionProperties.secretKey)

  def listBucketContent(bucketName: String)(implicit ec: ExecutionContext): Future[Option[BucketContent]] = {
    logger.debug(s"request to list bucket content with args: $bucketName")
    try {
      val content = minioClient.listObjects(bucketName).asScala.map(e => e.get().objectName()).toList

      Future(Option(BucketContent(bucketName, content)))
    } catch {
      case e: Exception =>
        logger.error(e.toString)
        Future(Option.empty)
    }
  }

  def getDownloadableURL(request: LinkRequest)(implicit ec: ExecutionContext): Future[Option[Link]] = {
    logger.debug(s"request to get dowloadable url with args: bucketName=${request.bucket}, filename=${request.fileName}")

    try {
      val url = minioClient.presignedGetObject(request.bucket, request.fileName)

      Future(Option(Link(url = url, bucket = request.bucket, fileName = request.fileName)))
    } catch {
      case e: Exception =>
        logger.error(e.toString)
        Future(Option.empty)
    }
  }
}
