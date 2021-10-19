package dharlanoliveira.mysocialmedia.acceptance

import io.cucumber.junit.{Cucumber, CucumberOptions}
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(features = Array("classpath:features"))
class CucumberRunner {

}
