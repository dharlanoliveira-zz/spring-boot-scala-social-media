package dharlanoliveira.mysocialmedia.acceptance.cucumber.features

import dharlanoliveira.mysocialmedia.application.domain.Post
import dharlanoliveira.mysocialmedia.repository.PostRepository
import io.cucumber.java.en.Given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

class PostSteps(userSteps: UserSteps) {

  @Autowired
  var restTemplate: TestRestTemplate = _

  @Autowired
  var postRepository: PostRepository = _

  var response: String = _

  var referencePost: Post = _

  @Given("there is a post with text {string} from this user")
  def postWithText(text: String): Unit = {
    val post = new Post(userSteps.users.head._2, text, null)
    postRepository.save(post)
    referencePost = postRepository.getAll().head
  }

  @Given("there is a post with text {string} from the user {string} created in {string}")
  def postWithTextToUser(text: String, username: String, creationDate: String): Unit = {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val userUid = userSteps.users.get(username).head
    val post = new Post(userUid, text)
    post.instant = LocalDate.parse(creationDate, formatter).atStartOfDay()
    postRepository.save(post)
    this.referencePost = postRepository.getAll().head
  }

  @Given("there is a post with text {string} from the user {string}")
  def postWithTextToUser(text: String, username: String) : Unit = {
    val userUid = userSteps.users.get(username).head
    val post = new Post(userUid, text, null)
    postRepository.save(post)
    referencePost = postRepository.getAll().head
  }

  @Given("there is a post of the user {string}")
  def postForUser(username: String): Unit = {
    val userUid = userSteps.users.get(username).head
    val post = new Post(userUid, "Any Text", null)
    postRepository.save(post)
    referencePost = postRepository.getAll().head
  }

}
