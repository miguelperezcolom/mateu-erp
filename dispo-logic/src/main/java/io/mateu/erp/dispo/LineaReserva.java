package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IRoom;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class LineaReserva {

    private int adultos;

    private int juniors;

    private int ninos;

    private int bebes;

    private int pax;

    private int[] edades;

    private String firmaOcupacion;


    public LineaReserva(int adultos, int juniors, int ninos, int bebes, int[] edades) {
        this.adultos = adultos;
        this.juniors = juniors;
        this.ninos = ninos;
        this.bebes = bebes;
        this.edades = edades;
        this.pax = adultos + juniors + ninos + bebes;
        this.firmaOcupacion = "" + adultos + "-" + ninos + "-" + bebes;
    }


}
