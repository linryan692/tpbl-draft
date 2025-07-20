// src/main/java/com/tpbl/controller/HomeController.java
package com.tpbl.controller;

import com.tpbl.model.Team;
import com.tpbl.service.DraftService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final DraftService draftService;

    public HomeController(DraftService draftService) {
        this.draftService = draftService;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        // 拿到 Spring Security 的 username
        String username = authentication.getName();
        // 通过 service 查出对应的 Team
        Team team = draftService.findTeamByUsername(username);

        model.addAttribute("team", team);
        model.addAttribute("order", draftService.getTeamOrder());
        model.addAttribute("picks", draftService.allPicks());
        // 你前端 JS 里会用到 teams 列表，传进来就好了
        model.addAttribute("teams", draftService.getTeamOrder());
        return "index";
    }
}