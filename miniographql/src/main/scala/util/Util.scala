package util

import java.io.InputStream
import java.net.URL

object Util {
  def downloadFile(url: String): InputStream = {
    val website = new URL(url)
    website.openStream()
  }
}
