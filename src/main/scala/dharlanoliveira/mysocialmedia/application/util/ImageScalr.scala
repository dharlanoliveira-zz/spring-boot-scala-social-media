package dharlanoliveira.mysocialmedia.application.util

import org.imgscalr.Scalr
import org.imgscalr.Scalr.Method

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import javax.imageio.ImageIO

object ImageScalr {

  def scaledownAndCompress(stream: InputStream): ByteArrayInputStream = {
    val output = new ByteArrayOutputStream()
    val image = ImageIO.read(stream)
    val resizedImage = Scalr.resize(image,Method.SPEED,128)

    ImageIO.write(resizedImage,"png",output)
    new ByteArrayInputStream(output.toByteArray)
  }

}
