/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bidding.frontend.bidding.fe.controller;

import com.bidding.frontend.bidding.fe.model.EditalBean;
import com.bidding.frontend.bidding.fe.model.LancesBean;
import com.bidding.frontend.bidding.fe.service.AuthRestClientService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EditalPageController {

    @Autowired
    private AuthRestClientService restService;


    @GetMapping("/editais")
    public String listarEditais(@RequestParam(required = false) Boolean urgente,
                                HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            List<EditalBean> editais = restService.listarEditais(token);

            if (Boolean.TRUE.equals(urgente)) {
                editais = editais.stream()
                        .filter(e -> Boolean.TRUE.equals(e.getUrgente()))
                        .collect(Collectors.toList());
                model.addAttribute("urgente", true);
            }

            model.addAttribute("editais", editais);
            model.addAttribute("nome", session.getAttribute("nome"));
            model.addAttribute("role", session.getAttribute("role"));
            return "editais";
            
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/editais/novo")
    public String novoEditalForm(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        String role = (String) session.getAttribute("role");
        if (!"COMPRADOR".equals(role)) return "redirect:/editais";
        
        model.addAttribute("edital", new EditalBean());
        model.addAttribute("nome", session.getAttribute("nome"));
        model.addAttribute("role", session.getAttribute("role"));
        return "novo-edital";
    }

    @PostMapping("/editais/novo")
    public String criarEdital(@ModelAttribute EditalBean edital,
                              HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            restService.criarEdital(edital, token);
            return "redirect:/editais";
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("403")) {
                model.addAttribute("erro", "Acesso negado: apenas COMPRADOR pode criar editais.");
            } else {
                model.addAttribute("erro", "Erro ao criar edital: " + msg);
            }
            model.addAttribute("edital", edital);
            model.addAttribute("nome", session.getAttribute("nome"));
            model.addAttribute("role", session.getAttribute("role"));


            return "novo-edital";
        }
    }
    
    @GetMapping("/editais/{id}")
    public String detalhesEdital(@PathVariable Long id,
                                  HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            List<EditalBean> todos = restService.listarEditais(token);
            EditalBean edital = todos.stream()
                    .filter(e -> e.getId().equals(id))
                    .findFirst().orElse(null);

            if (edital == null) return "redirect:/editais";

            model.addAttribute("edital", edital);
            model.addAttribute("lance", new LancesBean());
            model.addAttribute("role", session.getAttribute("role"));
            model.addAttribute("nome", session.getAttribute("nome"));
            return "edital-detalhes";
        } catch (Exception e) {
            return "redirect:/editais";
        }
    }

}