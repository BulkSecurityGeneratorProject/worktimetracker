package com.github.stepanort.worktimetracker.web.rest;

import com.github.stepanort.worktimetracker.WorktimetrackerApp;

import com.github.stepanort.worktimetracker.domain.Worklog;
import com.github.stepanort.worktimetracker.repository.WorklogRepository;

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
 * Test class for the WorklogResource REST controller.
 *
 * @see WorklogResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WorktimetrackerApp.class)
public class WorklogResourceIntTest {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_HOURS = 1D;
    private static final Double UPDATED_HOURS = 2D;

    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";

    private static final Boolean DEFAULT_APPROVED = false;
    private static final Boolean UPDATED_APPROVED = true;

    @Inject
    private WorklogRepository worklogRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restWorklogMockMvc;

    private Worklog worklog;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WorklogResource worklogResource = new WorklogResource();
        ReflectionTestUtils.setField(worklogResource, "worklogRepository", worklogRepository);
        this.restWorklogMockMvc = MockMvcBuilders.standaloneSetup(worklogResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Worklog createEntity(EntityManager em) {
        Worklog worklog = new Worklog()
                .date(DEFAULT_DATE)
                .hours(DEFAULT_HOURS)
                .comment(DEFAULT_COMMENT)
                .approved(DEFAULT_APPROVED);
        return worklog;
    }

    @Before
    public void initTest() {
        worklog = createEntity(em);
    }

    @Test
    @Transactional
    public void createWorklog() throws Exception {
        int databaseSizeBeforeCreate = worklogRepository.findAll().size();

        // Create the Worklog

        restWorklogMockMvc.perform(post("/api/worklogs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(worklog)))
                .andExpect(status().isCreated());

        // Validate the Worklog in the database
        List<Worklog> worklogs = worklogRepository.findAll();
        assertThat(worklogs).hasSize(databaseSizeBeforeCreate + 1);
        Worklog testWorklog = worklogs.get(worklogs.size() - 1);
        assertThat(testWorklog.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testWorklog.getHours()).isEqualTo(DEFAULT_HOURS);
        assertThat(testWorklog.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testWorklog.isApproved()).isEqualTo(DEFAULT_APPROVED);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = worklogRepository.findAll().size();
        // set the field null
        worklog.setDate(null);

        // Create the Worklog, which fails.

        restWorklogMockMvc.perform(post("/api/worklogs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(worklog)))
                .andExpect(status().isBadRequest());

        List<Worklog> worklogs = worklogRepository.findAll();
        assertThat(worklogs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHoursIsRequired() throws Exception {
        int databaseSizeBeforeTest = worklogRepository.findAll().size();
        // set the field null
        worklog.setHours(null);

        // Create the Worklog, which fails.

        restWorklogMockMvc.perform(post("/api/worklogs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(worklog)))
                .andExpect(status().isBadRequest());

        List<Worklog> worklogs = worklogRepository.findAll();
        assertThat(worklogs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkApprovedIsRequired() throws Exception {
        int databaseSizeBeforeTest = worklogRepository.findAll().size();
        // set the field null
        worklog.setApproved(null);

        // Create the Worklog, which fails.

        restWorklogMockMvc.perform(post("/api/worklogs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(worklog)))
                .andExpect(status().isBadRequest());

        List<Worklog> worklogs = worklogRepository.findAll();
        assertThat(worklogs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWorklogs() throws Exception {
        // Initialize the database
        worklogRepository.saveAndFlush(worklog);

        // Get all the worklogs
        restWorklogMockMvc.perform(get("/api/worklogs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(worklog.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS.doubleValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].approved").value(hasItem(DEFAULT_APPROVED.booleanValue())));
    }

    @Test
    @Transactional
    public void getWorklog() throws Exception {
        // Initialize the database
        worklogRepository.saveAndFlush(worklog);

        // Get the worklog
        restWorklogMockMvc.perform(get("/api/worklogs/{id}", worklog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(worklog.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.hours").value(DEFAULT_HOURS.doubleValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.approved").value(DEFAULT_APPROVED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingWorklog() throws Exception {
        // Get the worklog
        restWorklogMockMvc.perform(get("/api/worklogs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWorklog() throws Exception {
        // Initialize the database
        worklogRepository.saveAndFlush(worklog);
        int databaseSizeBeforeUpdate = worklogRepository.findAll().size();

        // Update the worklog
        Worklog updatedWorklog = worklogRepository.findOne(worklog.getId());
        updatedWorklog
                .date(UPDATED_DATE)
                .hours(UPDATED_HOURS)
                .comment(UPDATED_COMMENT)
                .approved(UPDATED_APPROVED);

        restWorklogMockMvc.perform(put("/api/worklogs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWorklog)))
                .andExpect(status().isOk());

        // Validate the Worklog in the database
        List<Worklog> worklogs = worklogRepository.findAll();
        assertThat(worklogs).hasSize(databaseSizeBeforeUpdate);
        Worklog testWorklog = worklogs.get(worklogs.size() - 1);
        assertThat(testWorklog.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testWorklog.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testWorklog.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testWorklog.isApproved()).isEqualTo(UPDATED_APPROVED);
    }

    @Test
    @Transactional
    public void deleteWorklog() throws Exception {
        // Initialize the database
        worklogRepository.saveAndFlush(worklog);
        int databaseSizeBeforeDelete = worklogRepository.findAll().size();

        // Get the worklog
        restWorklogMockMvc.perform(delete("/api/worklogs/{id}", worklog.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Worklog> worklogs = worklogRepository.findAll();
        assertThat(worklogs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
