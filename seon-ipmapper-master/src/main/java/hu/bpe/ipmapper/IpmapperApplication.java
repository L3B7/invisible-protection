package hu.bpe.ipmapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;

@Controller
@EnableJpaRepositories()
@SpringBootApplication()
public class IpmapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(IpmapperApplication.class, args);
	}
}
///todo create feedback endpoint
