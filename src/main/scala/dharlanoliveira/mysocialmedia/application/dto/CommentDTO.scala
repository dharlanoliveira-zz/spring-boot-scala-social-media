package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

trait CommentDTO {

  @JsonProperty
  var userUid: String = _

  @JsonProperty
  var postId: Long = _

  @JsonProperty
  var text: String = _

}
