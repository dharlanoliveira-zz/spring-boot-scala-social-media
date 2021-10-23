package dharlanoliveira.mysocialmedia.acceptance.cucumber.features

import dharlanoliveira.mysocialmedia.application.domain.Post
import dharlanoliveira.mysocialmedia.application.dto.PostDTO
import dharlanoliveira.mysocialmedia.repository.PostRepository
import io.cucumber.java.en.{Given, Then, When}
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.jdk.javaapi.CollectionConverters.asScala

class ListPostsSteps(userSteps: UserSteps, postSteps: PostSteps) {

  @Autowired
  var restTemplate: TestRestTemplate = _

  @Autowired
  var postRepository: PostRepository = _

  var posts: List[PostDTO] = _
  var response: String = _


  @When("the user {string} requests their posts")
  def userRequestsTheirPosts(username: String): Unit = {
    val uid = userSteps.users(username)
    val response = restTemplate.exchange(s"/posts?userUid=${uid}",
      HttpMethod.GET, null, new ParameterizedTypeReference[java.util.List[PostDTO]]() {
      });

    this.posts = asScala(response.getBody).toList
  }

  @When("the user {string} requests all posts")
  def userRequestsAllPosts(username: String): Unit = {
    val response = restTemplate.exchange(s"/posts",
      HttpMethod.GET, null, new ParameterizedTypeReference[java.util.List[PostDTO]]() {
      });

    this.posts = asScala(response.getBody).toList
  }

  @When("the user {string} requests their posts in ascending order")
  def userRequestsTheirPostsInAscendingOrder(username: String): Unit = {
    val uid = userSteps.users(username)
    val response = restTemplate.exchange(s"/posts?userUid=${uid}&order=ASC",
      HttpMethod.GET, null, new ParameterizedTypeReference[java.util.List[PostDTO]]() {
      });

    this.posts = asScala(response.getBody).toList
  }

  @Then("the system will return {string} and {string} in this order")
  def twoPostsReturned(post1: String, post2: String): Unit = {
    assertThat(this.posts.size).isEqualTo(2)
    assertThat(this.posts(0).text).isEqualTo(post1)
    assertThat(this.posts(1).text).isEqualTo(post2)
  }

  @Then("the system will return {string}, {string} and {string} in this order")
  def threePostsReturned(post1: String, post2: String, post3:String): Unit = {
    assertThat(this.posts.size).isEqualTo(3)
    assertThat(this.posts(0).text).isEqualTo(post1)
    assertThat(this.posts(1).text).isEqualTo(post2)
    assertThat(this.posts(2).text).isEqualTo(post3)
  }

}
