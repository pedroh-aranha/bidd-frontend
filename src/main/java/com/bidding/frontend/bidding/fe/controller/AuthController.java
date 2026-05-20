/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bidding.frontend.bidding.fe.controller;

import com.bidding.frontend.bidding.fe.service.APIService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Aluno
 */
@Controller
public class AuthController {
    
    @Autowired
    private APIService apiService;
    
    @PostMapping("/login")

    public String logar(UserRequestBean userauth, HttpSession session) {
        try {
        String token = apiService.logar(userauth);
        session.setAttribute("token", token);
        return "redirect:/editais";
        } catch (Exception e) {
        return "login?error=true";
        }
    }
    
}
