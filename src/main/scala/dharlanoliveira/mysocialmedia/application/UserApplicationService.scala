package dharlanoliveira.mysocialmedia.application

import dharlanoliveira.mysocialmedia.application.domain.User
import dharlanoliveira.mysocialmedia.application.dto.{UserIdDTO, UserRegistrationDTO}
import dharlanoliveira.mysocialmedia.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserApplicationService {

  @Autowired
  var userRepository: UserRepository = _

  def newUserRegistration(userDTO: UserRegistrationDTO): UserIdDTO = {
    val user = new User(userDTO.username, userDTO.email, userDTO.password)

    if(userRepository.existsUserWithUsername(user.username)) {
      throw new BusinessViolation(s"Already exists a user with name ${userDTO.username}")
    } else if(userRepository.existsUserWithMail(user.email)) {
      throw new BusinessViolation(s"Already exists a user with email ${userDTO.username}")
    }

    new UserIdDTO(userRepository.save(user))
  }

}
