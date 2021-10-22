package dharlanoliveira.mysocialmedia.rest

import dharlanoliveira.mysocialmedia.application.{PostApplicationService, UserApplicationService}
import dharlanoliveira.mysocialmedia.application.dto.{IdDTO, NewCommentDTO, NewPostDTO, UpdatePostDTO, UserIdDTO, UserRegistrationDTO}
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.{GetMapping, PatchMapping, PathVariable, PostMapping, RequestBody, ResponseStatus, RestController}
import org.valid4j.Assertive.ensure

import java.io.{ByteArrayInputStream, InputStream}
import java.net.URLConnection
import java.util.{Base64, Optional}
import javax.servlet.http.HttpServletResponse

@RestController
class PostsController {

  @Autowired
  var applicationService: PostApplicationService = _

  val validImageContentTypes: Array[String] = Array("image/png","image/jpeg")

  /**
   * Register new user
   *
   * @param post Information - mail and username - about new user
   * @return User id
   */
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(path = Array("/posts"))
  def newPost(@RequestBody post: NewPostDTO): IdDTO = {
    ensure(post != null)
    val stream = extractImageContent(post.imageBase64)
    applicationService.newPost(post.userUid,post.text,stream)
  }

  @ResponseStatus(HttpStatus.OK)
  @PatchMapping(path = Array("/posts/{id}"))
  def updatePost(@PathVariable id: Long, @RequestBody post: UpdatePostDTO): Unit = {
    ensure(post != null)
    ensure(post.id == id, "URL and body post ID are differents")
    val stream = extractImageContent(post.imageBase64)
    applicationService.updatePost(post, stream)
  }

  /**
   * Return image associated with a post
   */
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(path = Array("/posts/{id}/image"))
  def postImage(@PathVariable id: Long, response: HttpServletResponse): Unit = {
    val stream = applicationService.getPostImage(id)
    if(stream == null){
      response.setStatus(HttpStatus.NOT_FOUND.value())
    } else {
      response.setContentType("image/png")
      IOUtils.copy(stream, response.getOutputStream)
      response.flushBuffer()
    }
  }

  /**
   * Return image associated with a post
   */
  @ResponseStatus(HttpStatus.OK)
  @PostMapping(path = Array("/posts/{postId}/comments"))
  def postComment(@PathVariable postId: Long, @RequestBody comment: NewCommentDTO): Unit = {
    ensure(comment != null)
    ensure(comment.postId == postId)
    applicationService.newComment(comment)
  }

  def extractImageContent(image: String): ByteArrayInputStream = {
    if (image != null) {
      val imageBytes = Base64.getDecoder.decode(image)
      val stream = new ByteArrayInputStream(imageBytes)
      val contentType = URLConnection.guessContentTypeFromStream(stream)

      if (!validImageContentTypes.contains(contentType)) {
        throw new IllegalArgumentException("Image doens't have a valid content")
      }
      stream
    } else null
  }

}
