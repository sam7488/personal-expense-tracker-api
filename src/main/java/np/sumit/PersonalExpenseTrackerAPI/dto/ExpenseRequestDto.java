package np.sumit.PersonalExpenseTrackerAPI.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import np.sumit.PersonalExpenseTrackerAPI.entity.Category;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ExpenseRequestDto {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Double amount;

    @NotNull
    private Category category;

    @NotNull
    private LocalDate expenseDate;
}
