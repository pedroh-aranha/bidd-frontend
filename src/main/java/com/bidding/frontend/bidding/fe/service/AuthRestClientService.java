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
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Aluno /** Serviço de autenticação usando RestClient em vez de
 * RestTemplate.
 *
 * Este serviço ilustra uma alternativa com uma API fluente mais simples. Ele
 * encapsula a chamada ao endpoint de autenticação em um único método.
 *
 * A ideia principal é construir um cliente HTTP uma vez e reutilizá-lo,
 * evitando a necessidade de montar objetos HttpEntity manualmente.
 */
@Service
public class AuthRestClientService {

    // RestClient é o cliente HTTP moderno do Spring que permite
    // construir requisições de forma fluente e declarativa.
    private final RestClient restClient;

    /**
     * Construtor padrão do serviço.
     *
     * Aqui criamos o RestClient apenas uma vez e configuramos a URL base comum
     * para todas as requisições deste serviço.
     */
    public AuthRestClientService() {
        this.restClient = RestClient.builder()
                // Define a base URL que será usada em todas as requisições.
                // Depois, cada chamada só precisa informar o caminho relativo.
                .baseUrl("http://localhost:3333/api")
                .build();
    }

    /**
     * Envia as credenciais do usuário para o endpoint de login.
     *
     * @param user objeto DTO contendo email e senha
     * @return token ou resposta de autenticação como String
     */
    public String logar(UserRequestBean user) {
        // Inicia a construção de uma requisição POST.
        return restClient.post()
                // Define o caminho relativo ao endpoint de autenticação.
                // A URL final será "http://localhost:3333/api/auth/logar".
                .uri("/auth/logar")
                // Define o corpo da requisição como o DTO de login.
                // O Spring converte automaticamente este objeto para JSON.
                .body(user)
                // Dispara a requisição e obtém a resposta do servidor.
                .retrieve()
                // Lê o corpo da resposta e converte para String.
                // Use outro DTO aqui se a API retornar um objeto JSON complexo.
                .body(String.class);
    }

    public String registrar(UserBean user) {
        if (!user.getSenha().equals(user.getConfirmarSenha())) {
            return "As senhas não coincidem.";
        }
        user.setRole("FORNECEDOR");
        user.setConfirmarSenha(null);

        restClient
                .post()
                .uri("/auth/registrar")
                .body(user)
                .retrieve()
                .body(String.class);

        return null;
    }

    /**
     * Lista os editais do backend usando o token JWT no cabeçalho
     * Authorization.
     *
     * @param token token de autenticação recebido após o login
     * @return lista de editais retornada pela API
     */
    public List<EditalBean> listarEditais(String token) {
        // Faz uma requisição GET para o endpoint de editais.
        EditalBean[] editais = restClient.get()
                .uri("/editais")
                // Adiciona o header Authorization com o token Bearer.
                .header("Authorization", "Bearer " + token)
                .retrieve()
                // Converte o corpo JSON para um array de EditalDTO.
                .body(EditalBean[].class);

        // Converte o array para List para uso mais conveniente na aplicação.
        return Arrays.asList(editais);
    }

    public List<EditalBean> listarUrgentes(String token) {
        EditalBean[] editais = restClient.get()
                .uri("/editais/urgentes")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(EditalBean[].class);
        return Arrays.asList(editais);
    }

    public void criarEdital(EditalBean edital, String token) {
        restClient.post()
                .uri("/editais/criar")
                .header("Authorization", "Bearer " + token)
                .body(edital)
                .retrieve()
                .body(String.class);

    }

    public void registrarLance(Long idEdital, double valor, String token) {
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("valor", valor);
        body.put("data_lance", java.sql.Date.valueOf(java.time.LocalDate.now()));

        restClient.post()
                .uri("/editais/" + idEdital + "/lances")
                .header("Authorization", "Bearer " + token)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    public List<com.bidding.frontend.bidding.fe.model.LancesBean> listarLancesDoEdital(Long idEdital, String token) {
        com.bidding.frontend.bidding.fe.model.LancesBean[] lances = restClient.get()
                .uri("/editais/" + idEdital + "/lances")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(com.bidding.frontend.bidding.fe.model.LancesBean[].class);
        return Arrays.asList(lances);
    }

    public List<LancesBean> listarMeusLances(String token) {
        LancesBean[] lances = restClient.get()
                .uri("/editais/lances/meus")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(LancesBean[].class);
        return Arrays.asList(lances);
    }

    public EditalBean getEditalById(Long id, String token) {
        return restClient.get()
                .uri("/editais/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(EditalBean.class);
    }

}
