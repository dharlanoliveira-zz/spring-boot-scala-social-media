package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class DeletePostDTO {

  @JsonProperty
  var id: Long = _

  @JsonProperty
  var userUid : String = _

}
