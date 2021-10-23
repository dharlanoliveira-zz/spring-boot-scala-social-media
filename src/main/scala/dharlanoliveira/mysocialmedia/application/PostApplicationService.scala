package dharlanoliveira.mysocialmedia.application

import dharlanoliveira.mysocialmedia.application.domain.Post
import dharlanoliveira.mysocialmedia.application.dto.{DeleteCommentDTO, DeletePostDTO, IdDTO, NewCommentDTO, UpdateCommentDTO, UpdatePostDTO}
import dharlanoliveira.mysocialmedia.application.util.ImageScalr
import dharlanoliveira.mysocialmedia.repository.{PostRepository, UserRepository}
import org.springframework.beans.BeanUtils
import org.springframework.beans.BeanUtils.copyProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.io.{ByteArrayInputStream, InputStream}

@Component
class PostApplicationService {

  @Autowired
  var postRepository: PostRepository = _

  @Autowired
  var userRepository: UserRepository = _

  def newPost(userUid: String, text: String, stream: ByteArrayInputStream): IdDTO = {
    if (userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")

    val post = if (stream != null) {
      new Post(userUid, text, ImageScalr.scaledownAndCompress(stream))
    } else new Post(userUid, text)
    val id = postRepository.save(post)
    new IdDTO(id)
  }

  def updatePost(updatePost: UpdatePostDTO, stream: ByteArrayInputStream): Unit = {
      if(updatePost.id == 0) throw new BusinessViolation(s"Post with id ${updatePost.id} is invalid")
      val currentPost = postRepository.getPostById(updatePost.id)

      if(currentPost == null) throw new BusinessViolation(s"Post with id ${updatePost.id} doesnt't exists")

      currentPost.update(updatePost.text,updatePost.userUid,stream)
      if(stream != null)
        currentPost.image = stream
      postRepository.save(currentPost)
  }

  def newComment(comment: NewCommentDTO): Unit = {
    val userUid = comment.userUid
    if (userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")
    val post = postRepository.getPostById(comment.postId)
    if(post == null) throw new BusinessViolation(s"Post with id ${comment.postId} is invalid")
    post.addComment(comment.text,comment.userUid)
    postRepository.save(post)
  }

  def updateComment(updateComment: UpdateCommentDTO): Unit = {
    val userUid = updateComment.userUid
    if (userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")
    val post = postRepository.getPostById(updateComment.postId)
    if(post == null) throw new BusinessViolation(s"Post with id ${updateComment.id} is invalid")
    post.updateComment(updateComment.id, updateComment.userUid, updateComment.text)
    postRepository.save(post)
  }

  def deleteComment(deleteComment: DeleteCommentDTO): Unit = {
    val userUid = deleteComment.userUid
    if (userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")
    val post = postRepository.getPostById(deleteComment.postId)
    if(post == null) throw new BusinessViolation(s"Post with id ${deleteComment.id} is invalid")
    val comment = post.deleteComment(deleteComment.id, deleteComment.userUid)
    postRepository.deleteComment(comment.id)
  }

  def deletePost(deletePost: DeletePostDTO): Unit = {
    val userUid = deletePost.userUid
    if (userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User with id ${userUid} is invalid")
    val post = postRepository.getPostById(deletePost.id)
    if(post == null) throw new BusinessViolation(s"Post with id ${deletePost.id} is invalid")
    if(post.ownerUid != deletePost.userUid)  throw new BusinessViolation(s"Only same user can delete the post")

    postRepository.deletePost(deletePost.id)
  }

  def getPostImage(id: Long) : InputStream = {
    postRepository.getImageByPostId(id)
  }

}
