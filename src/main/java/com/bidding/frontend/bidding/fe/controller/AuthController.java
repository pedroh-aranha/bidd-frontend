/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bidding.frontend.bidding.fe.controller;

import com.bidding.frontend.bidding.fe.model.UserBean;
import com.bidding.frontend.bidding.fe.model.UserRequestBean;
import com.bidding.frontend.bidding.fe.service.AuthRestClientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    // Injeção do serviço de autenticação para delegar a lógica de login.    
    @Autowired
    private AuthRestClientService restService;

    // Tratador para requisições GET no caminho raiz "/".
    // Retorna o nome da view Thymeleaf "index".
    @GetMapping("/")
    public String home(HttpSession session) {
        return "redirect:/editais";
    }

    // Tratador para requisições GET em "/login".
    // Prepara o modelo com um objeto UserRequestDTO vazio para preencher o formulário.
    @GetMapping("/login")
    public String login(Model model) {
        UserRequestBean credenciais = new UserRequestBean();
        model.addAttribute("credenciais", credenciais);
        return "login";
    }

    // Tratador para requisições POST em "/logar".
    // Recebe as credenciais submetidas pelo formulário e tenta autenticar.
    @PostMapping("/logar")
    public String logar(@ModelAttribute UserRequestBean credenciais, HttpSession session, Model model) {
        try {
            String token = restService.logar(credenciais);
            if (token == null) {
                model.addAttribute("erro", "E-mail ou senha inválidos");
                model.addAttribute("credenciais", credenciais);
                return "login";
            }
            String[] partes = token.split("\\.");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(partes[1]));
            String nome = payload.replaceAll(".*\\\"nome\\\":\\\"([^\\\"]+)\\\".*", "$1");
            String role = payload.replaceAll(".*\\\"role\\\":\\\"([^\\\"]+)\\\".*", "$1");
            session.setAttribute("token", token);
            session.setAttribute("nome", nome);
            session.setAttribute("role", role);
            return "redirect:/editais";

        } catch (Exception e) {
            // ✅ Se o backend retornar 401/400, cai aqui e mostra mensagem na tela
            model.addAttribute("erro", "E-mail ou senha inválidos");
            model.addAttribute("credenciais", credenciais);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); //  acaba ca sessão inteira
        return "redirect:/login";
    }

    @GetMapping("/registrar")
    public String registrar(Model model) {
        UserBean newUser = new UserBean();
        model.addAttribute("user", newUser);
        return "registrar";
    }

    @PostMapping("/registrar")
    public String mandarRegistro(@ModelAttribute UserBean user, Model model) {
        String erro = restService.registrar(user);

        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("user", user);
            return "registrar";
        }

        return "redirect:/login";
    }

}
