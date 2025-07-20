// src/main/java/com/tpbl/controller/HomeController.java
package com.tpbl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 「首頁」路由
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // 如果有 dashboard 或其他頁面，再加新的 mapping
    // @GetMapping("/dashboard")
    // public String dashboard() { return "dashboard"; }

}
