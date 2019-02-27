import akka.actor.ActorSystem
import akka.stream.alpakka.s3.{S3Ext, S3Settings}
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.AwsRegionProvider

object AwsConnectionSettingsProvider {

  def getSettings(connectionProperties: MinioConnectionProperties)(implicit actorSystem: ActorSystem): S3Settings = {
    val awsCredentials = new BasicAWSCredentials(connectionProperties.accessKey, connectionProperties.secretKey)
    val awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials)
    val regionProvider = new AwsRegionProvider {
      def getRegion: String = null
    }

    S3Ext(actorSystem).settings
      .withCredentialsProvider(awsCredentialsProvider)
      .withS3RegionProvider(regionProvider)
      .withEndpointUrl(connectionProperties.endpoint)
  }
}
