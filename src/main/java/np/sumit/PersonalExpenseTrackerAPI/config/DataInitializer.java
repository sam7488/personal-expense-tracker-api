package np.sumit.PersonalExpenseTrackerAPI.config;

import np.sumit.PersonalExpenseTrackerAPI.entity.ERole;
import np.sumit.PersonalExpenseTrackerAPI.entity.Role;
import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import np.sumit.PersonalExpenseTrackerAPI.repository.RoleRepository;
import np.sumit.PersonalExpenseTrackerAPI.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            for (ERole role : ERole.values()) {
                if (!roleRepository.existsByRole(role)) {
                    roleRepository.save(new Role(role));
                }
            }
        };
    }

    @Bean
    CommandLineRunner initAdmin(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {

                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin123"));

                Role adminRole = roleRepository.findByRole(ERole.ROLE_ADMIN)
                        .orElseThrow();

                admin.getRoles().add(adminRole);

                userRepository.save(admin);
            }
        };
    }
}
