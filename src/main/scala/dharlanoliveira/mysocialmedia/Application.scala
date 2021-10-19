package dharlanoliveira.mysocialmedia

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application

object RunApplication extends App {

  val log = LoggerFactory.getLogger(classOf[Nothing])

  SpringApplication.run(classOf[Application])

  log.info(s"My social media microservice is running")
}