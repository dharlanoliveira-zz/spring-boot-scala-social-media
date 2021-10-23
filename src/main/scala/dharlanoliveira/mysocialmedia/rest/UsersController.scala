package dharlanoliveira.mysocialmedia.rest

import dharlanoliveira.mysocialmedia.application.UserApplicationService
import dharlanoliveira.mysocialmedia.application.dto.{UserIdDTO, UserRegistrationCommand}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.{PostMapping, RequestBody, ResponseStatus, RestController}

@RestController
class UsersController {

  @Autowired
  var applicationService: UserApplicationService = _

  /**
   * Endpoint that allows new users to be registered to my-social-media
   *
   * @param user Information - mail and username - about new user
   * @return User id
   */
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(path = Array("/users/registration"))
  def registration(@RequestBody user: UserRegistrationCommand): UserIdDTO = {
    applicationService.newUserRegistration(user)
  }

}
