package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class UpdateCommentCommand extends CommentCommand {

  @JsonProperty
  var id: Long = _

}
