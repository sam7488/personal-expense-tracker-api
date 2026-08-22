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

        if(req.getCategory()!=null) {
            expense.setCategory(req.getCategory());
        }

        expense.setExpenseDate(req.getExpenseDate());
        return expense;
    }

    public ExpenseResponseDto toDto(Expense expense) {
        ExpenseResponseDto responseDto = new ExpenseResponseDto(expense);
        responseDto.setMessage("Expense added successfully");
        return responseDto;
    }

    public ExpenseResponseDto updateAndToResponseDto(ExpenseRequestDto req, Expense expense) {
        if(req.getTitle()!=null) {
            expense.setTitle(req.getTitle());
        }
        if(req.getDescription()!=null) {
            expense.setDescription(req.getDescription());
        }
        if(req.getAmount()!=null) {
            expense.setAmount(req.getAmount());
        }
        if(req.getCategory()!=null) {
            expense.setCategory(req.getCategory());
        }
        if(req.getExpenseDate()!=null) {
            expense.setExpenseDate(req.getExpenseDate());
        }

        ExpenseResponseDto responseDto = new ExpenseResponseDto(expense);
        responseDto.setMessage("Expense Updated successfully");

        return responseDto;
    }
}
