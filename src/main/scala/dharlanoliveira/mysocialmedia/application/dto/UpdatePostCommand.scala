package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class UpdatePostCommand extends PostCommand {

  @JsonProperty
  var id: Long = _

}
