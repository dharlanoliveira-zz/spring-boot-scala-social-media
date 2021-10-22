package dharlanoliveira.mysocialmedia.application

import dharlanoliveira.mysocialmedia.application.domain.Post
import dharlanoliveira.mysocialmedia.application.dto.{IdDTO, NewCommentDTO, UpdatePostDTO}
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
    val post = postRepository.getPostById(comment.postId)
    val existsUser = userRepository.existsUserWithId(comment.userUid)

    if(post == null) throw new BusinessViolation(s"Post with id ${comment.postId} is invalid")
    if(!existsUser) throw new BusinessViolation(s"User with id ${comment.userUid} is invalid")

    post.addComment(comment.text,comment.userUid)

    postRepository.save(post)
  }

  def getPostImage(id: Long) : InputStream = {
    postRepository.getImageByPostId(id)
  }

}
