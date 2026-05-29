package com.feria.servicios;

public class ObservadorConsola implements ObservadorFeria {

    @Override
    public void actualizar(String evento, String detalle) {
        System.out.println("[" + evento + "] " + detalle);
    }
}
