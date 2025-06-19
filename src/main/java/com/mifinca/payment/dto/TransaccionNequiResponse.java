package com.mifinca.payment.dto;

public class TransaccionNequiResponse {

    private String idTransaccion;
    private String estado;
    private String referencia;

    public TransaccionNequiResponse(String idTransaccion, String estado, String referencia) {
        this.idTransaccion = idTransaccion;
        this.estado = estado;
        this.referencia = referencia;
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

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

}
