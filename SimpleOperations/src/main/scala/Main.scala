import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/*
docker run -p 9000:9000 -e "MINIO_ACCESS_KEY=AKIAIOSFODNN7EXAMPLE" -e "MINIO_SECRET_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY" minio/minio server /data

1. Ogarniczenie do 32 nodeow
2. Czemu nie mozna pobrac bucket-a jako obiekt a jedynie pliki z niego? Byloby to logiczne z punktu widzenia obiektowego
3. Uploadowanie pliku jest niskopoziomowe


Czy jak tworzy sie bucket to on jest automatycznie shardowany?
Jak wylistowac co jest w danym buckecie? minioClient.listObjects(e.name())

- Smutno bo policy jest per bucket a nie per file

dobra, ale jak to skalowac? Nie da sie, jedynie reset:
https://github.com/minio/minio/issues/5205

wiec jak cos upadnie to ma powstac i uzyc manualnego heal-a
 */

object Main extends App {
  val connection = MinioConnectionProperties("http://localhost:9000", "minio", "minio123")
//  val minioTest =  new MinioTest(connection)

//  println(minioTest.getDownloadadbleURL("picturebucket", "download.png"))

//  minioTest.complexRoute()

  implicit val system: ActorSystem = ActorSystem("reactive-minio")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val m = new Alpakka(connection)

  Await.result(m.list("test"), Duration(10, TimeUnit.SECONDS)) match {
    case Some(value) => println(value)
    case None => println("Empty bucket")
  }
}
