/*
docker run -p 9000:9000 -e "MINIO_ACCESS_KEY=AKIAIOSFODNN7EXAMPLE" -e "MINIO_SECRET_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY" minio/minio server /data

1. Ogarniczenie do 32 nodeow
2. Czemu nie mozna pobrac bucket-a jako obiekt a jedynie pliki z niego? Byloby to logiczne z punktu widzenia obiektowego
3. Uploadowanie pliku jest niskopoziomowe


Czy jak tworzy sie bucket to on jest automatycznie shardowany?
Jak wylistowac co jest w danym buckecie? minioClient.listObjects(e.name())

- Smutno bo policy jest per bucket a nie per file

fileName vs object name?

1. Upload/download pliku przez url
2. Upload/download przez graphQL
3. To samo tylko distributed
 */

object Main extends App {
//  val connection = MinioConnectionProperties("http://localhost:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY")
  val connection = MinioConnectionProperties("http://localhost:9001", "minio", "minio123")
  val minioTest =  new MinioTest(connection)

  println(minioTest.getDownloadadbleURL("picturebucket", "download.png"))

  minioTest.complexRoute()
}
