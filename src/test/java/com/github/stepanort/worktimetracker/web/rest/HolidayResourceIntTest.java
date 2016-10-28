package com.github.stepanort.worktimetracker.web.rest;

import com.github.stepanort.worktimetracker.WorktimetrackerApp;

import com.github.stepanort.worktimetracker.domain.Holiday;
import com.github.stepanort.worktimetracker.repository.HolidayRepository;

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
 * Test class for the HolidayResource REST controller.
 *
 * @see HolidayResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WorktimetrackerApp.class)
public class HolidayResourceIntTest {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_DAYS = 1D;
    private static final Double UPDATED_DAYS = 2D;

    @Inject
    private HolidayRepository holidayRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restHolidayMockMvc;

    private Holiday holiday;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        HolidayResource holidayResource = new HolidayResource();
        ReflectionTestUtils.setField(holidayResource, "holidayRepository", holidayRepository);
        this.restHolidayMockMvc = MockMvcBuilders.standaloneSetup(holidayResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Holiday createEntity(EntityManager em) {
        Holiday holiday = new Holiday()
                .startDate(DEFAULT_START_DATE)
                .endDate(DEFAULT_END_DATE)
                .days(DEFAULT_DAYS);
        return holiday;
    }

    @Before
    public void initTest() {
        holiday = createEntity(em);
    }

    @Test
    @Transactional
    public void createHoliday() throws Exception {
        int databaseSizeBeforeCreate = holidayRepository.findAll().size();

        // Create the Holiday

        restHolidayMockMvc.perform(post("/api/holidays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(holiday)))
                .andExpect(status().isCreated());

        // Validate the Holiday in the database
        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).hasSize(databaseSizeBeforeCreate + 1);
        Holiday testHoliday = holidays.get(holidays.size() - 1);
        assertThat(testHoliday.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testHoliday.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testHoliday.getDays()).isEqualTo(DEFAULT_DAYS);
    }

    @Test
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = holidayRepository.findAll().size();
        // set the field null
        holiday.setStartDate(null);

        // Create the Holiday, which fails.

        restHolidayMockMvc.perform(post("/api/holidays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(holiday)))
                .andExpect(status().isBadRequest());

        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = holidayRepository.findAll().size();
        // set the field null
        holiday.setEndDate(null);

        // Create the Holiday, which fails.

        restHolidayMockMvc.perform(post("/api/holidays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(holiday)))
                .andExpect(status().isBadRequest());

        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDaysIsRequired() throws Exception {
        int databaseSizeBeforeTest = holidayRepository.findAll().size();
        // set the field null
        holiday.setDays(null);

        // Create the Holiday, which fails.

        restHolidayMockMvc.perform(post("/api/holidays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(holiday)))
                .andExpect(status().isBadRequest());

        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllHolidays() throws Exception {
        // Initialize the database
        holidayRepository.saveAndFlush(holiday);

        // Get all the holidays
        restHolidayMockMvc.perform(get("/api/holidays?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(holiday.getId().intValue())))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
                .andExpect(jsonPath("$.[*].days").value(hasItem(DEFAULT_DAYS.doubleValue())));
    }

    @Test
    @Transactional
    public void getHoliday() throws Exception {
        // Initialize the database
        holidayRepository.saveAndFlush(holiday);

        // Get the holiday
        restHolidayMockMvc.perform(get("/api/holidays/{id}", holiday.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(holiday.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.days").value(DEFAULT_DAYS.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingHoliday() throws Exception {
        // Get the holiday
        restHolidayMockMvc.perform(get("/api/holidays/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHoliday() throws Exception {
        // Initialize the database
        holidayRepository.saveAndFlush(holiday);
        int databaseSizeBeforeUpdate = holidayRepository.findAll().size();

        // Update the holiday
        Holiday updatedHoliday = holidayRepository.findOne(holiday.getId());
        updatedHoliday
                .startDate(UPDATED_START_DATE)
                .endDate(UPDATED_END_DATE)
                .days(UPDATED_DAYS);

        restHolidayMockMvc.perform(put("/api/holidays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedHoliday)))
                .andExpect(status().isOk());

        // Validate the Holiday in the database
        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).hasSize(databaseSizeBeforeUpdate);
        Holiday testHoliday = holidays.get(holidays.size() - 1);
        assertThat(testHoliday.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testHoliday.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testHoliday.getDays()).isEqualTo(UPDATED_DAYS);
    }

    @Test
    @Transactional
    public void deleteHoliday() throws Exception {
        // Initialize the database
        holidayRepository.saveAndFlush(holiday);
        int databaseSizeBeforeDelete = holidayRepository.findAll().size();

        // Get the holiday
        restHolidayMockMvc.perform(delete("/api/holidays/{id}", holiday.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).hasSize(databaseSizeBeforeDelete - 1);
    }
}
