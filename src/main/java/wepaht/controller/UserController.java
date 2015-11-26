package wepaht.controller;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.security.access.annotation.Secured;
=======
>>>>>>> 80b137aa739ebfa3b8ebbffc83c3176e631c3f2c
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.domain.User;
import wepaht.repository.UserRepository;

@Controller
public class UserController {

    private String[] roles = {"STUDENT","TEACHER","ADMIN"};

    @Autowired
    UserRepository userRepository;

<<<<<<< HEAD
    @Secured("ROLE_ADMIN")
=======
>>>>>>> 80b137aa739ebfa3b8ebbffc83c3176e631c3f2c
    @RequestMapping(value="users", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", roles);
        return "users";
    }

<<<<<<< HEAD
    @Secured("ROLE_ADMIN")
=======
>>>>>>> 80b137aa739ebfa3b8ebbffc83c3176e631c3f2c
    @RequestMapping(value="users/{id}", method = RequestMethod.GET)
    public String getUser(Model model, @PathVariable Long id) {
        model.addAttribute("user", userRepository.findOne(id));
        model.addAttribute("roles", roles);
        return "user";
    }

<<<<<<< HEAD
    @Secured("ROLE_ADMIN")
=======
>>>>>>> 80b137aa739ebfa3b8ebbffc83c3176e631c3f2c
    @RequestMapping(value="users", method = RequestMethod.POST)
    public String create(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("messages", "User created succesfully.");

<<<<<<< HEAD
        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
=======
        return "redirect:/users";
    }

>>>>>>> 80b137aa739ebfa3b8ebbffc83c3176e631c3f2c
    @RequestMapping(value="users/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.delete(id);
        redirectAttributes.addFlashAttribute("messages", "User deleted.");

        return "redirect:/users";
    }

<<<<<<< HEAD
    @Secured("ROLE_ADMIN")
    @Transactional
    @RequestMapping(value ="users/{id}/edit", method = RequestMethod.POST)
    public String update(@PathVariable Long id, RedirectAttributes redirectAttributes,
                         @RequestParam(required = false) String username,
                         @RequestParam(required = false) String role,
                         @RequestParam(required = false) String password,
                         @RequestParam(required = false) String repassword){

        if(!password.equals(repassword)){
            redirectAttributes.addFlashAttribute("messages", "Passwords didn't match");
            return "redirect:/users/{id}";
        }
        
=======
    @Transactional
    @RequestMapping(value ="users/{id}/edit", method = RequestMethod.POST)
    public String update(@PathVariable Long id, RedirectAttributes redirectAttributes,
                         @RequestParam String username,
                         @RequestParam String role,
                         @RequestParam String password){

>>>>>>> 80b137aa739ebfa3b8ebbffc83c3176e631c3f2c
        User olduser = userRepository.getOne(id);
        olduser.setUsername(username);
        olduser.setRole(role);
        olduser.setPassword(password);

        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addFlashAttribute("messages", "User modified!");
        return "redirect:/users/{id}";
    }

<<<<<<< HEAD
=======
//    @RequestMapping(value = "login", method = RequestMethod.GET)
//    public String viewLogin() {
//        return "login";
//    }
//
//    @RequestMapping(value = "login", method = RequestMethod.POST)
//    public String authenticateLogin(@RequestParam String username, @RequestParam String password,
//                                    RedirectAttributes redirectAttributes) {
//
//        redirectAttributes.addFlashAttribute("messages", "Logged in successfully!");
//
//        return "redirect:/index";
//    }

>>>>>>> 80b137aa739ebfa3b8ebbffc83c3176e631c3f2c
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String viewRegister() {
        return "register";
    }
}
