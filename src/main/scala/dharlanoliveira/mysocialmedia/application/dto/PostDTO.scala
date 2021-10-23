package dharlanoliveira.mysocialmedia.application.dto

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnore, JsonProperty}
import org.apache.commons.io.IOUtils.toByteArray

import java.io.InputStream
import java.time.LocalDateTime
import java.util.Base64

class PostDTO {

  @JsonProperty
  var username: String = _

  @JsonProperty
  var text: String = _

  @JsonProperty
  var instant: LocalDateTime = _

  @JsonProperty
  var comments: java.util.List[CommentDTO] = _

  @JsonProperty
  var imageBase64: String = _



}
