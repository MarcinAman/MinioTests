import java.io.InputStream

import io.minio.MinioClient
import io.minio.errors.InvalidBucketNameException

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.io.Source.fromInputStream

case class MinioConnectionProperties(endpoint: String, accessKey: String, secretKey: String)

class MinioTest(private val connectionProperties: MinioConnectionProperties) {
  private val minioClient = new MinioClient(connectionProperties.endpoint, connectionProperties.accessKey, connectionProperties.secretKey)

  def complexRoute(): Unit = {

    clearBuckets()

    List.range(0, 10).map(i => createBucket("test" + i))

    printf(listContainers())

    for(i <- 0 until 10) {
      for(j <- 0 to 5) {
        uploadFileToBucket(s"test$i", FileUtil.createFile(s"bucket_$i test$i file_$j"))
      }
    }

    val url = getURLfromFile("test0", "bucket_0 test0 file_0")

    println(s"\nFirst file from bucket url: $url")

    val downloadableURL = getDownloadadbleURL("test0", "bucket_0 test0 file_0")

    println(s"\nFirst file from bucket url (downloadable): $downloadableURL")

    val downloadFileInputStream = downloadFileFromBucket("test0", "bucket_0 test0 file_0")

    val content = fromInputStream(downloadFileInputStream).mkString
    println("First file content: :")
    println(content)

    println(minioClient.statObject("test0", "bucket_0 test0 file_0").toString)
  }

  def listContainers(): String = {
    this.minioClient.listBuckets().asScala.mkString("\n")
  }

  def createBucket(name: String, region: String = null): Boolean = {
    try {
      this.minioClient.makeBucket(name, region)
      this.minioClient.bucketExists(name)
    } catch {
      case _: InvalidBucketNameException => false
    }
  }

  def clearBuckets(): Unit = {
    this.minioClient.listBuckets().asScala.foreach(e => {
      this.clearBucketeContent(e.name())
      minioClient.removeBucket(e.name())
    })
  }

  private def clearBucketeContent(name: String): Unit = {
    val content = minioClient.listObjects(name).asScala.map(e => e.get().objectName())
    content.foreach(e => {
      minioClient.removeObject(name, e)
    })
  }

  def uploadFileToBucket(bucketName: String, file: FileDetails): Unit = {
    this.minioClient.putObject(bucketName, file.name, file.content, file.contentType)
  }

  def downloadFileFromBucket(bucketName: String, fileName: String): InputStream = {
    this.minioClient.getObject(bucketName, fileName)
  }

  def getDownloadadbleURL(bucketName: String, fileName: String): String = {
    this.minioClient.presignedGetObject(bucketName, fileName)
  }

  def getURLfromFile(bucketName: String, fileName: String): String = {
    this.minioClient.getObjectUrl(bucketName, fileName)
  }
}
