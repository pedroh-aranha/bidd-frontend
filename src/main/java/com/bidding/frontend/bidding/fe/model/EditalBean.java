/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bidding.frontend.bidding.fe.model;

import java.sql.Date;

/**
 *
 * @author Aluno
 */
public class EditalBean {
    private Long id;
    private String titulo;
    private String descricao;
    private Date datafechamento;
    private String status;
    private Boolean urgente;

    public EditalBean() {
    }

    public EditalBean(Long id, String titulo, String descricao, Date datafechamento, String status, Boolean urgente) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.datafechamento = datafechamento;
        this.status = status;
        this.urgente = urgente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDatafechamento() {
        return datafechamento;
    }

    public void setDatafechamento(Date datafechamento) {
        this.datafechamento = datafechamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getUrgente() {
        if (datafechamento == null || status == null || !status.equals("ABERTO")) {
            return false;
        }
        long diffMillis = datafechamento.getTime() - System.currentTimeMillis();
        long diffDays = diffMillis / (1000 * 60 * 60 * 24);
        return diffDays >= 0 && diffDays <= 3;
    }

    public void setUrgente(Boolean urgente) {
        this.urgente = urgente;
    }

}
