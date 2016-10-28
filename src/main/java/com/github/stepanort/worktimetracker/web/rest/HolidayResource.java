package com.github.stepanort.worktimetracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.github.stepanort.worktimetracker.domain.Holiday;

import com.github.stepanort.worktimetracker.repository.HolidayRepository;
import com.github.stepanort.worktimetracker.web.rest.util.HeaderUtil;
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

/**
 * REST controller for managing Holiday.
 */
@RestController
@RequestMapping("/api")
public class HolidayResource {

    private final Logger log = LoggerFactory.getLogger(HolidayResource.class);
        
    @Inject
    private HolidayRepository holidayRepository;

    /**
     * POST  /holidays : Create a new holiday.
     *
     * @param holiday the holiday to create
     * @return the ResponseEntity with status 201 (Created) and with body the new holiday, or with status 400 (Bad Request) if the holiday has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/holidays",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Holiday> createHoliday(@Valid @RequestBody Holiday holiday) throws URISyntaxException {
        log.debug("REST request to save Holiday : {}", holiday);
        if (holiday.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("holiday", "idexists", "A new holiday cannot already have an ID")).body(null);
        }
        Holiday result = holidayRepository.save(holiday);
        return ResponseEntity.created(new URI("/api/holidays/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("holiday", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /holidays : Updates an existing holiday.
     *
     * @param holiday the holiday to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated holiday,
     * or with status 400 (Bad Request) if the holiday is not valid,
     * or with status 500 (Internal Server Error) if the holiday couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/holidays",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Holiday> updateHoliday(@Valid @RequestBody Holiday holiday) throws URISyntaxException {
        log.debug("REST request to update Holiday : {}", holiday);
        if (holiday.getId() == null) {
            return createHoliday(holiday);
        }
        Holiday result = holidayRepository.save(holiday);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("holiday", holiday.getId().toString()))
            .body(result);
    }

    /**
     * GET  /holidays : get all the holidays.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of holidays in body
     */
    @RequestMapping(value = "/holidays",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Holiday> getAllHolidays() {
        log.debug("REST request to get all Holidays");
        List<Holiday> holidays = holidayRepository.findAll();
        return holidays;
    }

    /**
     * GET  /holidays/:id : get the "id" holiday.
     *
     * @param id the id of the holiday to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the holiday, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/holidays/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Holiday> getHoliday(@PathVariable Long id) {
        log.debug("REST request to get Holiday : {}", id);
        Holiday holiday = holidayRepository.findOne(id);
        return Optional.ofNullable(holiday)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /holidays/:id : delete the "id" holiday.
     *
     * @param id the id of the holiday to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/holidays/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        log.debug("REST request to delete Holiday : {}", id);
        holidayRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("holiday", id.toString())).build();
    }

}
