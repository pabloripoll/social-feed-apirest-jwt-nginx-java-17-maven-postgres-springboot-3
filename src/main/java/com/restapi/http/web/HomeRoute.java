package com.restapi.http.web;

import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.restapi.domain.user.repository.UserRepository;

@Controller
public class HomeRoute {

    private final UserRepository userRepository;

    @Autowired
    public HomeRoute(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        Map<String, String> db = new HashMap<>();
        try {
            long userCount = userRepository.count();
            db.put("status", "ok");
            db.put("message", "User count: " + userCount);
        } catch (DataAccessException ex) {
            db.put("status", "error");
            db.put("message", "Database connection failed: " + ex.getMessage());
        }
        model.addAttribute("db", db);

        return "home";
    }
}
