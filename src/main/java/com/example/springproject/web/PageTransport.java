package com.example.springproject.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageTransport {
//    @Autowired
//    private

    @GetMapping("/repository")
    public String repository(){
        return "Repository";
    }
    @GetMapping("/event")
    public String event(){
        return "Event";
    }
    @GetMapping()
    public String homepage(){
        return "redirect:/repository";
    }
//    @GetMapping("/echarts.js")
//    public String get_echarts(){
//        return "echarts"
//    }
}
