/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bidding.frontend.bidding.fe.controller;

import com.bidding.frontend.bidding.fe.model.UserBean;
import com.bidding.frontend.bidding.fe.model.UserRequestBean;
import com.bidding.frontend.bidding.fe.service.APIService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpSession;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private APIService apiService;

    // Mesma secret do backend (application.properties do back)
    private static final String SECRET = "dXVpZC1zZWNyZXQta2V5LXBhcmEtaG1hYy1zaGEyNTY=";

    // ── LOGIN ─────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String exibirLogin(Model model) {
        model.addAttribute("credentials", new UserRequestBean());
        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(@ModelAttribute UserRequestBean credentials,
                                  HttpSession session, Model model) {
        try {
            String token = apiService.logar(credentials);

            // Extrai role e nome do token JWT para guardar na sessão
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();

            session.setAttribute("token", token);
            session.setAttribute("role", claims.get("role", String.class));
            session.setAttribute("nome", claims.get("nome", String.class));
            session.setAttribute("userId", claims.get("id", Long.class));

            return "redirect:/editais";
        } catch (Exception e) {
            model.addAttribute("erro", "E-mail ou senha inválidos.");
            model.addAttribute("credentials", credentials);
            return "login";
        }
    }

    // ── REGISTER ──────────────────────────────────────────────────────────────

    @GetMapping("/register")
    public String exibirCadastro(Model model) {
        model.addAttribute("user", new UserBean());
        return "register";
    }

    @PostMapping("/register")
    public String processarCadastro(@ModelAttribute UserBean user, Model model) {
        try {
            apiService.registrarUsuario(user);
            return "redirect:/login?cadastro=ok";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao cadastrar: " + e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }

    // ── LOGOUT ────────────────────────────────────────────────────────────────

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}