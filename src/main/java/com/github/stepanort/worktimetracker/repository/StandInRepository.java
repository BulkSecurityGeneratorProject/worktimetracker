package com.github.stepanort.worktimetracker.repository;

import com.github.stepanort.worktimetracker.domain.StandIn;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the StandIn entity.
 */
@SuppressWarnings("unused")
public interface StandInRepository extends JpaRepository<StandIn,Long> {

}
