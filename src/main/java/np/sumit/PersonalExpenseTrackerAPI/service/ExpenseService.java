package np.sumit.PersonalExpenseTrackerAPI.service;

import np.sumit.PersonalExpenseTrackerAPI.dto.ExpenseRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.ExpenseResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.ExpenseSummaryResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.ExpenseTotalResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.Category;
import np.sumit.PersonalExpenseTrackerAPI.entity.Expense;
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

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @Transactional
    public ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto) {
        Expense expense = expenseMapper.toEntity(expenseRequestDto);
        expenseRepository.save(expense);
        return expenseMapper.toDto(expense);
    }

    public ExpenseResponseDto getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(
                        () -> new ExpenseNotFoundException("Expense with id: " + id + " not found")
                );
        return expenseMapper.toDto(expense);
    }

    public List<ExpenseResponseDto> getExpenses(Category category, LocalDate from, LocalDate to) {
        if (from != null && to != null && to.isBefore(from)) {
            throw new InvalidDateRangeException("to Date cannot be before from");
        }

        List<Expense> expenses;
        if(category != null && from != null && to != null) {
            expenses = expenseRepository.findByCategoryAndExpenseDateBetween(category, from, to);
        }
        else if(category != null && from != null) {
            expenses = expenseRepository.findByCategoryAndExpenseDateGreaterThanEqual(category, from);
        }
        else if(category != null && to != null) {
            expenses = expenseRepository.findByCategoryAndExpenseDateLessThanEqual(category, to);
        }
        else if(from != null && to != null) {
            expenses = expenseRepository.findByExpenseDateBetween(from, to);
        }
        else if(from != null) {
            expenses = expenseRepository.findByExpenseDateGreaterThanEqual(from);
        }
        else if(to != null) {
            expenses = expenseRepository.findByExpenseDateLessThanEqual(to);
        }
        else if(category != null) {
            expenses = expenseRepository.findByCategory(category);
        }
        else {
            expenses = expenseRepository.findAll();
        }

        return expenses.stream().map(expenseMapper::toDto).toList();
    }

    @Transactional
    public ExpenseResponseDto updateExpense(Long id, ExpenseRequestDto expenseRequestDto) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(
                        () -> new ExpenseNotFoundException("Expense with id: " + id + " not found")
                );

        return expenseMapper.updateAndToResponseDto(expenseRequestDto, existingExpense);
    }

    public void deleteById(Long id) {
        if(!expenseRepository.existsById(id)) {
            throw new ExpenseNotFoundException("Expense with id: " + id + " not found");
        }
        expenseRepository.deleteById(id);
    }

    public ExpenseTotalResponseDto getTotalExpense() {
        List<Expense> expenses = expenseRepository.findAll();
        Double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        return new ExpenseTotalResponseDto(total);
    }

    public ExpenseSummaryResponseDto getSummaryOfExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        Map<Category, Double> categories = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        Double total = categories.values().stream().mapToDouble(Double::doubleValue).sum();

        return new ExpenseSummaryResponseDto(total, categories);
    }
}
