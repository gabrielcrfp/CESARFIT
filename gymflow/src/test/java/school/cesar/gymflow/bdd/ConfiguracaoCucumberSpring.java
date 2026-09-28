package school.cesar.gymflow.bdd;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.spring.CucumberContextConfiguration;
import school.cesar.gymflow.GymflowApplication;

/** Liga o contexto do Spring Boot aos steps do Cucumber. */
@CucumberContextConfiguration
@SpringBootTest(classes = GymflowApplication.class)
@ActiveProfiles("test")
public class ConfiguracaoCucumberSpring {
}
