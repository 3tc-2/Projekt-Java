package com.market.backend_market.controllers;

import com.market.backend_market.magazynKlientow.User_Repo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class User_Controller {
    User_Repo user_repo;
    public User_Controller(User_Repo user_repo) {
        this.user_repo = user_repo;
    }
    @GetMapping("/exist/{login}")
    public String exist(
            @PathVariable String login
    ) {
        if (user_repo.existsByLogin(login)){
            return "exist";
        }else {
            return "not exist";
        }
    }
}
