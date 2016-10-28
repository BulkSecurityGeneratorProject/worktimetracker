package com.github.stepanort.worktimetracker.web.rest;

import com.github.stepanort.worktimetracker.WorktimetrackerApp;

import com.github.stepanort.worktimetracker.domain.StandIn;
import com.github.stepanort.worktimetracker.repository.StandInRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StandInResource REST controller.
 *
 * @see StandInResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WorktimetrackerApp.class)
public class StandInResourceIntTest {

    @Inject
    private StandInRepository standInRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restStandInMockMvc;

    private StandIn standIn;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StandInResource standInResource = new StandInResource();
        ReflectionTestUtils.setField(standInResource, "standInRepository", standInRepository);
        this.restStandInMockMvc = MockMvcBuilders.standaloneSetup(standInResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StandIn createEntity(EntityManager em) {
        StandIn standIn = new StandIn();
        return standIn;
    }

    @Before
    public void initTest() {
        standIn = createEntity(em);
    }

    @Test
    @Transactional
    public void createStandIn() throws Exception {
        int databaseSizeBeforeCreate = standInRepository.findAll().size();

        // Create the StandIn

        restStandInMockMvc.perform(post("/api/stand-ins")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(standIn)))
                .andExpect(status().isCreated());

        // Validate the StandIn in the database
        List<StandIn> standIns = standInRepository.findAll();
        assertThat(standIns).hasSize(databaseSizeBeforeCreate + 1);
        StandIn testStandIn = standIns.get(standIns.size() - 1);
    }

    @Test
    @Transactional
    public void getAllStandIns() throws Exception {
        // Initialize the database
        standInRepository.saveAndFlush(standIn);

        // Get all the standIns
        restStandInMockMvc.perform(get("/api/stand-ins?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(standIn.getId().intValue())));
    }

    @Test
    @Transactional
    public void getStandIn() throws Exception {
        // Initialize the database
        standInRepository.saveAndFlush(standIn);

        // Get the standIn
        restStandInMockMvc.perform(get("/api/stand-ins/{id}", standIn.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(standIn.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingStandIn() throws Exception {
        // Get the standIn
        restStandInMockMvc.perform(get("/api/stand-ins/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStandIn() throws Exception {
        // Initialize the database
        standInRepository.saveAndFlush(standIn);
        int databaseSizeBeforeUpdate = standInRepository.findAll().size();

        // Update the standIn
        StandIn updatedStandIn = standInRepository.findOne(standIn.getId());

        restStandInMockMvc.perform(put("/api/stand-ins")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedStandIn)))
                .andExpect(status().isOk());

        // Validate the StandIn in the database
        List<StandIn> standIns = standInRepository.findAll();
        assertThat(standIns).hasSize(databaseSizeBeforeUpdate);
        StandIn testStandIn = standIns.get(standIns.size() - 1);
    }

    @Test
    @Transactional
    public void deleteStandIn() throws Exception {
        // Initialize the database
        standInRepository.saveAndFlush(standIn);
        int databaseSizeBeforeDelete = standInRepository.findAll().size();

        // Get the standIn
        restStandInMockMvc.perform(delete("/api/stand-ins/{id}", standIn.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<StandIn> standIns = standInRepository.findAll();
        assertThat(standIns).hasSize(databaseSizeBeforeDelete - 1);
    }
}
