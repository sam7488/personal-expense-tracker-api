package np.sumit.PersonalExpenseTrackerAPI.service;

import np.sumit.PersonalExpenseTrackerAPI.dto.request.ExpenseRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.ExpenseResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.ExpenseSummaryResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.ExpenseTotalResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.*;
import np.sumit.PersonalExpenseTrackerAPI.exception.ExpenseNotFoundException;
import np.sumit.PersonalExpenseTrackerAPI.exception.InvalidDateRangeException;
import np.sumit.PersonalExpenseTrackerAPI.mapper.ExpenseMapper;
import np.sumit.PersonalExpenseTrackerAPI.repository.ExpenseRepository;
import np.sumit.PersonalExpenseTrackerAPI.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void shouldCreateExpense() {
        ExpenseRequestDto expenseRequestDto = new ExpenseRequestDto(
                "Lunch",
                "Lunch at restaurant",
                15.50,
                Category.FOOD,
                LocalDate.of(2026, 8, 24)
        );

        Role role = new Role(ERole.ROLE_USER);
        role.setId(1L);

        User user = new User(
                "ram", "ram@password", "ram@gmail.com"
        );
        user.setRoles(Set.of(role));
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        Expense expense = new Expense(
                "Lunch",
                "Lunch at restaurant",
                15.50,
                Category.FOOD,
                LocalDate.of(2026, 8, 24),
                user
        );
        expense.setId(1L);

        when(expenseMapper.toEntity(expenseRequestDto)).thenReturn(expense);
        expense.setUser(user);

        when(expenseRepository.save(expense)).thenReturn(expense);

        ExpenseResponseDto expenseResponseDto = new ExpenseResponseDto(expense);
        expenseResponseDto.setMessage("Expense added successfully");

        when(expenseMapper.toResponseDto(expense)).thenReturn(expenseResponseDto);

        ExpenseResponseDto result = expenseService.createExpense(expenseRequestDto);

        assertEquals(user, expense.getUser());
        assertEquals(expenseResponseDto, result);

        verify(currentUserService).getCurrentUser();
        verify(expenseMapper).toEntity(expenseRequestDto);
        verify(expenseRepository).save(expense);
        verify(expenseMapper).toResponseDto(expense);
    }

    // No Filters (All expenses for current user)
    @Test
    void shouldGetAllExpensesForCurrentUser() {
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        Expense expense1 = new Expense();
        expense1.setId(1L);

        Expense expense2 = new Expense();
        expense2.setId(2L);

        when(expenseRepository.findByUser(user)).thenReturn(List.of(expense1, expense2));

        ExpenseResponseDto expenseResponseDto1 = new ExpenseResponseDto(expense1);
        ExpenseResponseDto expenseResponseDto2 = new ExpenseResponseDto(expense2);

        when(expenseMapper.toResponseDto(expense1)).thenReturn(expenseResponseDto1);
        when(expenseMapper.toResponseDto(expense2)).thenReturn(expenseResponseDto2);

        List<ExpenseResponseDto> expected = List.of(expenseResponseDto1, expenseResponseDto2);

        List<ExpenseResponseDto> result = expenseService.getExpenses(null, null, null);

        assertEquals(expected, result);

        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUser(user);
        verify(expenseMapper).toResponseDto(expense1);
        verify(expenseMapper).toResponseDto(expense2);
    }

    // Category only
    @Test
    void shouldGetExpensesForCurrentUserByCategory() {
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        Category expenseCategory = Category.FOOD;

        Expense expense1 = new Expense();
        expense1.setUser(user);

        Expense expense2 = new Expense();
        expense2.setUser(user);

        when(expenseRepository.findByUserAndCategory(user, expenseCategory))
                .thenReturn(List.of(expense1, expense2));

        ExpenseResponseDto expenseResponseDto1 = new ExpenseResponseDto(expense1);
        ExpenseResponseDto expenseResponseDto2 = new ExpenseResponseDto(expense2);

        List<ExpenseResponseDto> expected = List.of(expenseResponseDto1, expenseResponseDto2);

        when(expenseMapper.toResponseDto(expense1)).thenReturn(expenseResponseDto1);
        when(expenseMapper.toResponseDto(expense2)).thenReturn(expenseResponseDto2);

        List<ExpenseResponseDto> result =
                expenseService.getExpenses(expenseCategory, null, null);

        assertEquals(expected, result);

        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndCategory(user, expenseCategory);
        verify(expenseMapper).toResponseDto(expense1);
        verify(expenseMapper).toResponseDto(expense2);
    }

    // from Date only
    @Test
    void shouldGetExpensesForCurrentUserByFromDate() {
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        LocalDate fromDate = LocalDate.of(2026, 8, 20);

        Expense expense1 = new Expense();
        expense1.setUser(user);

        Expense expense2 = new Expense();
        expense2.setUser(user);

        when(expenseRepository.findByUserAndExpenseDateGreaterThanEqual(user, fromDate))
                .thenReturn(List.of(expense1, expense2));

        ExpenseResponseDto expenseResponseDto1 = new ExpenseResponseDto(expense1);
        ExpenseResponseDto expenseResponseDto2 = new ExpenseResponseDto(expense2);

        List<ExpenseResponseDto> expected = List.of(expenseResponseDto1, expenseResponseDto2);

        when(expenseMapper.toResponseDto(expense1)).thenReturn(expenseResponseDto1);
        when(expenseMapper.toResponseDto(expense2)).thenReturn(expenseResponseDto2);

        List<ExpenseResponseDto> result =
                expenseService.getExpenses(null, fromDate, null);

        assertEquals(expected, result);

        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndExpenseDateGreaterThanEqual(user, fromDate);
        verify(expenseMapper).toResponseDto(expense1);
        verify(expenseMapper).toResponseDto(expense2);
    }

    // to Date only
    @Test
    void shouldGetExpensesForCurrentUserByToDate() {
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        LocalDate toDate = LocalDate.of(2026, 8, 24);

        Expense expense1 = new Expense();
        expense1.setUser(user);

        Expense expense2 = new Expense();
        expense2.setUser(user);

        when(expenseRepository.findByUserAndExpenseDateLessThanEqual(user, toDate))
                .thenReturn(List.of(expense1, expense2));

        ExpenseResponseDto expenseResponseDto1 = new ExpenseResponseDto(expense1);
        ExpenseResponseDto expenseResponseDto2 = new ExpenseResponseDto(expense2);

        List<ExpenseResponseDto> expected = List.of(expenseResponseDto1, expenseResponseDto2);

        when(expenseMapper.toResponseDto(expense1)).thenReturn(expenseResponseDto1);
        when(expenseMapper.toResponseDto(expense2)).thenReturn(expenseResponseDto2);

        List<ExpenseResponseDto> result =
                expenseService.getExpenses(null, null, toDate);

        assertEquals(expected, result);

        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndExpenseDateLessThanEqual(user, toDate);
        verify(expenseMapper).toResponseDto(expense1);
        verify(expenseMapper).toResponseDto(expense2);
    }

//    category + from
    @Test
    void shouldGetExpensesForCurrentUserByCategoryAndFromDate() {
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        Category expenseCategory = Category.FOOD;
        LocalDate fromDate = LocalDate.of(2026, 8, 17);

        Expense expense1 = new Expense();
        expense1.setUser(user);

        Expense expense2 = new Expense();
        expense2.setUser(user);

        when(expenseRepository
                .findByUserAndCategoryAndExpenseDateGreaterThanEqual(
                        user,
                        expenseCategory,
                        fromDate
                )
        ).thenReturn(List.of(expense1, expense2));

        ExpenseResponseDto expenseResponseDto1 = new ExpenseResponseDto(expense1);
        ExpenseResponseDto expenseResponseDto2 = new ExpenseResponseDto(expense2);

        List<ExpenseResponseDto> expected = List.of(expenseResponseDto1, expenseResponseDto2);

        when(expenseMapper.toResponseDto(expense1)).thenReturn(expenseResponseDto1);
        when(expenseMapper.toResponseDto(expense2)).thenReturn(expenseResponseDto2);

        List<ExpenseResponseDto> result =
                expenseService.getExpenses(
                        expenseCategory,
                        fromDate,
                        null
                );

        assertEquals(expected, result);

        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndCategoryAndExpenseDateGreaterThanEqual(
                user, expenseCategory, fromDate
        );
        verify(expenseMapper).toResponseDto(expense1);
        verify(expenseMapper).toResponseDto(expense2);
    }

//    category + to
    @Test
    void shouldGetExpensesForCurrentUserByCategoryAndToDate() {
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        Category expenseCategory = Category.FOOD;
        LocalDate toDate = LocalDate.of(2026, 8, 17);

        Expense expense1 = new Expense();
        expense1.setUser(user);

        Expense expense2 = new Expense();
        expense2.setUser(user);

        when(expenseRepository
                .findByUserAndCategoryAndExpenseDateLessThanEqual(
                        user,
                        expenseCategory,
                        toDate
                )
        ).thenReturn(List.of(expense1, expense2));

        ExpenseResponseDto expenseResponseDto1 = new ExpenseResponseDto(expense1);
        ExpenseResponseDto expenseResponseDto2 = new ExpenseResponseDto(expense2);

        List<ExpenseResponseDto> expected = List.of(expenseResponseDto1, expenseResponseDto2);

        when(expenseMapper.toResponseDto(expense1)).thenReturn(expenseResponseDto1);
        when(expenseMapper.toResponseDto(expense2)).thenReturn(expenseResponseDto2);

        List<ExpenseResponseDto> result =
                expenseService.getExpenses(
                        expenseCategory,
                        null,
                        toDate
                );

        assertEquals(expected, result);

        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndCategoryAndExpenseDateLessThanEqual(
                user, expenseCategory, toDate
        );
        verify(expenseMapper).toResponseDto(expense1);
        verify(expenseMapper).toResponseDto(expense2);
    }

//    from + to

    @Test
    void shouldGetExpensesForCurrentUserBetweenDates() {
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        LocalDate fromDate = LocalDate.of(2026, 7, 20);
        LocalDate toDate = LocalDate.of(2026, 8, 24);

        Expense expense1 = new Expense();
        expense1.setUser(user);

        Expense expense2 = new Expense();
        expense2.setUser(user);

        when(expenseRepository
                .findByUserAndExpenseDateBetween(
                        user,
                        fromDate,
                        toDate
                )
        ).thenReturn(List.of(expense1, expense2));

        ExpenseResponseDto expenseResponseDto1 = new ExpenseResponseDto(expense1);
        ExpenseResponseDto expenseResponseDto2 = new ExpenseResponseDto(expense2);

        List<ExpenseResponseDto> expected = List.of(expenseResponseDto1, expenseResponseDto2);

        when(expenseMapper.toResponseDto(expense1)).thenReturn(expenseResponseDto1);
        when(expenseMapper.toResponseDto(expense2)).thenReturn(expenseResponseDto2);

        List<ExpenseResponseDto> result =
                expenseService.getExpenses(
                        null,
                        fromDate,
                        toDate
                );

        assertEquals(expected, result);

        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndExpenseDateBetween(
                user, fromDate, toDate
        );
        verify(expenseMapper).toResponseDto(expense1);
        verify(expenseMapper).toResponseDto(expense2);
    }

//    Category + from + to
    @Test
    void shouldGetExpensesForCurrentUserByCategoryAndDateRange() {
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        Category expenseCategory = Category.ENTERTAINMENT;
        LocalDate fromDate = LocalDate.of(2026, 7, 20);
        LocalDate toDate = LocalDate.of(2026, 8, 24);

        Expense expense1 = new Expense();
        expense1.setUser(user);

        Expense expense2 = new Expense();
        expense2.setUser(user);

        when(expenseRepository
                .findByUserAndCategoryAndExpenseDateBetween(
                        user,
                        expenseCategory,
                        fromDate,
                        toDate
                )
        ).thenReturn(List.of(expense1, expense2));

        ExpenseResponseDto expenseResponseDto1 = new ExpenseResponseDto(expense1);
        ExpenseResponseDto expenseResponseDto2 = new ExpenseResponseDto(expense2);

        List<ExpenseResponseDto> expected = List.of(expenseResponseDto1, expenseResponseDto2);

        when(expenseMapper.toResponseDto(expense1)).thenReturn(expenseResponseDto1);
        when(expenseMapper.toResponseDto(expense2)).thenReturn(expenseResponseDto2);

        List<ExpenseResponseDto> result =
                expenseService.getExpenses(
                        expenseCategory,
                        fromDate,
                        toDate
                );

        assertEquals(expected, result);

        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndCategoryAndExpenseDateBetween(
                user, expenseCategory, fromDate, toDate
        );
        verify(expenseMapper).toResponseDto(expense1);
        verify(expenseMapper).toResponseDto(expense2);
    }

    @Test
    void shouldThrowExceptionForInvalidDateRange() {
        User user = new User();
        when(currentUserService.getCurrentUser()).thenReturn(user);

        LocalDate fromDate = LocalDate.of(2026, 8, 24);
        LocalDate toDate = LocalDate.of(2026, 8, 20);

        InvalidDateRangeException ex = assertThrows(
                InvalidDateRangeException.class,
                () -> expenseService.getExpenses(null, fromDate, toDate)
        );

        assertEquals("to Date cannot be before from", ex.getMessage());

        verify(currentUserService).getCurrentUser();
    }

    @Test
    void shouldUpdateExpense() {
        ExpenseRequestDto expenseRequestDto = new ExpenseRequestDto();
        Long expenseId = 1L;

        User user = new User();
        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        Expense existingExpense = new Expense();

        when(expenseRepository.findByUserAndId(user, expenseId))
                .thenReturn(Optional.of(existingExpense));

        ExpenseResponseDto expenseResponseDto = new ExpenseResponseDto(existingExpense);

        when(expenseMapper.updateAndToResponseDto(expenseRequestDto, existingExpense)).thenReturn(expenseResponseDto);

        ExpenseResponseDto result = expenseService.updateExpense(expenseId, expenseRequestDto);

        assertEquals(expenseResponseDto, result);
        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndId(user, expenseId);
        verify(expenseMapper).updateAndToResponseDto(expenseRequestDto, existingExpense);
    }

    @Test
    void shouldThrowExceptionIfExpenseDoesNotExistWhenUpdating() {
        ExpenseRequestDto expenseRequestDto = new ExpenseRequestDto();
        Long expenseId = 1L;

        User user = new User();
        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(expenseRepository.findByUserAndId(user, expenseId)).thenReturn(Optional.empty());

        ExpenseNotFoundException ex = assertThrows(
                ExpenseNotFoundException.class,
                () -> expenseService.updateExpense(expenseId, expenseRequestDto)
        );

        assertEquals("Expense with id: " + expenseId + " not found", ex.getMessage());

        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndId(user, 1L);
    }

    @Test
    void shouldDeleteExpense() {
        Long expenseId = 1L;
        Expense expense = new Expense();
        expense.setId(expenseId);

        User user = new User();
        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(expenseRepository.findByUserAndId(user, expenseId))
                .thenReturn(Optional.of(expense));

        expenseService.deleteById(expenseId);

        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndId(user, expenseId);
        verify(expenseRepository).delete(expense);
    }

    @Test
    void shouldThrowExceptionForInvalidExpenseIdWhenDeleting() {
        Long expenseId = 1L;
        Expense expense = new Expense();
        expense.setId(expenseId);

        User user = new User();
        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(expenseRepository.findByUserAndId(user, expenseId))
                .thenReturn(Optional.empty());

        ExpenseNotFoundException ex = assertThrows(
                ExpenseNotFoundException.class,
                () -> expenseService.deleteById(expenseId)
        );

        assertEquals("Expense with id: " + expenseId + " not found", ex.getMessage());
        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUserAndId(user, expenseId);
    }

    @Test
    void shouldGetTotalExpenses() {
        User user = new User();
        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        Expense expense1 = new Expense();
        expense1.setAmount(1000.50);

        Expense expense2 = new Expense();
        expense2.setAmount(499.50);

        List<Expense> expenses = List.of(expense1, expense2);

        when(expenseRepository.findByUser(user))
                .thenReturn(expenses);

        ExpenseTotalResponseDto totalExpense = new ExpenseTotalResponseDto(1500.0);

        ExpenseTotalResponseDto result = expenseService.getTotalExpense();

        assertEquals(totalExpense.getTotal(), result.getTotal());
        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUser(user);
    }

    @Test
    void shouldGetTotalExpensesZeroIfNoExpensesAdded() {
        User user = new User();
        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        List<Expense> expenses = new ArrayList<>();

        when(expenseRepository.findByUser(user))
                .thenReturn(expenses);

        ExpenseTotalResponseDto totalExpense = new ExpenseTotalResponseDto(0.0);

        ExpenseTotalResponseDto result = expenseService.getTotalExpense();

        assertEquals(totalExpense.getTotal(), result.getTotal());
        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUser(user);
    }

    @Test
    void ShouldGetSummaryOfExpenses() {
        User user = new User();
        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        Expense expense1 = new Expense();
        expense1.setCategory(Category.ENTERTAINMENT);
        expense1.setAmount(169.0);

        Expense expense2 = new Expense();
        expense2.setCategory(Category.TRANSPORT);
        expense2.setAmount(29.50);

        Expense expense3 = new Expense();
        expense3.setCategory(Category.FOOD);
        expense3.setAmount(120.0);

        Expense expense4 = new Expense();
        expense4.setCategory(Category.TRANSPORT);
        expense4.setAmount(40.0);

        List<Expense> expenses = List.of(expense1, expense2, expense3, expense4);

        when(expenseRepository.findByUser(user))
                .thenReturn(expenses);

        ExpenseSummaryResponseDto result = expenseService.getSummaryOfExpenses();

        assertEquals(358.5, result.getTotal());

        assertEquals(169, result.getCategories().get(Category.ENTERTAINMENT));
        assertEquals(69.50, result.getCategories().get(Category.TRANSPORT));
        assertEquals(120.0, result.getCategories().get(Category.FOOD));


        verify(currentUserService).getCurrentUser();
        verify(expenseRepository).findByUser(user);
    }
}
