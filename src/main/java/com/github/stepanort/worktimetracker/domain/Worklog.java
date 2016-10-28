package com.github.stepanort.worktimetracker.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Worklog.
 */
@Entity
@Table(name = "worklog")
public class Worklog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "hours", nullable = false)
    private Double hours;

    @Column(name = "comment")
    private String comment;

    @NotNull
    @Column(name = "approved", nullable = false)
    private Boolean approved;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Worklog date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getHours() {
        return hours;
    }

    public Worklog hours(Double hours) {
        this.hours = hours;
        return this;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public String getComment() {
        return comment;
    }

    public Worklog comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean isApproved() {
        return approved;
    }

    public Worklog approved(Boolean approved) {
        this.approved = approved;
        return this;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Project getProject() {
        return project;
    }

    public Worklog project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public Worklog user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Worklog worklog = (Worklog) o;
        if(worklog.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, worklog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Worklog{" +
            "id=" + id +
            ", date='" + date + "'" +
            ", hours='" + hours + "'" +
            ", comment='" + comment + "'" +
            ", approved='" + approved + "'" +
            '}';
    }
}
