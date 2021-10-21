package dharlanoliveira.mysocialmedia.application

import dharlanoliveira.mysocialmedia.application.domain.Post
import dharlanoliveira.mysocialmedia.application.dto.IdDTO
import dharlanoliveira.mysocialmedia.application.util.ImageScalr
import dharlanoliveira.mysocialmedia.repository.{PostRepository, UserRepository}
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
    if(userUid != null && !userRepository.existsUserWithId(userUid)) throw new BusinessViolation(s"User ${userUid} is invalid")

    val post = if(stream != null) {
      new Post(userUid,text,ImageScalr.scaledownAndCompress(stream))
    } else new Post(userUid, text)
    val id = postRepository.save(post)
    new IdDTO(id)
  }

  def getPostImage(id: Long) : InputStream = {
    postRepository.getImageByPostId(id)
  }

}
