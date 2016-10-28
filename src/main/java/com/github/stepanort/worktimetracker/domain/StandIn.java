package com.github.stepanort.worktimetracker.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A StandIn.
 */
@Entity
@Table(name = "stand_in")
public class StandIn implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(unique = true)
    private Holiday forHoliday;

    @OneToOne
    @JoinColumn(unique = true)
    private User substitut;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Holiday getForHoliday() {
        return forHoliday;
    }

    public StandIn forHoliday(Holiday holiday) {
        this.forHoliday = holiday;
        return this;
    }

    public void setForHoliday(Holiday holiday) {
        this.forHoliday = holiday;
    }

    public User getSubstitut() {
        return substitut;
    }

    public StandIn substitut(User user) {
        this.substitut = user;
        return this;
    }

    public void setSubstitut(User user) {
        this.substitut = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StandIn standIn = (StandIn) o;
        if(standIn.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, standIn.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "StandIn{" +
            "id=" + id +
            '}';
    }
}
