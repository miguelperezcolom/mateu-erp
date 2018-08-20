package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter@Setter
public class LineaReserva {

    private LocalDate entrada;

    private LocalDate salida;

    private int release;

    private int numeroNoches;

    private int adultos;

    private int juniors;

    private int ninos;

    private int bebes;

    private int pax;

    private int[] edades;

    private String firmaOcupacion;


    public LineaReserva(LocalDate entrada, LocalDate salida, int release, int numeroNoches, int adultos, int juniors, int ninos, int bebes, int[] edades) {
        this.entrada = entrada;
        this.salida = salida;
        this.release = release;
        this.numeroNoches = numeroNoches;
        this.adultos = adultos;
        this.juniors = juniors;
        this.ninos = ninos;
        this.bebes = bebes;
        this.edades = edades;
        this.pax = adultos + juniors + ninos + bebes;
        this.firmaOcupacion = "" + adultos + "-" + ninos + "-" + bebes;
    }


}
