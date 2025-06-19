
package com.mifinca.payment.dto;


public class CrearTransaccionNequiDTO {
    private String acceptanceToken;
    private String acceptPersonalAuth;
    private Integer amountInCents;
    private String currency;
    private String reference;
    private String customerEmail;
    private String phoneNumber;

    // Getters y setters
    public String getAcceptanceToken() { return acceptanceToken; }
    public void setAcceptanceToken(String acceptanceToken) { this.acceptanceToken = acceptanceToken; }

    public String getAcceptPersonalAuth() { return acceptPersonalAuth; }
    public void setAcceptPersonalAuth(String acceptPersonalAuth) { this.acceptPersonalAuth = acceptPersonalAuth; }

    public Integer getAmountInCents() { return amountInCents; }
    public void setAmountInCents(Integer amountInCents) { this.amountInCents = amountInCents; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
