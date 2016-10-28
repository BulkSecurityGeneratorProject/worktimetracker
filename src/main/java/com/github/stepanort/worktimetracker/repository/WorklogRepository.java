package com.github.stepanort.worktimetracker.repository;

import com.github.stepanort.worktimetracker.domain.Worklog;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Worklog entity.
 */
@SuppressWarnings("unused")
public interface WorklogRepository extends JpaRepository<Worklog,Long> {

    @Query("select worklog from Worklog worklog where worklog.user.login = ?#{principal.username}")
    List<Worklog> findByUserIsCurrentUser();

}
