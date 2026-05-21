/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bidding.frontend.bidding.fe.service;

import com.bidding.frontend.bidding.fe.model.EditalBean;
import com.bidding.frontend.bidding.fe.model.LancesBean;
import com.bidding.frontend.bidding.fe.model.UserBean;
import com.bidding.frontend.bidding.fe.model.UserRequestBean;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Aluno
 */
@Service
public class APIService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:3333";

    // ── Utilitário: monta headers com Bearer token ─────────────────────────────
    private HttpHeaders headersComToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ── AUTH ──────────────────────────────────────────────────────────────────

    public void registrarUsuario(UserBean user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserBean> entity = new HttpEntity<>(user, headers);
        restTemplate.postForObject(BASE_URL + "/api/auth/registrar", entity, String.class);
    }

    public String logar(UserRequestBean credentials) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRequestBean> entity = new HttpEntity<>(credentials, headers);
        return restTemplate.postForObject(BASE_URL + "/api/auth/logar", entity, String.class);
    }

    // ── EDITAIS ───────────────────────────────────────────────────────────────

    public List<EditalBean> listarEditais(String token) {
        HttpEntity<Void> entity = new HttpEntity<>(headersComToken(token));
        ResponseEntity<EditalBean[]> response = restTemplate.exchange(
                BASE_URL + "/api/editais",
                HttpMethod.GET,
                entity,
                EditalBean[].class);
        return Arrays.asList(response.getBody());
    }

    public void criarEdital(EditalBean edital, String token) {
        HttpEntity<EditalBean> entity = new HttpEntity<>(edital, headersComToken(token));
        restTemplate.postForObject(BASE_URL + "/api/editais/criar", entity, String.class);
    }

    // ── LANCES ────────────────────────────────────────────────────────────────

    public void registrarLance(Long editalId, LancesBean lance, String token) {
        HttpEntity<LancesBean> entity = new HttpEntity<>(lance, headersComToken(token));
        // Atenção: o backend tem typo "laces" — manter igual ao backend
        restTemplate.postForObject(BASE_URL + "/api/editais/" + editalId + "/laces", entity, String.class);
    }
}
