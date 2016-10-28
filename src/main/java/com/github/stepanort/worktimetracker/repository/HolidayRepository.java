package com.github.stepanort.worktimetracker.repository;

import com.github.stepanort.worktimetracker.domain.Holiday;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Holiday entity.
 */
@SuppressWarnings("unused")
public interface HolidayRepository extends JpaRepository<Holiday,Long> {

    @Query("select holiday from Holiday holiday where holiday.user.login = ?#{principal.username}")
    List<Holiday> findByUserIsCurrentUser();

}
