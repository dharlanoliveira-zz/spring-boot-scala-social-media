package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class UserRegistrationCommand {

  @JsonProperty
  var username: String = _

  @JsonProperty
  var email: String = _

  @JsonProperty
  var password: String = _

}
