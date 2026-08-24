package np.sumit.PersonalExpenseTrackerAPI.service;

import np.sumit.PersonalExpenseTrackerAPI.dto.request.ExpenseRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.ExpenseResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.ExpenseSummaryResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.ExpenseTotalResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.Category;
import np.sumit.PersonalExpenseTrackerAPI.entity.Expense;
import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import np.sumit.PersonalExpenseTrackerAPI.exception.ExpenseNotFoundException;
import np.sumit.PersonalExpenseTrackerAPI.exception.InvalidDateRangeException;
import np.sumit.PersonalExpenseTrackerAPI.mapper.ExpenseMapper;
import np.sumit.PersonalExpenseTrackerAPI.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final CurrentUserService currentUserService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseMapper expenseMapper,
            CurrentUserService currentUserService
    ) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto) {
        User user = currentUserService.getCurrentUser();

        Expense expense = expenseMapper.toEntity(expenseRequestDto);
        expense.setUser(user);

        expenseRepository.save(expense);

        return expenseMapper.toResponseDto(expense);
    }

    public List<ExpenseResponseDto> getExpenses(Category category, LocalDate from, LocalDate to) {
        User user = currentUserService.getCurrentUser();

        if (from != null && to != null && to.isBefore(from)) {
            throw new InvalidDateRangeException("to Date cannot be before from");
        }

        List<Expense> expenses;
        if(category != null && from != null && to != null) {
            expenses = expenseRepository.findByUserAndCategoryAndExpenseDateBetween(user, category, from, to);
        }
        else if(category != null && from != null) {
            expenses = expenseRepository.findByUserAndCategoryAndExpenseDateGreaterThanEqual(user, category, from);
        }
        else if(category != null && to != null) {
            expenses = expenseRepository.findByUserAndCategoryAndExpenseDateLessThanEqual(user, category, to);
        }
        else if(from != null && to != null) {
            expenses = expenseRepository.findByUserAndExpenseDateBetween(user, from, to);
        }
        else if(from != null) {
            expenses = expenseRepository.findByUserAndExpenseDateGreaterThanEqual(user, from);
        }
        else if(to != null) {
            expenses = expenseRepository.findByUserAndExpenseDateLessThanEqual(user, to);
        }
        else if(category != null) {
            expenses = expenseRepository.findByUserAndCategory(user, category);
        }
        else {
            expenses = expenseRepository.findByUser(user);
        }

        return expenses.stream().map(expenseMapper::toResponseDto).toList();
    }

    @Transactional
    public ExpenseResponseDto updateExpense(Long id, ExpenseRequestDto expenseRequestDto) {
        User user = currentUserService.getCurrentUser();

        Expense existingExpense = expenseRepository.findByUserAndId(user, id)
                .orElseThrow(
                        () -> new ExpenseNotFoundException("Expense with id: " + id + " not found")
                );

        return expenseMapper.updateAndToResponseDto(expenseRequestDto, existingExpense);
    }

    public void deleteById(Long id) {
        User user = currentUserService.getCurrentUser();

        Expense expense = expenseRepository.findByUserAndId(user, id)
                .orElseThrow(
                        () -> new ExpenseNotFoundException("Expense with id: " + id + " not found")
                );

        expenseRepository.delete(expense);
    }

    public ExpenseTotalResponseDto getTotalExpense() {
        List<Expense> expenses = expenseRepository.findByUser(currentUserService.getCurrentUser());
        Double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        return new ExpenseTotalResponseDto(total);
    }

    public ExpenseSummaryResponseDto getSummaryOfExpenses() {
        List<Expense> expenses = expenseRepository.findByUser(currentUserService.getCurrentUser());
        Map<Category, Double> categories = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        Double total = categories.values().stream().mapToDouble(Double::doubleValue).sum();

        return new ExpenseSummaryResponseDto(total, categories);
    }
}
