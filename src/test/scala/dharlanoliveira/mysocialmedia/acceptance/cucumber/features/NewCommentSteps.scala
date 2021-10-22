package dharlanoliveira.mysocialmedia.acceptance.cucumber.features

import dharlanoliveira.mysocialmedia.application.dto.NewCommentDTO
import dharlanoliveira.mysocialmedia.repository.PostRepository
import io.cucumber.java.en.{Then, When}
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate

class NewCommentSteps(userSteps: UserSteps, postSteps: PostSteps) {

  @Autowired
  var restTemplate: TestRestTemplate = _

  @Autowired
  var postRepository: PostRepository = _

  var response : String = _

  var username: String = _

  @When("the user {string} add a comment with text {string} to this post")
  def userAddCommentToPost(username: String, text: String): Unit = {
    this.username = username

    val dto = new NewCommentDTO()
    dto.userUid = userSteps.users(this.username)
    dto.postId = postSteps.referencePost.id
    dto.text = text
    this.response = restTemplate.postForObject(s"/posts/${dto.postId}/comments", dto, classOf[String])
  }

  @When("this user try to add a comment to a invalid post")
  def addCommentToInvalidPost(): Unit = {
    val dto = new NewCommentDTO()
    dto.userUid = userSteps.users.head._2
    dto.postId = 999
    dto.text = "Indeed"
    this.response = restTemplate.postForObject(s"/posts/${dto.postId}/comments", dto, classOf[String])
  }

  @When("you try to add a comment to this post without inform a user")
  def commentWithoutUser(): Unit = {
    val dto = new NewCommentDTO()
    dto.userUid = null
    dto.postId = postSteps.referencePost.id
    dto.text = "Indeed"
    this.response = restTemplate.postForObject(s"/posts/${dto.postId}/comments", dto, classOf[String])
  }

  @Then("the system will inform that user is required in a comment")
  def userIsRequiredInAComment(): Unit = {
    assertThat(this.response).startsWith("User cannot be null")
  }

  @Then("the system will inform that a comment only can be added to a valid post")
  def postIsInvalid(): Unit = {
    assertThat(this.response).startsWith("Post with id")
    assertThat(this.response).endsWith("is invalid")
  }

  @Then("comment will be added with text {string}")
  def commentAddedWithText(text: String): Unit = {
    val userUid = userSteps.users(this.username)

    val comments = this.postRepository.getPostById(postSteps.referencePost.id).comments
    assertThat(comments.length).isEqualTo(1)
    assertThat(comments.head.text).isEqualTo(text)
    assertThat(comments.head.ownerUid).isEqualTo(userUid)
  }

}
