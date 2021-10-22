package dharlanoliveira.mysocialmedia.acceptance.cucumber.features

import dharlanoliveira.mysocialmedia.application.domain.{Post, User}
import dharlanoliveira.mysocialmedia.application.dto.UpdatePostDTO
import dharlanoliveira.mysocialmedia.repository.PostRepository
import io.cucumber.java.en.{Given, Then, When}
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate

class UpdatePostSteps(userSteps: UserSteps, postSteps: PostSteps) {

  @Autowired
  var restTemplate: TestRestTemplate = _

  @Autowired
  var postRepository: PostRepository = _

  var response: String = _


  @When("This user try to update text {string} of a post that doesn't exists")
  def updatePostThatDoesntExists(text: String): Unit = {
    val dto = new UpdatePostDTO()
    dto.id = 99
    dto.text = text
    this.response = restTemplate.patchForObject(s"/posts/${dto.id}", dto, classOf[String])
  }


  @When("this user try to update text {string} of this post")
  def updatePostWithText(postText: String): Unit = {
    val dto = new UpdatePostDTO()
    dto.id = postSteps.referencePost.id
    dto.userUid = userSteps.users.head._2
    dto.text = postText
    this.response = restTemplate.patchForObject(s"/posts/${dto.id}", dto, classOf[String])
  }

  @When("the user {string} try to update this post")
  def updatePostOfOtherUser(username: String): Unit = {
    val userUid = userSteps.users.get(username).head
    val dto = new UpdatePostDTO()
    dto.id = 99
    dto.userUid = userUid
    dto.text = "Text"
    this.response = restTemplate.patchForObject(s"/posts/${dto.id}", dto, classOf[String])
  }


  @Then("the system will inform that is required a valid post")
  def postIsInvalid(): Unit = {
    assertThat(this.response).startsWith("Post with id")
    assertThat(this.response).endsWith("doesnt't exists")
  }

  @Then("the post text will became {string}")
  def postTextWillBecame(newText: String): Unit = {
    val posts = postRepository.getAll
    assertThat(posts.size).isEqualTo(1L)
    val firstPost = posts.head
    assertThat(firstPost.text).isEqualTo(newText)
    assertThat(firstPost.text).isNotEqualTo(postSteps.referencePost.text)
  }

  @Then("the system will inform that just the owner of the post can update the content")
  def systemWillInformOnlyUserCanEdit(): Unit = {
    assertThat(this.response).startsWith("Post with id")
    assertThat(this.response).endsWith("doesnt't exists")
  }


}
