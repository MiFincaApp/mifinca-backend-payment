package com.mifinca.payment.dto;

public class TransaccionNequiResponse {

    private String idTransaccion;
    private String estado;

    public TransaccionNequiResponse(String idTransaccion, String estado) {
        this.idTransaccion = idTransaccion;
        this.estado = estado;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
