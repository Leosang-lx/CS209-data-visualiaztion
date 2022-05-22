package com.example.springproject.web;

import com.example.springproject.domain.IdComments;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@RestController
public class HelloTest {
    @RequestMapping("/hello")
    public String hello(){
        return "bye";
    }
    @RequestMapping("/ic")
    @CrossOrigin
    public List<IdComments> userinfo(){
        return Arrays.asList(new IdComments(1,2),new IdComments(3,4));
    }
}