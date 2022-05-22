package com.example.springproject.web;

import com.example.springproject.domain.PurchaseRecord;
import com.example.springproject.domain.User;
import com.example.springproject.domain.UserForm;
import com.example.springproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
@Controller
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    @RequestMapping(params = "submit", method = RequestMethod.POST)
    @PostMapping("/register")
    public String registerUser(@Valid UserForm userForm, BindingResult
            result, RedirectAttributes attributes) {
        attributes.addFlashAttribute("username", userForm.getUsername());
        if (result.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            attributes.addFlashAttribute("errorMsg",
                    errors.get(0).getDefaultMessage());
            return "redirect:/register";
        } else {
            return "redirect:/login";
        }
    }
//    @GetMapping("/findUser{id}")
//    public String findUserInfo(@PathVariable String username, Model model){
//        User user = userService.findByUsername(username);
//        model.addAttribute("purchaseRecord",purchaseRecord);
//        return "addPurchaseRecord";
//    }
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }
    @PostMapping("/homepage")
    public String homepage(User user){
        return "homepage";
    }
    @PostMapping("/login")
    public String login(@Valid User user, BindingResult
            result, RedirectAttributes attributes){
        attributes.addFlashAttribute("username", user.getUsername());
        if(result.hasErrors()){
            List<FieldError> errors = result.getFieldErrors();
            attributes.addFlashAttribute("errorMsg",
                    errors.get(0).getDefaultMessage());
            return "redirect:/login";
        }
        else{
            attributes.addAttribute("username",user.getUsername());
            return "redirect:/homepage";
        }
    }
}
