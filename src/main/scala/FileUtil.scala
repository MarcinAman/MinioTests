import java.io.{File, FileInputStream, InputStream, PrintWriter}

case class FileDetails(name: String, content: InputStream, contentType: String)

object FileUtil {
  def createFile(name: String): FileDetails = {
    val writter = new PrintWriter(new File(name))

    writter.write(this.generateRandomContent(name))
    writter.close()

    FileDetails(name, new FileInputStream(new File(name)), "application/octet-stream")
  }

  def generateRandomContent(fileName: String): String = s"testing file : $fileName"
}
