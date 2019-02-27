
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.s3.scaladsl.S3
import akka.stream.alpakka.s3.{ListBucketResultContents, S3Attributes}
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

class Alpakka(private val connectionProperties: MinioConnectionProperties)
             (implicit val actorSystem: ActorSystem) {
  private val settings = AwsConnectionSettingsProvider.getSettings(connectionProperties)

  def list(bucketName: String)(implicit materializer: ActorMaterializer): Future[Option[ListBucketResultContents]] = {

    val keySource: Source[ListBucketResultContents, NotUsed] =
      S3.listBucket(bucketName, None)
        .withAttributes(S3Attributes.settings(settings))

    keySource.runWith(Sink.headOption)(materializer)
  }

  def createBucket(bucketName: String) = {
    S3.makeBucket(bucketName)
  }
}
