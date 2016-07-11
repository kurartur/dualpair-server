package com.artur.dualpair.server.interfaces.web.controller;

import com.artur.dualpair.server.service.socionics.test.SocionicsTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @Autowired
    private SocionicsTestService socionicsTestService;

    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "Hello";
    }

}
