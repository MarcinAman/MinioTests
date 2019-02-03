package model

object model {
  case class Link(url: String, bucket: String, fileName: String)

  case class LinkRequest(bucket: String, fileName: String)

  case class BucketContent(bucketName: String, content: List[String])

  case class UploadRequest(bucketName: String, url: String, fileName: String)
}

