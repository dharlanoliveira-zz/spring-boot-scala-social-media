package dharlanoliveira.mysocialmedia.acceptance.cucumber.features

import dharlanoliveira.mysocialmedia.application.dto.NewPostDTO
import dharlanoliveira.mysocialmedia.repository.PostRepository
import io.cucumber.java.en.{Then, When}
import org.apache.commons.io.IOUtils
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.ClassPathResource

import java.util.Base64

class NewPostSteps(userSteps: UserSteps) {

  @Autowired
  var restTemplate: TestRestTemplate = _

  @Autowired
  var postRepository: PostRepository = _

  var response : String = _

  var imageSize: Int = _

  var postId: Long = _

  var post: NewPostDTO = _

  @When("I try to register a post without inform user")
  def newPostWithoutUser(): Unit = {
    val dto = new NewPostDTO()
    dto.userUid = null
    dto.imageBase64 = base64Image("lapis.png")
    dto.text="Post text"

    this.response = restTemplate.postForObject("/posts", dto, classOf[String])
  }

  @When("I try to create a post using a invalid user")
  def newPostWithInvalidUser(): Unit = {
    val dto = new NewPostDTO()
    dto.userUid = "shjgdjahsdgjs"
    dto.imageBase64 = null
    dto.text="Post text"

    this.response = restTemplate.postForObject("/posts", dto, classOf[String])
  }

  @When("This user try to register a post without inform text")
  def newPostWithoutText(): Unit = {
    val dto = new NewPostDTO()
    dto.userUid = userSteps.users.head._2
    dto.imageBase64=base64Image("lapis.png")
    dto.text=null

    this.response = restTemplate.postForObject("/posts", dto, classOf[String])
  }

  @When("This user try to register a post without inform a image")
  def newPostWithoutImage(): Unit = {
    val dto = new NewPostDTO()
    dto.userUid = userSteps.users.head._2
    dto.imageBase64=null
    dto.text="Any text"

    this.post = dto

    this.response = restTemplate.postForObject("/posts", dto, classOf[String])
  }


  @When("This user try to register a post with text {string} and a image")
  def postWithText(postText: String): Unit = {
    val dto = new NewPostDTO()
    dto.userUid = userSteps.users.head._2
    dto.imageBase64=base64Image("lapis.png")
    dto.text=postText

    this.imageSize=fileSize("lapis.png")

    this.post = dto

    this.response = restTemplate.postForObject("/posts", dto, classOf[String])
  }

  @Then("the system will inform that user is required")
  def userUidCannotBeNull(): Unit = {
    val posts = postRepository.getAll
    assertThat(this.response).startsWith("User cannot be ")
    assertThat(posts.size).isEqualTo(0L)
  }

  @Then("the system will inform that a correct user is required")
  def userCannotBeInvalid(): Unit = {
    val posts = postRepository.getAll
    assertThat(this.response).startsWith("User")
    assertThat(this.response).endsWith("is invalid")
    assertThat(posts.size).isEqualTo(0L)
  }

  @Then("the system will inform that text is required")
  def textCannotBeNull(): Unit = {
    val posts = postRepository.getAll
    assertThat(this.response).startsWith("Text cannot be ")
    assertThat(posts.size).isEqualTo(0L)
  }

  @Then("the post will be saved without image")
  def postWillBeSaved(): Unit = {
    val posts = postRepository.getAll
    assertThat(posts.size).isEqualTo(1L)
    val firstPost = posts.head
    assertThat(firstPost.instant).isNotNull
    assertThat(firstPost.text).isEqualTo(this.post.text)
    assertThat(firstPost.ownerUid).isEqualTo(userSteps.users.head._2)
    assertThat(firstPost.image).isNull()
  }

  @Then("the post will be saved and image will be scalled down")
  def postWillBeSavedAndImageScalledDown(): Unit = {
    val posts = postRepository.getAll
    assertThat(posts.size).isEqualTo(1L)
    val firstPost = posts.head
    assertThat(firstPost.instant).isNotNull
    assertThat(firstPost.text).isEqualTo(this.post.text)
    assertThat(firstPost.ownerUid).isEqualTo(userSteps.users.head._2)
    assertThat(firstPost.image.readAllBytes().length).isLessThan(this.imageSize)
  }

  def base64Image(path: String): String = {
    val inputStream = new ClassPathResource(path, this.getClass.getClassLoader).getInputStream
    Base64.getEncoder.encodeToString(IOUtils.toByteArray(inputStream))
  }


  def fileSize(path: String): Int = {
    val inputStream = new ClassPathResource(path, this.getClass.getClassLoader).getInputStream
    IOUtils.length(inputStream.readAllBytes())
  }
}
