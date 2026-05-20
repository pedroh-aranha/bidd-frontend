/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bidding.frontend.bidding.fe.service;

import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Aluno
 */
@Service
public class APIService {
    
    private RestTemplate restTemplate;
    private final String BASE_URL = "http://localhost:9000";
    
    public List<EditalBean> listarEditais(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);
        
        return restTemplate.exchange(BASE_URL + "/api/editais",
                HttpMethod.GET, entity, ...).getBody();

    }
    
}
