package dharlanoliveira.mysocialmedia.application.domain

import dharlanoliveira.mysocialmedia.application.BusinessViolation

import java.io.InputStream
import java.sql.Timestamp
import java.time.LocalDateTime

class Post() {
  var id: Long = _
  var userUid: String = _
  var text: String = _
  var image: InputStream = _
  var instant: LocalDateTime = _

  def this(userUid: String, text: String, image: InputStream) = {
    this()

    this.userUid = userUid
    this.text = text
    this.image = image
    this.instant = LocalDateTime.now()
    checkInvariants()
  }

  def this(userUid: String, text: String) = {
    this()

    this.userUid = userUid
    this.text = text
    this.instant = LocalDateTime.now()
    checkInvariants()
  }

  def this(id: Long, userUid: String, text: String, image: InputStream, instant: LocalDateTime) = {
    this(userUid,text,image)
    if (id <= 0) {
      throw new BusinessViolation("Post ID invalid")
    }
    this.id = id
    this.instant = instant
  }

  def checkInvariants(): Unit = {
    if (this.userUid == null) {
      throw new BusinessViolation("User cannot be null")
    } else if (this.text == null) {
      throw new BusinessViolation("Text cannot be null")
    } else if (this.instant == null) {
      throw new BusinessViolation("Instant cannot be null")
    }
  }

  def toMap: Map[String, _] = {
    Map(
      "owner_uid" -> this.userUid,
      "text" -> this.text,
      "instant" -> Timestamp.valueOf(this.instant),
      "image" -> this.image
    )
  }


}
