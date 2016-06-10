/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.CategoryDetailsWrapper;
import wepaht.SQLTasker.service.CourseService;

@Controller
@RequestMapping("courses")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String getCourses(Model model) {
        return courseService.courseListing(model);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getCourse(Model model, @PathVariable Long id) {
        return courseService.getCourse(model, id);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String createCourse(RedirectAttributes redirectAttributes,
            @RequestParam String name,
            @RequestParam(required = false) String starts,
            @RequestParam(required = false) String expires,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Long> categoryIds) {
        
        return courseService.createCourse(redirectAttributes, name, starts, expires, description, categoryIds);
    }
    
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String getCreateCourse(Model model) {
        return courseService.courseCreateForm(model);
    }
    
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public String deleteCourse(RedirectAttributes redirectAttributes, @PathVariable Long id) {
        return courseService.deleteCourse(redirectAttributes, id);
    }
    
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String getEditCourse(Model model, @PathVariable Long id) {
        return courseService.editForm(model, id);
    }
    
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String editCourse(RedirectAttributes redirectAttributes, 
            @PathVariable Long id, 
            @RequestParam String name,
            @RequestParam(required = false) String starts,
            @RequestParam(required = false) String expires,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Long> categoryIds) {
        
        return courseService.editCourse(redirectAttributes, id, name, starts, expires, description, categoryIds);
    }
    
    @RequestMapping(value = "/{id}/join", method = RequestMethod.POST)
    public String joinCourse(RedirectAttributes redirectAttributes, 
            @PathVariable Long id) {
        return courseService.joinCourse(redirectAttributes, id);
    }
    
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.POST)
    public String leaveCourse(RedirectAttributes redirectAttributes, @PathVariable Long id) {
        return courseService.leaveCourse(redirectAttributes, id);
    }
    
    @RequestMapping(value = "/{id}/details", method = RequestMethod.GET)
    public String getCategoryDetails(Model model, @PathVariable Long id, @ModelAttribute("wrapper") CategoryDetailsWrapper wrapper) {
        return courseService.getCategoryDetails(model, id);
    }
    
    @RequestMapping(value = "/{id}/details", method = RequestMethod.POST)
    public String postCategoryDetails(RedirectAttributes redirectAttributes, @PathVariable Long id, @ModelAttribute CategoryDetailsWrapper wrapper) {
        return courseService.setCategoryDetails(redirectAttributes, wrapper.getCategoryDetailsList(), id);
    }
    
    @RequestMapping(value = "/{courseId}/category/{categoryId}", method = RequestMethod.GET)
    public String getCourseCategory(Model model, RedirectAttributes redirectAttributes, @PathVariable Long courseId, @PathVariable Long categoryId) {
        return courseService.getCourseCategory(model, redirectAttributes, courseId, categoryId);
    }
    
    @RequestMapping(value = "/{courseId}/category/{categoryId}/task/{taskId}", method = RequestMethod.GET)
    public String getCourseCategoryTask(
            Model model, 
            RedirectAttributes redirectAttr, 
            @PathVariable Long courseId, 
            @PathVariable Long categoryId,
            @PathVariable Long taskId) {
        return courseService.getCourseCategoryTask(model, redirectAttr, courseId, categoryId, taskId);
    }
    
    @RequestMapping(value = "/{courseId}/category/{categoryId}/task/{taskId}/query", method = RequestMethod.POST)
    public String postQuery(RedirectAttributes redirectAttr, 
            @RequestParam String query, 
            @PathVariable Long courseId, 
            @PathVariable Long categoryId, 
            @PathVariable Long taskId) {
        
        return courseService.createQuery(redirectAttr, query, courseId, categoryId, taskId);
    }
}
