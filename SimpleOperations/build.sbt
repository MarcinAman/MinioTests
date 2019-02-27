name := "MinioSingleLocal"

version := "0.1"

scalaVersion := "2.12.8"

// https://mvnrepository.com/artifact/io.minio/minio
libraryDependencies += "io.minio" % "minio" % "6.0.1"

libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "1.0-M2+172-5b47e6a3+20190227-2116"

