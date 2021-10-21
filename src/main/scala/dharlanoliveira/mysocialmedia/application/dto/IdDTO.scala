package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class IdDTO(idResult: Long) {

  @JsonProperty var id: Long = _

  this.id = idResult
}