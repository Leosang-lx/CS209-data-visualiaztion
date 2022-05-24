package com.example.springproject.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PageTransport {
    @Deprecated
    @GetMapping("/repository")
    public String repository(){
        return "Repository";
    }
    @Deprecated
    @GetMapping("/event")
    public String event(){
        return "Event";
    }
    @GetMapping("/index")
    public String index(){
        return "New";
    }
    @GetMapping()
    public String homepage(){
        return "redirect:/index";
    }
}
