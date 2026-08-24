package np.sumit.PersonalExpenseTrackerAPI.mapper;

import np.sumit.PersonalExpenseTrackerAPI.dto.request.ExpenseRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.ExpenseResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.Expense;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {
    public Expense toEntity(ExpenseRequestDto req) {
        Expense expense = new Expense();
        expense.setTitle(req.getTitle());
        expense.setDescription(req.getDescription());
        expense.setAmount(req.getAmount());
        expense.setCategory(req.getCategory());
        expense.setExpenseDate(req.getExpenseDate());
        return expense;
    }

    public ExpenseResponseDto toResponseDto(Expense expense) {
        ExpenseResponseDto responseDto = new ExpenseResponseDto(expense);
        responseDto.setMessage("Expense added successfully");
        return responseDto;
    }

    public ExpenseResponseDto updateAndToResponseDto(ExpenseRequestDto req, Expense expense) {
        expense.setTitle(req.getTitle());
        expense.setDescription(req.getDescription());
        expense.setAmount(req.getAmount());
        expense.setCategory(req.getCategory());
        expense.setExpenseDate(req.getExpenseDate());
        return toResponseDto(expense);
    }
}
