package dharlanoliveira.mysocialmedia.acceptance

import dharlanoliveira.mysocialmedia.Application
import dharlanoliveira.mysocialmedia.acceptance.listener.CleanDatabaseBetweenScenarios
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.{SpringBootContextLoader, SpringBootTest}
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.{Bean, Primary}
import org.springframework.test.context.{ContextConfiguration, TestExecutionListeners}

@CucumberContextConfiguration
@ContextConfiguration(classes = Array(classOf[Application]), loader = classOf[SpringBootContextLoader])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(value = Array(classOf[CleanDatabaseBetweenScenarios]))
class AcceptanceTestConfiguration {

  @Bean
  @Primary
  def testRestTemplate() : TestRestTemplate = {
    val restTemplate = new TestRestTemplate()
    restTemplate.getRestTemplate.setRequestFactory(new org.springframework.http.client.HttpComponentsClientHttpRequestFactory())
    restTemplate
  }

}
