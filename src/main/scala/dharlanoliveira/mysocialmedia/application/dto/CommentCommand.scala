package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

trait CommentCommand {

  @JsonProperty
  var userUid: String = _

  @JsonProperty
  var postId: Long = _

  @JsonProperty
  var text: String = _

}
