package com.github.stepanort.worktimetracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.github.stepanort.worktimetracker.domain.StandIn;

import com.github.stepanort.worktimetracker.repository.StandInRepository;
import com.github.stepanort.worktimetracker.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing StandIn.
 */
@RestController
@RequestMapping("/api")
public class StandInResource {

    private final Logger log = LoggerFactory.getLogger(StandInResource.class);
        
    @Inject
    private StandInRepository standInRepository;

    /**
     * POST  /stand-ins : Create a new standIn.
     *
     * @param standIn the standIn to create
     * @return the ResponseEntity with status 201 (Created) and with body the new standIn, or with status 400 (Bad Request) if the standIn has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/stand-ins",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StandIn> createStandIn(@RequestBody StandIn standIn) throws URISyntaxException {
        log.debug("REST request to save StandIn : {}", standIn);
        if (standIn.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("standIn", "idexists", "A new standIn cannot already have an ID")).body(null);
        }
        StandIn result = standInRepository.save(standIn);
        return ResponseEntity.created(new URI("/api/stand-ins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("standIn", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /stand-ins : Updates an existing standIn.
     *
     * @param standIn the standIn to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated standIn,
     * or with status 400 (Bad Request) if the standIn is not valid,
     * or with status 500 (Internal Server Error) if the standIn couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/stand-ins",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StandIn> updateStandIn(@RequestBody StandIn standIn) throws URISyntaxException {
        log.debug("REST request to update StandIn : {}", standIn);
        if (standIn.getId() == null) {
            return createStandIn(standIn);
        }
        StandIn result = standInRepository.save(standIn);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("standIn", standIn.getId().toString()))
            .body(result);
    }

    /**
     * GET  /stand-ins : get all the standIns.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of standIns in body
     */
    @RequestMapping(value = "/stand-ins",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<StandIn> getAllStandIns() {
        log.debug("REST request to get all StandIns");
        List<StandIn> standIns = standInRepository.findAll();
        return standIns;
    }

    /**
     * GET  /stand-ins/:id : get the "id" standIn.
     *
     * @param id the id of the standIn to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the standIn, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/stand-ins/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StandIn> getStandIn(@PathVariable Long id) {
        log.debug("REST request to get StandIn : {}", id);
        StandIn standIn = standInRepository.findOne(id);
        return Optional.ofNullable(standIn)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /stand-ins/:id : delete the "id" standIn.
     *
     * @param id the id of the standIn to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/stand-ins/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteStandIn(@PathVariable Long id) {
        log.debug("REST request to delete StandIn : {}", id);
        standInRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("standIn", id.toString())).build();
    }

}
