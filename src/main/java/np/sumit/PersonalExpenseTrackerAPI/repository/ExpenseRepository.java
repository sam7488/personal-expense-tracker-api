package np.sumit.PersonalExpenseTrackerAPI.repository;

import np.sumit.PersonalExpenseTrackerAPI.entity.Category;
import np.sumit.PersonalExpenseTrackerAPI.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCategory(Category category);
    List<Expense> findByCategoryAndExpenseDateBetween(Category category, LocalDate from, LocalDate to);
    List<Expense> findByCategoryAndExpenseDateGreaterThanEqual(Category category, LocalDate from);
    List<Expense> findByCategoryAndExpenseDateLessThanEqual(Category category, LocalDate to);
    List<Expense> findByExpenseDateBetween(LocalDate from, LocalDate to);
    List<Expense> findByExpenseDateGreaterThanEqual(LocalDate from);
    List<Expense> findByExpenseDateLessThanEqual(LocalDate to);
}
