package com.github.stepanort.worktimetracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.github.stepanort.worktimetracker.domain.Worklog;

import com.github.stepanort.worktimetracker.repository.WorklogRepository;
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
 * REST controller for managing Worklog.
 */
@RestController
@RequestMapping("/api")
public class WorklogResource {

    private final Logger log = LoggerFactory.getLogger(WorklogResource.class);
        
    @Inject
    private WorklogRepository worklogRepository;

    /**
     * POST  /worklogs : Create a new worklog.
     *
     * @param worklog the worklog to create
     * @return the ResponseEntity with status 201 (Created) and with body the new worklog, or with status 400 (Bad Request) if the worklog has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/worklogs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Worklog> createWorklog(@Valid @RequestBody Worklog worklog) throws URISyntaxException {
        log.debug("REST request to save Worklog : {}", worklog);
        if (worklog.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("worklog", "idexists", "A new worklog cannot already have an ID")).body(null);
        }
        Worklog result = worklogRepository.save(worklog);
        return ResponseEntity.created(new URI("/api/worklogs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("worklog", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /worklogs : Updates an existing worklog.
     *
     * @param worklog the worklog to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated worklog,
     * or with status 400 (Bad Request) if the worklog is not valid,
     * or with status 500 (Internal Server Error) if the worklog couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/worklogs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Worklog> updateWorklog(@Valid @RequestBody Worklog worklog) throws URISyntaxException {
        log.debug("REST request to update Worklog : {}", worklog);
        if (worklog.getId() == null) {
            return createWorklog(worklog);
        }
        Worklog result = worklogRepository.save(worklog);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("worklog", worklog.getId().toString()))
            .body(result);
    }

    /**
     * GET  /worklogs : get all the worklogs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of worklogs in body
     */
    @RequestMapping(value = "/worklogs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Worklog> getAllWorklogs() {
        log.debug("REST request to get all Worklogs");
        List<Worklog> worklogs = worklogRepository.findAll();
        return worklogs;
    }

    /**
     * GET  /worklogs/:id : get the "id" worklog.
     *
     * @param id the id of the worklog to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the worklog, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/worklogs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Worklog> getWorklog(@PathVariable Long id) {
        log.debug("REST request to get Worklog : {}", id);
        Worklog worklog = worklogRepository.findOne(id);
        return Optional.ofNullable(worklog)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /worklogs/:id : delete the "id" worklog.
     *
     * @param id the id of the worklog to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/worklogs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteWorklog(@PathVariable Long id) {
        log.debug("REST request to delete Worklog : {}", id);
        worklogRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("worklog", id.toString())).build();
    }

}
