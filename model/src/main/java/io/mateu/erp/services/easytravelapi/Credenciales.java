package io.mateu.erp.services.easytravelapi;

import io.mateu.erp.model.util.Helper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Credenciales {

    private String agentId;
    private String hotelId;
    private String login;
    private String pass;
    private String lan;

    public Credenciales(String authToken) throws IOException {
        Map<String, Object> d = Helper.fromJson(authToken, HashMap.class);
        setAgentId((String) d.get("actorId"));
        setLogin((String) d.get("login"));
        setPass((String) d.get("pass"));
        setLan((String) d.get("lan"));
        setHotelId((String) d.get("hotelId"));
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
}
