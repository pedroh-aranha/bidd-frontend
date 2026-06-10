/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bidding.frontend.bidding.fe.controller;

import com.bidding.frontend.bidding.fe.model.EditalBean;
import com.bidding.frontend.bidding.fe.model.LancesBean;
import com.bidding.frontend.bidding.fe.service.AuthRestClientService;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
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
        if (token == null) {
            return "redirect:/login";
        }

        try {
            List<EditalBean> editais;

            if (Boolean.TRUE.equals(urgente)) {
                editais = restService.listarUrgentes(token); // ← chama endpoint /urgentes
                model.addAttribute("urgente", true);
            } else {
                editais = restService.listarEditais(token); // ← chama endpoint normal
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
        if (token == null) {
            return "redirect:/login";
        }

        String role = (String) session.getAttribute("role");
        if (!"COMPRADOR".equals(role)) {
            return "redirect:/editais";
        }

        model.addAttribute("edital", new EditalBean());
        model.addAttribute("nome", session.getAttribute("nome"));
        model.addAttribute("role", session.getAttribute("role"));
        return "novo-edital";
    }

    @PostMapping("/editais/novo")
    public String criarEdital(@ModelAttribute EditalBean edital,
            HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

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
    public String detalhesEdital(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            EditalBean edital = restService.getEditalById(id, token);

            if (edital == null) {
                return "redirect:/editais";
            }

            model.addAttribute("edital", edital);
            model.addAttribute("lance", new LancesBean());
            model.addAttribute("role", session.getAttribute("role"));
            model.addAttribute("nome", session.getAttribute("nome"));

            // Buscar lances do edital se for COMPRADOR, ou lance próprio se for FORNECEDOR
            String role = (String) session.getAttribute("role");
            if ("COMPRADOR".equals(role)) {
                List<LancesBean> todosLances = restService.listarLancesDoEdital(id, token);
                model.addAttribute("todosLances", todosLances);
            } else if ("FORNECEDOR".equals(role)) {
                List<LancesBean> meusLances = restService.listarMeusLances(token);
                LancesBean meuLance = meusLances.stream()
                        .filter(l -> l.getIdEdital().equals(id))
                        .findFirst().orElse(null);
                model.addAttribute("meuLance", meuLance);
            }

            return "edital-detalhes";
        } catch (Exception e) {
            return "redirect:/editais";
        }
    }

    @PostMapping("/editais/{id}/lance")
    public String registrarLance(@PathVariable Long id,
            @ModelAttribute LancesBean lance,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            restService.registrarLance(id, lance.getValor(), token);
            return "redirect:/editais/" + id + "?sucesso";
        } catch (Exception e) {
            return "redirect:/editais/" + id + "?erro=" + e.getMessage();
        }
    }

    @GetMapping("/meus-lances")
    public String meusLances(HttpSession session, Model model) {

        // Verificar autenticação
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        model.addAttribute("nome", session.getAttribute("nome"));
        model.addAttribute("role", session.getAttribute("role"));

        try {
            List<LancesBean> lances = restService.listarMeusLances(token);
            model.addAttribute("lances", lances);
        } catch (Exception e) {
            // Em caso de erro na API retornar lista vazia em vez de crashar
            model.addAttribute("lances", Collections.emptyList());
            model.addAttribute("erro", "Não foi possível carregar seus lances: " + e.getMessage());
        }

        return "meus-lances";
    }

}
