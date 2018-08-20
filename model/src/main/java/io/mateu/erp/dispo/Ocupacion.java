package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Ocupacion {

    private int numHabs;
    private int adultos;
    private int ninos;
    private int bebes;

    private int pax;

    public Ocupacion(int numHabs, int adultos, int ninos, int bebes) {
        this.numHabs = numHabs;
        this.adultos = adultos;
        this.ninos = ninos;
        this.bebes = bebes;
        this.pax = adultos + ninos + bebes;
    }
}
