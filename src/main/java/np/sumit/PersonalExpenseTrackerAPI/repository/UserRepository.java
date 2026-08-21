package np.sumit.PersonalExpenseTrackerAPI.repository;

import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
