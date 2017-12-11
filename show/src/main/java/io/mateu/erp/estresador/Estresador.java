package io.mateu.erp.estresador;

import java.time.LocalDateTime;

public class Estresador {

    public static boolean running;

    public static long pets;
    public static long totalMs;
    public static long tiempoMedioMs;
    public static long tiempoMaximoMs;
    public static long tiempoMinimoMs;

    public static long petsUltimoMinuto;
    public static long totalMsUltimoMinuto;
    public static long tiempoMedioMsUltimoMinuto;
    public static long tiempoMaximoMsUltimoMinuto;
    public static long tiempoMinimoMsUltimoMinuto;

    public static LocalDateTime inicio;
    private static LocalDateTime fin;
    public static LocalDateTime duracion;
    public static boolean terminar;

    public static void estresar(String url, int numeroHilos, int tiempoEnMinutos) throws Exception {
        if (running) throw new Exception("Ya está corriendo otro test");
        else {
            running = true;
            terminar = false;

            pets = 0;
            tiempoMaximoMs = 0;
            tiempoMedioMs = 0;
            tiempoMinimoMs = 0;

            petsUltimoMinuto = 0;
            tiempoMaximoMsUltimoMinuto = 0;
            tiempoMedioMsUltimoMinuto = 0;
            tiempoMinimoMsUltimoMinuto = 0;

            inicio = LocalDateTime.now();
            fin = inicio.plusMinutes(tiempoEnMinutos);
            duracion = LocalDateTime.now();


            System.out.println("Arrancando " + numeroHilos + " hilos...");


            for (int h = 0; h < numeroHilos; h++) {
                Thread t = new Thread(new Cliente(url, h));
                t.start();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!terminar) {
                        if (LocalDateTime.now().isAfter(fin)) terminar = true;
                        if (!terminar) try {
                            duracion = LocalDateTime.now();
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Estres terminado");
                    running = false;
                }
            }).start();


            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!terminar) {
                        if (!terminar) try {
                            Thread.sleep(1000);
                            petsUltimoMinuto = 0;
                            totalMsUltimoMinuto = 0;
                            tiempoMaximoMsUltimoMinuto = 0;
                            tiempoMedioMsUltimoMinuto = 0;
                            tiempoMinimoMsUltimoMinuto = 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }


    public static void main(String... args) throws Exception {
        String url = "http://admin.test.quoon.net/resources/eyAiY3JlYXRlZCI6ICJXZWQgTm92IDA4IDEyOjE4OjQ3IENFVCAyMDE3IiwgInVzZXJJZCI6ICJhZG1pbiIsICJhY3RvcklkIjogIjMiLCAiaG90ZWxJZCI6ICIxMiJ9";

        estresar(url, 1, 1);
    }

}
