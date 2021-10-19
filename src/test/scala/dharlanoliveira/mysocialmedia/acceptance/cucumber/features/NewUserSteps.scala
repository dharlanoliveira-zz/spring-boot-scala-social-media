package dharlanoliveira.mysocialmedia.acceptance.cucumber.features

import dharlanoliveira.mysocialmedia.application.domain.User
import io.cucumber.java.en.{Given, Then, When}
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.web.client.TestRestTemplate
import dharlanoliveira.mysocialmedia.application.dto.UserRegistrationDTO
import dharlanoliveira.mysocialmedia.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired

class NewUserSteps {

  @Autowired
  var restTemplate: TestRestTemplate = _

  @Autowired
  var userRepository: UserRepository = _

  var dto: UserRegistrationDTO = _

  var response : String = _

  @Given("There isn't any user registered yet")
  def thereIsntAnyUser(): Unit = {

  }

  @Given("There is a user with username {string}")
  def thereIsUserWithUsername(username: String): Unit = {
    val user = new User(username, "xxx@domain.com")

    userRepository.save(user)
  }

  @Given("There is a user with email {string}")
  def thereIsUserWithEmail(email: String): Unit = {
    val user = new User("login",email)

    userRepository.save(user)
  }

  @When("I register a user with username {string} and email {string}")
  def userWithUsernameAndMail(username: String, email: String): Unit = {
    dto = new UserRegistrationDTO()
    dto.username = username
    dto.email = email

    this.response = restTemplate.postForObject("/users/registration", dto, classOf[String])
  }

  @When("I register a new user with username {string}")
  def userWithUsername(username: String): Unit = {
    dto = new UserRegistrationDTO()
    dto.username = username
    dto.email = "anymail@google.com"

    this.response =  restTemplate.postForObject("/users/registration", dto, classOf[String])
  }

  @When("I register a new user with email {string}")
  def userWithEmail(email: String): Unit = {
    dto = new UserRegistrationDTO()
    dto.username = "ykz"
    dto.email = email

    this.response =  restTemplate.postForObject("/users/registration", dto, classOf[String])
  }

  @When("I register a user without username")
  def userWithoutUsername(): Unit = {
    dto = new UserRegistrationDTO()
    dto.username = null
    dto.email = "yzk@domain.com"

    this.response =  restTemplate.postForObject("/users/registration", dto, classOf[String])
  }

  @When("I register a user without email")
  def userWithoutEmail(): Unit = {
    dto = new UserRegistrationDTO()
    dto.username = "out"
    dto.email = null

    this.response =  restTemplate.postForObject("/users/registration", dto, classOf[String])
  }

  @Then("this new user will be created")
  def newUserCreated(): Unit = {
    val user = userRepository.getAll.head
    assertThat(user.id).isGreaterThan(0L)
    assertThat(user.username).isEqualTo(dto.username)
    assertThat(user.email).isEqualTo(dto.email)
  }

  @Then("the service will warn that username cannot be empty")
  def usernameCannotBeNull(): Unit = {
    val users = userRepository.getAll
    assertThat(this.response).startsWith("Username cannot be ")
    assertThat(users.size).isEqualTo(0L)
  }

  @Then("the service will warn that email cannot be empty")
  def emailCannotBeNull(): Unit = {
    val users = userRepository.getAll
    assertThat(this.response).startsWith("Email cannot be ")
    assertThat(users.size).isEqualTo(0L)
  }

  @Then("the service will warn that already exists a user registered with this username")
  def userAlreadyExistsWithUsername(): Unit = {
    val users = userRepository.getAll
    assertThat(this.response).startsWith("Already exists a user with name")
    assertThat(users.size).isEqualTo(1L)
  }

  @Then("the service will warn that already exists a user registered with this email")
  def userAlreadyExistsWithMail(): Unit = {
    val users = userRepository.getAll
    assertThat(this.response).startsWith("Already exists a user with email")
    assertThat(users.size).isEqualTo(1L)
  }
}
