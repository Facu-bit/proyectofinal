package com.feria.servicios;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class ObservadorLog implements ObservadorFeria {

    private String archivoLog;

    public ObservadorLog(String archivoLog) {
        this.archivoLog = archivoLog;
    }

    @Override
    public void actualizar(String evento, String detalle) {
        try {
            FileWriter fw = new FileWriter(archivoLog, true);
            fw.write(LocalDateTime.now() + " [" + evento + "] " + detalle + "\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Error al escribir el log: " + e.getMessage());
        }
    }
}
