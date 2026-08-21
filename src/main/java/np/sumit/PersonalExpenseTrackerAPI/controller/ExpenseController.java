package np.sumit.PersonalExpenseTrackerAPI.controller;

import np.sumit.PersonalExpenseTrackerAPI.dto.ExpenseRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.ExpenseResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.ExpenseSummaryResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.ExpenseTotalResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.Category;
import np.sumit.PersonalExpenseTrackerAPI.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> addExpense(@RequestBody ExpenseRequestDto expenseRequestDto) {
        ExpenseResponseDto responseDto = expenseService.createExpense(expenseRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDto>> getExpenses(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {

        return ResponseEntity.ok(
                expenseService.getExpenses(category, from, to)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> getExpenseById(@PathVariable Long id){
        ExpenseResponseDto responseDto = expenseService.getExpenseById(id);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(
            @PathVariable Long id, @RequestBody ExpenseRequestDto req) {
        ExpenseResponseDto responseDto = expenseService.updateExpense(id, req);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteExpenseById(@PathVariable Long id) {
        expenseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<ExpenseTotalResponseDto> getTotalExpensesAmount() {
        ExpenseTotalResponseDto resp = expenseService.getTotalExpense();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummaryResponseDto> getSummaryOfExpenses() {
        ExpenseSummaryResponseDto resp = expenseService.getSummaryOfExpenses();
        return ResponseEntity.ok(resp);
    }
}
