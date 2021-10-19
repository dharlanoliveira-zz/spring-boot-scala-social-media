package dharlanoliveira.mysocialmedia.rest

import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{GetMapping, RestController}

@RestController
class AliveController {

  @GetMapping(Array("/version"))
  def isAlive(): ResponseEntity[String] = new ResponseEntity[String]("Version 1.0",HttpStatus.OK)

}
