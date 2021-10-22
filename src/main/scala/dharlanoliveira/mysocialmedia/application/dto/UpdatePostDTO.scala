package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class UpdatePostDTO extends PostDTO {

  @JsonProperty
  var id: Long = _

}
