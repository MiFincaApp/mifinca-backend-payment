package com.mifinca.payment.dto;

public class TokensAceptacionDTO {

    private String acceptanceToken;
    private String acceptancePermalink;
    private String personalToken;
    private String personalPermalink;

    // Getters y setters
    public String getAcceptanceToken() {
        return acceptanceToken;
    }

    public void setAcceptanceToken(String acceptanceToken) {
        this.acceptanceToken = acceptanceToken;
    }

    public String getAcceptancePermalink() {
        return acceptancePermalink;
    }

    public void setAcceptancePermalink(String acceptancePermalink) {
        this.acceptancePermalink = acceptancePermalink;
    }

    public String getPersonalToken() {
        return personalToken;
    }

    public void setPersonalToken(String personalToken) {
        this.personalToken = personalToken;
    }

    public String getPersonalPermalink() {
        return personalPermalink;
    }

    public void setPersonalPermalink(String personalPermalink) {
        this.personalPermalink = personalPermalink;
    }

}
