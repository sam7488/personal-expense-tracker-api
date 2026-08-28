package np.sumit.PersonalExpenseTrackerAPI.controller;

import np.sumit.PersonalExpenseTrackerAPI.config.SecurityConfig;
import np.sumit.PersonalExpenseTrackerAPI.dto.request.ExpenseRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.ExpenseResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.ExpenseSummaryResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.ExpenseTotalResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.Category;
import np.sumit.PersonalExpenseTrackerAPI.service.CustomUserDetailsService;
import np.sumit.PersonalExpenseTrackerAPI.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ExpenseController.class)
@Import(SecurityConfig.class)
public class ExpenseControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    ExpenseService expenseService;

    @Test
    void shouldRejectUnauthenticatedUser() throws Exception {
        ExpenseRequestDto expenseRequestDto = new ExpenseRequestDto(
                "title",
                "description",
                20.0,
                Category.FOOD,
                LocalDate.now()
        );
        mockMvc.perform(
                post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequestDto))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", roles = "USER")
    void shouldRejectExpenseIfTitleIsBlank() throws Exception {
        ExpenseRequestDto request = new ExpenseRequestDto(
                "",
                "description",
                220.50,
                Category.TRANSPORT,
                LocalDate.now()
        );

        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("title is required"));

        verifyNoInteractions(expenseService);
    }

    @Test
    @WithMockUser(username = "username", roles = "USER")
    void shouldRejectExpenseIfAmountIsNull() throws Exception {
        ExpenseRequestDto request = new ExpenseRequestDto(
                "title",
                "description",
                null,
                Category.TRANSPORT,
                LocalDate.now()
        );

        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Amount is required"));

        verifyNoInteractions(expenseService);
    }

    @Test
    @WithMockUser(username = "username", roles = "USER")
    void shouldRejectExpenseIfCategoryIsNull() throws Exception {
        ExpenseRequestDto request = new ExpenseRequestDto(
                "title",
                "description",
                220.50,
                null,
                LocalDate.now()
        );

        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("category is required"));

        verifyNoInteractions(expenseService);
    }

    @Test
    @WithMockUser(username = "username", roles = "USER")
    void shouldRejectExpenseIfExpenseDateIsNull() throws Exception {
        ExpenseRequestDto request = new ExpenseRequestDto(
                "title",
                "description",
                220.50,
                Category.BILLS,
                null
        );

        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("expenseDate is required"));

        verifyNoInteractions(expenseService);
    }

    @Test
    @WithMockUser(username = "username", roles = "USER")
    void shouldRejectExpenseIfExpenseDateIsFuture() throws Exception {
        ExpenseRequestDto request = new ExpenseRequestDto(
                "title",
                "description",
                220.50,
                Category.BILLS,
                LocalDate.of(2027, 1, 1)
        );

        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Expense date cannot be in the future"));

        verifyNoInteractions(expenseService);
    }

    @Test
    @WithMockUser(username = "username", roles = "USER")
    void shouldAddExpense() throws Exception {
        ExpenseRequestDto request = new ExpenseRequestDto(
                "title",
                "description",
                220.50,
                Category.TRANSPORT,
                LocalDate.now()
        );

        ExpenseResponseDto response = new ExpenseResponseDto();
        response.setId(1L);
        response.setTitle("title");
        response.setAmount(220.50);
        response.setCategory(Category.TRANSPORT);

        when(expenseService.createExpense(any(ExpenseRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.amount").value(220.50))
                .andExpect(jsonPath("$.category").value("TRANSPORT"));

        verify(expenseService).createExpense(any(ExpenseRequestDto.class));
    }

    @Test
    @WithMockUser(username = "username", roles = "USER")
    void shouldGetExpense() throws Exception {
        ExpenseResponseDto resp1 = new ExpenseResponseDto();
        resp1.setId(1L);
        resp1.setTitle("title");
        ExpenseResponseDto resp2 = new ExpenseResponseDto();
        resp2.setId(2L);
        resp2.setTitle("title2");


        List<ExpenseResponseDto> expenses = new ArrayList<>();
        expenses.add(resp1);
        expenses.add(resp2);

        when(expenseService.getExpenses(null, null, null))
                .thenReturn(expenses);

        mockMvc.perform(
                get("/api/expenses")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(resp1.getId()))
                .andExpect(jsonPath("$[0].title").value(resp1.getTitle()))
                .andExpect(jsonPath("$[1].id").value(resp2.getId()))
                .andExpect(jsonPath("$[1].title").value(resp2.getTitle()));

        verify(expenseService).getExpenses(null, null, null);
    }

    @Test
    @WithMockUser(username = "username", roles = "USER")
    void shouldUpdateExpenseSuccessfully() throws Exception {
        Long expenseId = 1L;
        ExpenseRequestDto expenseRequestDto = new ExpenseRequestDto(
                "title",
                "description",
                20.0,
                Category.TRANSPORT,
                LocalDate.now()
        );

        ExpenseResponseDto expectedResponse = new ExpenseResponseDto();
        expectedResponse.setId(expenseId);
        expectedResponse.setTitle(expenseRequestDto.getTitle());
        expectedResponse.setAmount(expenseRequestDto.getAmount());
        expectedResponse.setCategory(expenseRequestDto.getCategory());
        expectedResponse.setExpenseDate(expenseRequestDto.getExpenseDate());

        when(expenseService.updateExpense(eq(expenseId), any(ExpenseRequestDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(
                        put("/api/expenses/{id}", expenseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(expenseRequestDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(expenseService).updateExpense(eq(expenseId), any(ExpenseRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDeleteExpenseSuccessfully() throws Exception {
        Long expenseId = 1L;

        mockMvc.perform(
                delete("/api/expenses/{id}", expenseId)
        )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(expenseService).deleteById(expenseId);
    }

    @Test
    @WithMockUser
    void shouldGetTotalExpense() throws Exception {
        when(expenseService.getTotalExpense())
                .thenReturn(
                        new  ExpenseTotalResponseDto(12D)
                );

        mockMvc.perform(
                get("/api/expenses/total")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(12D));

        verify(expenseService).getTotalExpense();
    }

    @Test
    @WithMockUser
    void shouldGetSummaryOfExpenses() throws Exception {
        Map<Category, Double> expensesByCategory =
                Map.of(
                        Category.ENTERTAINMENT, 105.0,
                        Category.FOOD, 200.0
                );

        ExpenseSummaryResponseDto expectedResponse =
                new ExpenseSummaryResponseDto(305.0, expensesByCategory);

        when(expenseService.getSummaryOfExpenses())
                .thenReturn(expectedResponse);

        mockMvc.perform(
                get("/api/expenses/summary")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(305.0))
                .andExpect(jsonPath("$.categories.FOOD").value(200.0))
                .andExpect(content().json(
                        objectMapper.writeValueAsString(expectedResponse)
                ));

        verify(expenseService).getSummaryOfExpenses();
    }
}
