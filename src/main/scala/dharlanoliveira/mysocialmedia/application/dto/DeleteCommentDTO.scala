package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class DeleteCommentDTO {

  @JsonProperty
  var id: Long = _

  @JsonProperty
  var postId: Long = _

  @JsonProperty
  var userUid : String = _

}
