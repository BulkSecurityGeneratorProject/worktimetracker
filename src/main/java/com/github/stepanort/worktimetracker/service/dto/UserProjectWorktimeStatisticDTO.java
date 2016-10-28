package com.github.stepanort.worktimetracker.service.dto;

import com.github.stepanort.worktimetracker.domain.Project;
import com.github.stepanort.worktimetracker.domain.User;

/**
 *
 * @author stepan.ort
 */
public class UserProjectWorktimeStatisticDTO {
    
    private User user;
    private Project project;
    private double totalWorkTime;

    public UserProjectWorktimeStatisticDTO(User user, Project project, double totalWorkTime) {
        this.user = user;
        this.project = project;
        this.totalWorkTime = totalWorkTime;
    }
    
    public User getUser() {
        return user;
    }

    public Project getProject() {
        return project;
    }

    public double getTotalWorkTime() {
        return totalWorkTime;
    }
    
}
