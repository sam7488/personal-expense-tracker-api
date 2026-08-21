package np.sumit.PersonalExpenseTrackerAPI.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import np.sumit.PersonalExpenseTrackerAPI.entity.Category;
import np.sumit.PersonalExpenseTrackerAPI.entity.Expense;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonPropertyOrder({
        "id",
        "title",
        "category",
        "description",
        "amount",
        "expenseDate",
        "createdAt",
        "message"
})
@Getter
@Setter
@AllArgsConstructor
public class ExpenseResponseDto {
    private Long id;
    private String title;
    private String description;
    private Double amount;
    private Category category;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
    private String message;

    public ExpenseResponseDto(Expense expense) {
        this.id = expense.getId();
        this.title = expense.getTitle();
        this.description = expense.getDescription();
        this.amount = expense.getAmount();
        this.category = expense.getCategory();
        this.expenseDate = expense.getExpenseDate();
        this.createdAt = expense.getCreatedAt();
    }
}
