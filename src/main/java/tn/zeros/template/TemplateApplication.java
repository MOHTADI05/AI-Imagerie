package tn.zeros.template;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.zeros.template.entities.Role;
import tn.zeros.template.entities.enums.TypeRole;
import tn.zeros.template.repositories.RoleRepository;
import tn.zeros.template.repositories.UserRepository;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
@EnableAsync
public class TemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class, args);
    }

    /////////////////////////////////////// Roles to be added by default on startup ///////////////////////////////////////////////////////
    @Bean
    CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder){
        return args -> {
            //set every user's password in the database to "password" for testing purposes
			/*userRepository.findAll().forEach(user -> {
				user.setPassword(encoder.encode("password"));
				userRepository.save(user);
			});*/
            if(roleRepository.findByType(TypeRole.ADMIN).isPresent() && roleRepository.findByType(TypeRole.USER).isPresent() && roleRepository.findByType(TypeRole.AGENT).isPresent()) return;
            roleRepository.save(new Role(null, "ADMIN", TypeRole.ADMIN));
            roleRepository.save(new Role(null, "USER", TypeRole.USER));
            roleRepository.save(new Role(null, "USER", TypeRole.AGENT));
        };


    }

}
