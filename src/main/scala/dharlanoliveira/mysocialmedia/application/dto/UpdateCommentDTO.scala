package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class UpdateCommentDTO extends CommentDTO {

  @JsonProperty
  var id: Long = _

}
