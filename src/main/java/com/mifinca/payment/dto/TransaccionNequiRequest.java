package com.mifinca.payment.dto;

public class TransaccionNequiRequest {

    private String celular;
    private String correoCliente;
    private String acceptanceToken;
    private String personalToken;
    private int monto_en_centavos;

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getAcceptanceToken() {
        return acceptanceToken;
    }

    public void setAcceptanceToken(String acceptanceToken) {
        this.acceptanceToken = acceptanceToken;
    }

    public String getPersonalToken() {
        return personalToken;
    }

    public void setPersonalToken(String personalToken) {
        this.personalToken = personalToken;
    }

    public int getMonto_en_centavos() {
        return monto_en_centavos;
    }

    public void setMonto_en_centavos(int monto_en_centavos) {
        this.monto_en_centavos = monto_en_centavos;
    }
    
    
}
