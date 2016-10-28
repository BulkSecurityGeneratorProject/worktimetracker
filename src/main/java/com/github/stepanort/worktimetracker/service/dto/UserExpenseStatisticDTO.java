/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.stepanort.worktimetracker.service.dto;

import com.github.stepanort.worktimetracker.domain.User;

/**
 *
 * @author stepan.ort
 */
public class UserExpenseStatisticDTO {
    
    private User user;
            
    private double totalExpenses;
    
    public UserExpenseStatisticDTO(User user, Double totalExpenses) {
        this.user = user;
        this.totalExpenses = totalExpenses;
    }

    public User getUser() {
        return user;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

}
