package dharlanoliveira.mysocialmedia.application.domain

import dharlanoliveira.mysocialmedia.application.BusinessViolation

import java.time.LocalDateTime

class Comment() {

  var id: Long = _
  var ownerUid: String = _
  var text: String = _
  var usersUidMentions: List[String] = _
  var instant: LocalDateTime = _

  def this(text: String, ownerUid: String) = {
    this()

    this.ownerUid = ownerUid
    this.text = text
    this.instant = LocalDateTime.now()

    checkInvariants()

    this.usersUidMentions = extractMentions(text)
  }

  def this(id: Long, text: String, ownerUid: String, instant: LocalDateTime) = {
    this()

    this.id = id
    this.ownerUid = ownerUid
    this.text = text
    this.instant = instant

    checkInvariants()
  }

  def updateText(text: String) : Unit = {
    this.text = text
    this.instant = LocalDateTime.now()

    checkInvariants()
  }

  def checkInvariants(): Unit = {
    if (this.ownerUid == null) {
      throw new BusinessViolation("User cannot be null")
    } else if (this.text == null) {
      throw new BusinessViolation("Text cannot be null")
    } else if (this.instant == null) {
      throw new BusinessViolation("Instant cannot be null")
    }
  }

  def extractMentions(text: String): List[String] = {
    val pattern = "@([^ ]*)".r
    val users = pattern.findAllMatchIn(text).map { e =>
      e.group(1)
    }
    users.toList
  }

}
