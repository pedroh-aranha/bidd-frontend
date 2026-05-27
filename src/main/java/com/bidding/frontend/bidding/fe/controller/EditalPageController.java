/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 *//*
package com.bidding.frontend.bidding.fe.controller;

import com.bidding.frontend.bidding.fe.model.EditalBean;
import com.bidding.frontend.bidding.fe.model.LancesBean;
import com.bidding.frontend.bidding.fe.service.APIService;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
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
    private APIService apiService;

    // Redireciona raiz para /editais
    @GetMapping("/")
    public String raiz() { return "redirect:/editais"; }

    // ── LISTAGEM ──────────────────────────────────────────────────────────────

    @GetMapping("/editais")
    public String listarEditais(@RequestParam(required = false) Boolean urgente,
        HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            List<EditalBean> editais = apiService.listarEditais(token);

            if (Boolean.TRUE.equals(urgente)) {
                editais = editais.stream()
                         .filter(edital -> "URGENTE".equalsIgnoreCase(edital.getStatus()))
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

    // ── DETALHES + LANCE ──────────────────────────────────────────────────────

    @GetMapping("/editais/{id}")
    public String detalhesEdital(@PathVariable Long id,
                                  HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            List<EditalBean> todos = apiService.listarEditais(token);
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

    @PostMapping("/editais/{id}/lance")
    public String enviarLance(@PathVariable Long id,
                               @ModelAttribute LancesBean lance,
                               HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            lance.setIdEdital(id);
            lance.setData_lance(new Date(System.currentTimeMillis()));
            lance.setIdusuario((Long) session.getAttribute("userId"));
            apiService.registrarLance(id, lance, token);
            return "redirect:/editais/" + id + "?sucesso=true";
        } catch (Exception e) {
            return "redirect:/editais/" + id + "?erro=" + e.getMessage();
        }
    }

    // ── NOVO EDITAL (apenas COMPRADOR) ────────────────────────────────────────

    @GetMapping("/editais/novo")
    public String novoEditalForm(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        String role = (String) session.getAttribute("role");
        if (!"COMPRADOR".equals(role)) return "redirect:/editais?erro=acesso_negado";

        model.addAttribute("edital", new EditalBean());
        model.addAttribute("nome", session.getAttribute("nome"));
        model.addAttribute("role", role);
        return "novo-edital";
    }

    @PostMapping("/editais/novo")
    public String criarEdital(@ModelAttribute EditalBean edital,
                               HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            apiService.criarEdital(edital, token);
            return "redirect:/editais?criado=true";
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

}*/