import java.io.{File, FileInputStream, InputStream, PrintWriter}

import scala.util.Random

case class FileDetails(name: String, content: InputStream, contentType: String)

object FileUtil {
  def createFile(name: String): FileDetails = {
    val writter = new PrintWriter(new File(name))

    writter.write(this.generateRandomContent())
    writter.close()

    FileDetails(name, new FileInputStream(new File(name)), "application/octet-stream")
  }

  def generateRandomContent(wordsCount: Int = 100): String = {
    val random = new Random()
    List.range(0,50).map(_ => random.nextString(wordsCount)).mkString(" ")
  }
}
