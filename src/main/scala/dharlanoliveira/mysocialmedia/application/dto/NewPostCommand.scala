package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class NewPostCommand {

  @JsonProperty
  var userUid: String = _

  @JsonProperty
  var text: String = _

  @JsonProperty
  var imageBase64: String = _

}
