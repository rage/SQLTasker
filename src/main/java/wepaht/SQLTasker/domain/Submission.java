package wepaht.SQLTasker.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Submission extends AbstractPersistable<Long> {
    
    @NotNull
    @ManyToOne
    private Account account;
    
    @ManyToOne
    private Task task;
    
    @ManyToOne
    private Category category;
    
    @ManyToOne
    private Course course;
    
    private String query;
    private Boolean points;
    
    public Submission() {
        
    }
    
    public Submission(Account account, Task task, Category category, Course course, String query, Boolean points) {
        this.account = account;
        this.task = task;
        this.category = category;
        this.course = course;
        this.query = query;
        this.points = points;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isPoints() {
        return points;
    }

    public void setPoints(boolean points) {
        this.points = points;
    }
}
