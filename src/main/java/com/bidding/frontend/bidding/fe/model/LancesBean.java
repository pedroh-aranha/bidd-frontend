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
public class LancesBean {
    private Long id;
    private double valor;
    private Date data_lance;
    private Long idEdital;
    private Long idusuario;

    public LancesBean() {
    }

    public LancesBean(Long id, double valor, Date data_lance, Long idEdital, Long idusuario) {
        this.id = id;
        this.valor = valor;
        this.data_lance = data_lance;
        this.idEdital = idEdital;
        this.idusuario = idusuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getData_lance() {
        return data_lance;
    }

    public void setData_lance(Date data_lance) {
        this.data_lance = data_lance;
    }

    public Long getIdEdital() {
        return idEdital;
    }

    public void setIdEdital(Long idEdital) {
        this.idEdital = idEdital;
    }

    public Long getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(Long idusuario) {
        this.idusuario = idusuario;
    }
  
    
}
