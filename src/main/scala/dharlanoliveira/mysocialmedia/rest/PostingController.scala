package dharlanoliveira.mysocialmedia.rest

import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{GetMapping, RestController}

@RestController
class PostingController {

  @GetMapping(Array("/hello"))
  def hello(): ResponseEntity[String] = new ResponseEntity[String]("Hello",HttpStatus.OK)

}
