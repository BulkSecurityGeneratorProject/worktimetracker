package com.github.stepanort.worktimetracker.web.rest;

import com.github.stepanort.worktimetracker.WorktimetrackerApp;

import com.github.stepanort.worktimetracker.domain.Expense;
import com.github.stepanort.worktimetracker.repository.ExpenseRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ExpenseResource REST controller.
 *
 * @see ExpenseResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WorktimetrackerApp.class)
public class ExpenseResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_VALUE = 1D;
    private static final Double UPDATED_VALUE = 2D;

    private static final byte[] DEFAULT_RECEIPT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_RECEIPT = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_RECEIPT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_RECEIPT_CONTENT_TYPE = "image/png";

    @Inject
    private ExpenseRepository expenseRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restExpenseMockMvc;

    private Expense expense;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ExpenseResource expenseResource = new ExpenseResource();
        ReflectionTestUtils.setField(expenseResource, "expenseRepository", expenseRepository);
        this.restExpenseMockMvc = MockMvcBuilders.standaloneSetup(expenseResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expense createEntity(EntityManager em) {
        Expense expense = new Expense()
                .name(DEFAULT_NAME)
                .date(DEFAULT_DATE)
                .value(DEFAULT_VALUE)
                .receipt(DEFAULT_RECEIPT)
                .receiptContentType(DEFAULT_RECEIPT_CONTENT_TYPE);
        return expense;
    }

    @Before
    public void initTest() {
        expense = createEntity(em);
    }

    @Test
    @Transactional
    public void createExpense() throws Exception {
        int databaseSizeBeforeCreate = expenseRepository.findAll().size();

        // Create the Expense

        restExpenseMockMvc.perform(post("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expense)))
                .andExpect(status().isCreated());

        // Validate the Expense in the database
        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeCreate + 1);
        Expense testExpense = expenses.get(expenses.size() - 1);
        assertThat(testExpense.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testExpense.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testExpense.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testExpense.getReceipt()).isEqualTo(DEFAULT_RECEIPT);
        assertThat(testExpense.getReceiptContentType()).isEqualTo(DEFAULT_RECEIPT_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().size();
        // set the field null
        expense.setName(null);

        // Create the Expense, which fails.

        restExpenseMockMvc.perform(post("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expense)))
                .andExpect(status().isBadRequest());

        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().size();
        // set the field null
        expense.setDate(null);

        // Create the Expense, which fails.

        restExpenseMockMvc.perform(post("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expense)))
                .andExpect(status().isBadRequest());

        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().size();
        // set the field null
        expense.setValue(null);

        // Create the Expense, which fails.

        restExpenseMockMvc.perform(post("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expense)))
                .andExpect(status().isBadRequest());

        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllExpenses() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenses
        restExpenseMockMvc.perform(get("/api/expenses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(expense.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.doubleValue())))
                .andExpect(jsonPath("$.[*].receiptContentType").value(hasItem(DEFAULT_RECEIPT_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].receipt").value(hasItem(Base64Utils.encodeToString(DEFAULT_RECEIPT))));
    }

    @Test
    @Transactional
    public void getExpense() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get the expense
        restExpenseMockMvc.perform(get("/api/expenses/{id}", expense.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(expense.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.doubleValue()))
            .andExpect(jsonPath("$.receiptContentType").value(DEFAULT_RECEIPT_CONTENT_TYPE))
            .andExpect(jsonPath("$.receipt").value(Base64Utils.encodeToString(DEFAULT_RECEIPT)));
    }

    @Test
    @Transactional
    public void getNonExistingExpense() throws Exception {
        // Get the expense
        restExpenseMockMvc.perform(get("/api/expenses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateExpense() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);
        int databaseSizeBeforeUpdate = expenseRepository.findAll().size();

        // Update the expense
        Expense updatedExpense = expenseRepository.findOne(expense.getId());
        updatedExpense
                .name(UPDATED_NAME)
                .date(UPDATED_DATE)
                .value(UPDATED_VALUE)
                .receipt(UPDATED_RECEIPT)
                .receiptContentType(UPDATED_RECEIPT_CONTENT_TYPE);

        restExpenseMockMvc.perform(put("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedExpense)))
                .andExpect(status().isOk());

        // Validate the Expense in the database
        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeUpdate);
        Expense testExpense = expenses.get(expenses.size() - 1);
        assertThat(testExpense.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testExpense.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testExpense.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testExpense.getReceipt()).isEqualTo(UPDATED_RECEIPT);
        assertThat(testExpense.getReceiptContentType()).isEqualTo(UPDATED_RECEIPT_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void deleteExpense() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);
        int databaseSizeBeforeDelete = expenseRepository.findAll().size();

        // Get the expense
        restExpenseMockMvc.perform(delete("/api/expenses/{id}", expense.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeDelete - 1);
    }
}
