package np.sumit.PersonalExpenseTrackerAPI.repository;

import np.sumit.PersonalExpenseTrackerAPI.entity.ERole;
import np.sumit.PersonalExpenseTrackerAPI.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(ERole role);
    boolean existsByRole(ERole role);
}
