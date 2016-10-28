package com.github.stepanort.worktimetracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.github.stepanort.worktimetracker.domain.Expense;

import com.github.stepanort.worktimetracker.repository.ExpenseRepository;
import com.github.stepanort.worktimetracker.web.rest.util.HeaderUtil;
import com.github.stepanort.worktimetracker.web.rest.util.ExcelUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import org.springframework.core.io.InputStreamResource;

/**
 * REST controller for managing Expense.
 */
@RestController
@RequestMapping("/api")
public class ExpenseResource {

    private final Logger log = LoggerFactory.getLogger(ExpenseResource.class);
        
    @Inject
    private ExpenseRepository expenseRepository;

    /**
     * POST  /expenses : Create a new expense.
     *
     * @param expense the expense to create
     * @return the ResponseEntity with status 201 (Created) and with body the new expense, or with status 400 (Bad Request) if the expense has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/expenses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) throws URISyntaxException {
        log.debug("REST request to save Expense : {}", expense);
        if (expense.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("expense", "idexists", "A new expense cannot already have an ID")).body(null);
        }
        Expense result = expenseRepository.save(expense);
        return ResponseEntity.created(new URI("/api/expenses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("expense", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /expenses : Updates an existing expense.
     *
     * @param expense the expense to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated expense,
     * or with status 400 (Bad Request) if the expense is not valid,
     * or with status 500 (Internal Server Error) if the expense couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/expenses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Expense> updateExpense(@Valid @RequestBody Expense expense) throws URISyntaxException {
        log.debug("REST request to update Expense : {}", expense);
        if (expense.getId() == null) {
            return createExpense(expense);
        }
        Expense result = expenseRepository.save(expense);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("expense", expense.getId().toString()))
            .body(result);
    }

    /**
     * GET  /expenses : get all the expenses.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of expenses in body
     */
    @RequestMapping(value = "/expenses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Expense> getAllExpenses() {
        log.debug("REST request to get all Expenses");
        List<Expense> expenses = expenseRepository.findAll();
        return expenses;
    }

    @RequestMapping(value = "/expenses/excel",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    public ResponseEntity<InputStreamResource> getExpensesExport() {
        log.debug("REST request to get expenses exported to Excel file");
        byte[] excelBytes;
        try {
            excelBytes = ExcelUtil.generate(expenseRepository.findAll());
            return ResponseEntity
                    .ok()
                    .contentLength(excelBytes.length)
                    .contentType(
                            MediaType.parseMediaType("application/octet-stream"))
                    .header("Content-Disposition", "attachment; filename=\"expenses.xlsx\"")
                    .body(new InputStreamResource(new ByteArrayInputStream(excelBytes)));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * GET  /expenses/:id : get the "id" expense.
     *
     * @param id the id of the expense to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the expense, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/expenses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Expense> getExpense(@PathVariable Long id) {
        log.debug("REST request to get Expense : {}", id);
        Expense expense = expenseRepository.findOne(id);
        return Optional.ofNullable(expense)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /expenses/:id : delete the "id" expense.
     *
     * @param id the id of the expense to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/expenses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        log.debug("REST request to delete Expense : {}", id);
        expenseRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("expense", id.toString())).build();
    }

}
