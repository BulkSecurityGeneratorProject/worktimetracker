package com.github.stepanort.worktimetracker.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Expense.
 */
@Entity
@Table(name = "expense")
public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "value", nullable = false)
    private Double value;

    @Lob
    @Column(name = "receipt")
    private byte[] receipt;

    @Column(name = "receipt_content_type")
    private String receiptContentType;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Expense name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public Expense date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public Expense value(Double value) {
        this.value = value;
        return this;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public byte[] getReceipt() {
        return receipt;
    }

    public Expense receipt(byte[] receipt) {
        this.receipt = receipt;
        return this;
    }

    public void setReceipt(byte[] receipt) {
        this.receipt = receipt;
    }

    public String getReceiptContentType() {
        return receiptContentType;
    }

    public Expense receiptContentType(String receiptContentType) {
        this.receiptContentType = receiptContentType;
        return this;
    }

    public void setReceiptContentType(String receiptContentType) {
        this.receiptContentType = receiptContentType;
    }

    public User getUser() {
        return user;
    }

    public Expense user(User user) {
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
        Expense expense = (Expense) o;
        if(expense.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Expense{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", date='" + date + "'" +
            ", value='" + value + "'" +
            ", receipt='" + receipt + "'" +
            ", receiptContentType='" + receiptContentType + "'" +
            '}';
    }
}
