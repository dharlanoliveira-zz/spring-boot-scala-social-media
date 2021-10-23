package dharlanoliveira.mysocialmedia.acceptance.cucumber.features

import dharlanoliveira.mysocialmedia.application.domain.Comment
import dharlanoliveira.mysocialmedia.application.dto.{DeleteCommentDTO, NewCommentDTO, UpdateCommentDTO}
import dharlanoliveira.mysocialmedia.repository.PostRepository
import io.cucumber.java.en.{Given, Then, When}
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.{HttpEntity, HttpMethod}

class CommentSteps(userSteps: UserSteps, postSteps: PostSteps) {

  @Autowired
  var restTemplate: TestRestTemplate = _

  @Autowired
  var postRepository: PostRepository = _

  var referenceComment : Comment = _

  var response : String = _

  var username: String = _

  @Given("there is a comment {string} on this post from the user {string}")
  def postWithCommentFromAUser(text: String, username: String): Unit = {
    val post = postSteps.referencePost
    post.addComment(text,userSteps.users(username))
    postRepository.save(post)
    val savedPost = this.postRepository.getPostById(post.id)
    referenceComment = savedPost.comments.head
  }

  @When("the user {string} add a comment with text {string} to this post")
  def userAddCommentToPost(username: String, text: String): Unit = {
    this.username = username

    val dto = new NewCommentDTO()
    dto.userUid = userSteps.users(username)
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

  @When("the user {string} change the text of this comment to {string}")
  def userUpdateComment(username: String, text: String): Unit = {
    val dto = new UpdateCommentDTO()
    dto.id = this.referenceComment.id
    dto.userUid = userSteps.users(username)
    dto.postId = postSteps.referencePost.id
    dto.text = text
    this.response = restTemplate.postForObject(s"/posts/${dto.postId}/comments/${dto.id}", dto, classOf[String])
  }

  @When("the user {string} delete this comment")
  def deleteComment(username: String): Unit = {
    val dto = new DeleteCommentDTO()
    dto.id = this.referenceComment.id
    dto.postId = this.postSteps.referencePost.id
    dto.userUid = userSteps.users(username)

    val request = new HttpEntity[DeleteCommentDTO](dto)
    val responseEntity = restTemplate.exchange(s"/posts/${dto.id}/comments/${dto.id}", HttpMethod.DELETE, request, classOf[String])
    this.response = responseEntity.getBody
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

  @Then("this post will have two comments")
  def postWithTwoComments(): Unit = {
    val comments = this.postRepository.getPostById(postSteps.referencePost.id).comments
    assertThat(comments.length).isEqualTo(2)
    assertThat(comments(0).text).isEqualTo("first comment")
    assertThat(comments(1).text).isEqualTo("second comment")
  }

  @Then("this comment will have the text {string}")
  def commentWillHaveText(text: String): Unit = {
    val comments = this.postRepository.getPostById(postSteps.referencePost.id).comments
    assertThat(comments.length).isEqualTo(1)
    val comment = comments.head
    assertThat(comment.text).isEqualTo(text)
  }

  @Then("the system will inform that only the owner can edit the comment")
  def onlyOwnerCanEditComments(): Unit = {
    assertThat(this.response).isEqualTo("Only same user can edit the comment")
  }

  @Then("will registered a mention to {string} in this comment")
  def mentionRegistered(userUid: String): Unit = {
    val postId = postSteps.referencePost.id
    val post = postRepository.getPostById(postId)
    assertNotNull(post)
    assertThat(post.comments.length).isEqualTo(1)
    assertThat(post.comments(0).usersUidMentions.find( uid => uid == userUid).size).isEqualTo(1)
  }

  @Then("there will be no mentions of users in this comment")
  def noMentionsOfUsers(): Unit = {
    val postId = postSteps.referencePost.id
    val post = postRepository.getPostById(postId)
    assertNotNull(post)
    assertThat(post.comments.length).isEqualTo(1)
    assertThat(post.comments(0).usersUidMentions.length).isEqualTo(0)
  }

  @Then("comment will be excluded")
  def commentExcluded(): Unit = {
    val postId = postSteps.referencePost.id
    val post = postRepository.getPostById(postId)
    assertNotNull(post)
    assertThat(post.comments.length).isEqualTo(0)
  }

  @Then("the system will inform that only the owner can delete the comment")
  def onlyOwnerCanDeleteComments(): Unit = {
    assertThat(this.response).isEqualTo("Only same user can delete the comment")
  }

}
