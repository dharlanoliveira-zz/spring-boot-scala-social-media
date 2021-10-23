package dharlanoliveira.mysocialmedia.application

import dharlanoliveira.mysocialmedia.application.domain.Post
import dharlanoliveira.mysocialmedia.application.dto.Order.OrderType
import dharlanoliveira.mysocialmedia.application.dto._
import dharlanoliveira.mysocialmedia.application.util.ImageScalr
import dharlanoliveira.mysocialmedia.repository.{PostRepository, UserRepository}
import org.apache.commons.io.IOUtils.toByteArray
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.io.{ByteArrayInputStream, InputStream}
import java.util.Base64
import scala.jdk.javaapi.CollectionConverters.asJava

@Component
class PostApplicationService {

  @Autowired
  var postRepository: PostRepository = _

  @Autowired
  var userRepository: UserRepository = _


  def getAllPostsOfUser(userUid: String, order: OrderType): List[PostDTO] = {
    if (userUid == null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")
    convertPostsToDTO(postRepository.getPostsByUser(userUid, order))
  }

  def getAllPosts(order: OrderType): List[PostDTO] = {
    convertPostsToDTO(postRepository.getAll(order))
  }

  def newPost(userUid: String, text: String, stream: ByteArrayInputStream): IdDTO = {
    if (userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")

    val post = if (stream != null) {
      new Post(userUid, text, ImageScalr.scaledownAndCompress(stream))
    } else new Post(userUid, text)
    val id = postRepository.save(post)
    new IdDTO(id)
  }

  def updatePost(updatePost: UpdatePostCommand, stream: ByteArrayInputStream): Unit = {
    if (updatePost.id == 0) throw new BusinessViolation(s"Post with id ${updatePost.id} is invalid")
    val currentPost = postRepository.getPostById(updatePost.id)

    if (currentPost == null) throw new BusinessViolation(s"Post with id ${updatePost.id} doesnt't exists")

    currentPost.update(updatePost.text, updatePost.userUid, stream)
    if (stream != null)
      currentPost.image = stream
    postRepository.save(currentPost)
  }

  def newComment(comment: NewCommentDTO): Unit = {
    val userUid = comment.userUid
    if (userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")
    val post = postRepository.getPostById(comment.postId)
    if (post == null) throw new BusinessViolation(s"Post with id ${comment.postId} is invalid")
    post.addComment(comment.text, comment.userUid)
    postRepository.save(post)
  }

  def updateComment(updateComment: UpdateCommentCommand): Unit = {
    val userUid = updateComment.userUid
    if (userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")
    val post = postRepository.getPostById(updateComment.postId)
    if (post == null) throw new BusinessViolation(s"Post with id ${updateComment.id} is invalid")
    post.updateComment(updateComment.id, updateComment.userUid, updateComment.text)
    postRepository.save(post)
  }

  def deleteComment(deleteComment: DeleteCommentCommand): Unit = {
    val userUid = deleteComment.userUid
    if (userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")
    val post = postRepository.getPostById(deleteComment.postId)
    if (post == null) throw new BusinessViolation(s"Post with id ${deleteComment.id} is invalid")
    val comment = post.deleteComment(deleteComment.id, deleteComment.userUid)
    postRepository.deleteComment(comment.id)
  }

  def deletePost(deletePost: DeletePostCommand): Unit = {
    val userUid = deletePost.userUid
    if (userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")
    val post = postRepository.getPostById(deletePost.id)
    if (post == null) throw new BusinessViolation(s"Post with id ${deletePost.id} is invalid")
    if (post.ownerUid != deletePost.userUid) throw new BusinessViolation(s"Only same user can delete the post")

    postRepository.deletePost(deletePost.id)
  }

  def getPostImage(id: Long): InputStream = {
    postRepository.getImageByPostId(id)
  }

  def convertPostsToDTO(posts: List[Post]): List[PostDTO] = {
    posts.map(p => {
      val comments = p.comments.map(c => {
        val comment = new CommentDTO()
        comment.text = c.text
        comment.instant = c.instant
        comment
      })
      val post = new PostDTO()
      post.username = userRepository.getUsernameByUserUid(p.ownerUid)
      post.text = p.text
      post.imageBase64 = if(p.image != null) Base64.getEncoder.encodeToString(toByteArray(p.image)) else null
      post.instant = p.instant
      post.comments = asJava(comments)
      post
    })
  }
}
