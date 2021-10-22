package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class NewCommentDTO {

  @JsonProperty
  var userUid: String = _

  @JsonProperty
  var postId: Long = _

  @JsonProperty
  var text: String = _

}
