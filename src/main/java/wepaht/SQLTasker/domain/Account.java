package wepaht.SQLTasker.domain;


import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@Entity
public class Account extends AbstractPersistable<Long> {
    
    @ManyToMany(mappedBy = "students")
    private List<Course> courses;

    @Column(unique = true)
    @NotEmpty
    @Length(min = 4, max = 30)
    private String username;

    @NotNull
    @NotEmpty
    private String password;
    
    @OneToMany(mappedBy = "account", orphanRemoval = true)
    private List<Submission> submissions;

    private String role;
    private String salt;

    /**
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!password.isEmpty() || password.length() >= 3) {
            this.salt = BCrypt.gensalt();
            this.password = BCrypt.hashpw(password, this.salt);
        }
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }
}
