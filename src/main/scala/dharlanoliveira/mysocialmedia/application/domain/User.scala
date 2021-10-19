package dharlanoliveira.mysocialmedia.application.domain

import dharlanoliveira.mysocialmedia.application.BusinessViolation

class User(val id: Long, val username: String, val email: String) {

  def this(username: String, email: String) {
    this(0L, username, email)

    checkInvariants()
  }

  def checkInvariants(): Unit = {
    if(this.username == null){
      throw new BusinessViolation("Username cannot be null")
    } else if(this.email == null){
      throw new BusinessViolation("Email cannot be null")
    }
  }

  def toMap: Map[String, _] = {
    Map(
      "username" -> this.username,
      "mail" -> this.email
    )
  }


}
