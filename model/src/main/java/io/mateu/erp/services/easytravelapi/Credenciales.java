package io.mateu.erp.services.easytravelapi;


import io.mateu.mdd.core.util.Helper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Credenciales {

    private String agentId;
    private String hotelId;
    private String posId;
    private String login;
    private String pass;
    private String lan;

    public Credenciales(String authToken) throws IOException {
        Map<String, Object> d = Helper.fromJson(authToken, HashMap.class);
        if (d.containsKey("agencyId")) setAgentId((String) d.get("agencyId"));
        else if (d.containsKey("actorId")) setAgentId((String) d.get("actorId"));
        setLogin((String) d.get("userId"));
        setPass((String) d.get("pass"));
        setLan((String) d.get("lan"));
        setHotelId((String) d.get("hotelId"));
        setPosId((String) d.get("posId"));
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

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }
}
