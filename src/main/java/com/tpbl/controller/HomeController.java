package com.tpbl.controller;

import com.tpbl.config.TeamUserDetails;
import com.tpbl.model.Team;
import com.tpbl.service.DraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DraftService draftService;

    @GetMapping("/")
    public String home(Model model,
                       @AuthenticationPrincipal TeamUserDetails userDetails) {
        Team me = userDetails.getTeam();
        model.addAttribute("teamId", me.getId());
        model.addAttribute("teamName", me.getName());
        model.addAttribute("teams", draftService.getAllTeams());
        return "index";
    }

    // ↓ 删除或注释掉这个方法 ↓
    // @GetMapping("/login")
    // public String login() {
    //     return "login";
    // }
}