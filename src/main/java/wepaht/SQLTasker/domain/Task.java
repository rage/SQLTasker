/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.domain;

import java.util.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Task extends AbstractPersistable<Long> implements Owned {
    @ManyToMany(mappedBy = "taskList", fetch = FetchType.EAGER)
    private List<Category> categories;

    @NotBlank
    @NotNull
    private String name;
    private String description;
    
    @Lob
    private String solution;

    @ManyToOne
    @NotNull
    private Database database;
    
    @OneToMany(mappedBy = "task")
    private List<Submission> submissions;
    
    @OneToMany(mappedBy = "task")
    private List<TaskFeedback> feedback;
    
    @ManyToOne
    private TmcAccount owner;
    
    @OneToMany(orphanRemoval = true, mappedBy = "task")
    private List<Tag> tags;
    
    private Boolean deleted;
    
    public Task() {
        this.deleted = false;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    /**
     * @return the solution
     */
    public String getSolution() {
        return solution;
    }

    /**
     * @param solution the solution to set
     */
    public void setSolution(String solution) {
        this.solution = solution;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public List<TaskFeedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<TaskFeedback> feedback) {
        this.feedback = feedback;
    }

    @Override
    public TmcAccount getOwner() {
        return owner;
    }

    @Override
    public void setOwner(TmcAccount owner) {
        this.owner = owner;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
