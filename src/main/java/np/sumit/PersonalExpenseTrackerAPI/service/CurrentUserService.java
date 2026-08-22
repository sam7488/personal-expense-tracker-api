package np.sumit.PersonalExpenseTrackerAPI.service;

import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import np.sumit.PersonalExpenseTrackerAPI.exception.UserNotFoundException;
import np.sumit.PersonalExpenseTrackerAPI.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {
    UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(
                        () -> new UserNotFoundException("User not found")
                );
    }
}
