package dharlanoliveira.mysocialmedia.application.domain

import dharlanoliveira.mysocialmedia.application.BusinessViolation
import dharlanoliveira.mysocialmedia.application.util.Util.md5

/*
 * Class user that guarantees the consistency of the state checking invariants in constructor
 */
class User() {
  var uid: String = _
  var username: String = _
  var email: String = _


  def this(username: String, email: String, password: String) = {
    this()

    if (password == null) {
      throw new BusinessViolation("Password cannot be null")
    }

    this.username = username
    this.email = email
    this.uid = md5(Array(username, email, password))
    checkInvariants()
  }

  /*
   * It's impossible to have two constructor with the same parameters types, so I "create" a variation with Array[Char]
   */
  def this(uid: Array[Char], username: String, email: String) = {
    this()

    this.uid = uid.mkString
    this.username = username
    this.email = email

    checkInvariants()
  }

  def checkInvariants(): Unit = {
    if (this.username == null) {
      throw new BusinessViolation("Username cannot be null")
    } else if (this.email == null) {
      throw new BusinessViolation("Email cannot be null")
    } else if (this.uid == null) {
      throw new BusinessViolation("UID cannot be null")
    }
  }

  def toMap: Map[String, _] = {
    Map(
      "uid" -> this.uid,
      "username" -> this.username,
      "mail" -> this.email
    )
  }


}
