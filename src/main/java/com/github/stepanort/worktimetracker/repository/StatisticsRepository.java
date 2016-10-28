/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.stepanort.worktimetracker.repository;

import com.github.stepanort.worktimetracker.domain.StandIn;
import com.github.stepanort.worktimetracker.service.dto.UserExpenseStatisticDTO;
import com.github.stepanort.worktimetracker.service.dto.UserProjectWorktimeStatisticDTO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author stepan.ort
 */
public interface StatisticsRepository extends JpaRepository<StandIn,Long> {
    
    @Query("SELECT new com.github.stepanort.worktimetracker.service.dto.UserExpenseStatisticDTO(expense.user, SUM(expense.value)) FROM Expense expense GROUP BY expense.user")
    public List<UserExpenseStatisticDTO> getAllUserExpenseStatistics();
    
    @Query("SELECT new com.github.stepanort.worktimetracker.service.dto.UserProjectWorktimeStatisticDTO(worklog.user, worklog.project, SUM(worklog.hours)) FROM Worklog worklog GROUP BY worklog.user, worklog.project")
    public List<UserProjectWorktimeStatisticDTO> getAllUserProjectWorktimeStatistics();
}
