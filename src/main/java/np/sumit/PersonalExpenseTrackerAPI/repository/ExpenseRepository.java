package np.sumit.PersonalExpenseTrackerAPI.repository;

import np.sumit.PersonalExpenseTrackerAPI.entity.Category;
import np.sumit.PersonalExpenseTrackerAPI.entity.Expense;
import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);

    Optional<Expense> findByUserAndId(
            User user, Long id
    );

    List<Expense> findByUserAndCategory(
            User user, Category category
    );

    List<Expense> findByUserAndCategoryAndExpenseDateBetween(
            User user, Category category, LocalDate from, LocalDate to
    );

    List<Expense> findByUserAndCategoryAndExpenseDateGreaterThanEqual(
            User user, Category category, LocalDate from
    );

    List<Expense> findByUserAndCategoryAndExpenseDateLessThanEqual(
            User user, Category category, LocalDate to
    );

    List<Expense> findByUserAndExpenseDateBetween(
            User user, LocalDate from, LocalDate to
    );

    List<Expense> findByUserAndExpenseDateGreaterThanEqual(
            User user, LocalDate from
    );

    List<Expense> findByUserAndExpenseDateLessThanEqual(
            User user, LocalDate to
    );

    boolean existsByUserAndId(User user, Long id);
}
