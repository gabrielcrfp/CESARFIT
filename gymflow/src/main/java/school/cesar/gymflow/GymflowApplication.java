package school.cesar.gymflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GymflowApplication {
    public static void main(String[] args) {
        SpringApplication.run(GymflowApplication.class, args);
    }
}
