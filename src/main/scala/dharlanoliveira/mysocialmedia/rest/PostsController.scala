package dharlanoliveira.mysocialmedia.rest

import dharlanoliveira.mysocialmedia.application.PostApplicationService
import dharlanoliveira.mysocialmedia.application.dto._
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation._
import org.valid4j.Assertive.ensure

import java.io.ByteArrayInputStream
import java.net.URLConnection
import java.util.Base64
import javax.servlet.http.HttpServletResponse
import scala.jdk.javaapi.CollectionConverters.asJava

@RestController
class PostsController {

  @Autowired
  var applicationService: PostApplicationService = _

  val validImageContentTypes: Array[String] = Array("image/png","image/jpeg")

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(path = Array("/posts"), params=Array("userUid"))
  def getAllPostsOfUser(@RequestParam(value="userUid",required=true) userUid: String, @RequestParam(required=false) order: String): java.util.List[PostDTO] = {
    val isOrderType = Order.isOrderType(order)
    val orderSelected = if (isOrderType) Order.withName(order) else Order.DESC
    ensure(userUid != null)
    asJava(applicationService.getAllPostsOfUser(userUid, orderSelected))
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(path = Array("/posts"))
  def getAllPosts(@RequestParam(required=false) order: String): java.util.List[PostDTO] = {
    val isOrderType = Order.isOrderType(order)
    val orderSelected = if (isOrderType) Order.withName(order) else Order.DESC
    asJava(applicationService.getAllPosts(orderSelected))
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(path = Array("/posts"))
  def newPost(@RequestBody post: NewPostCommand): IdDTO = {
    ensure(post != null)
    val stream = extractImageContent(post.imageBase64)
    applicationService.newPost(post.userUid,post.text,stream)
  }

  @ResponseStatus(HttpStatus.OK)
  @PatchMapping(path = Array("/posts/{id}"))
  def updatePost(@PathVariable id: Long, @RequestBody post: UpdatePostCommand): Unit = {
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

  @ResponseStatus(HttpStatus.OK)
  @PostMapping(path = Array("/posts/{postId}/comments"))
  def postComment(@PathVariable postId: Long, @RequestBody comment: NewCommentDTO): Unit = {
    ensure(comment != null)
    ensure(comment.postId == postId)
    applicationService.newComment(comment)
  }

  @ResponseStatus(HttpStatus.OK)
  @PatchMapping(path = Array("/posts/{postId}/comments/{commentId}"))
  def update(@PathVariable postId: Long,@PathVariable commentId: Long, @RequestBody comment: UpdateCommentCommand): Unit = {
    ensure(comment != null)
    ensure(comment.postId == postId)
    ensure(comment.id == commentId)
    applicationService.updateComment(comment)
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping(path = Array("/posts/{postId}/comments/{commentId}"))
  def deleteComment(@PathVariable postId: Long,@PathVariable commentId: Long, @RequestBody deleteComment: DeleteCommentCommand): Unit = {
    ensure(deleteComment != null)
    ensure(deleteComment.id == commentId)
    ensure(deleteComment.postId == postId)
    applicationService.deleteComment(deleteComment)
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping(path = Array("/posts/{postId}"))
  def deletePost(@PathVariable postId: Long, @RequestBody deletePost: DeletePostCommand): Unit = {
    ensure(deletePost != null)
    ensure(deletePost.id == postId)
    applicationService.deletePost(deletePost)
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
