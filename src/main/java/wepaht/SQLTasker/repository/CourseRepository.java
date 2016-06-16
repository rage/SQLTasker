package wepaht.SQLTasker.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;

public interface CourseRepository extends JpaRepository<Course, Long>{
    
    @Query("SELECT c.courseCategories FROM Course c WHERE c = :course")
    List<Category> getCourseCategories(@Param("course") Course course);
    
    List<Course> findByName(String name);
    
    @Query("SELECT c.students FROM Course c WHERE c = :course")
    List<Account> getCourseStudents(@Param("course") Course course);
    
    @Query("SELECT c FROM Course c WHERE (starts <= :now OR starts IS NULL) AND (expires >= :now OR expires IS NULL)")
    List<Course> findByStartsBeforeAndExpiresAfter(@Param("now") LocalDate now);
}
