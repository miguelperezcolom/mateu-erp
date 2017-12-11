package io.mateu.erp.model.monitoring;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class HttpWatchdog extends Watchdog {

    @NotNull
    private String url;

    @NotNull
    private HttpMethod method;

    private String requestBody;

    private int expectedResponseCode = 200;

    private String expectedText;

    @Override
    public void check(EntityManager em) throws Throwable {

    }
}
