package com.feria;

import com.feria.modelos.Emprendedor;
import com.feria.modelos.Producto;
import com.feria.servicios.GestorFeria;
import com.feria.servicios.ObservadorConsola;
import com.feria.servicios.ObservadorLog;
import com.feria.servicios.Reportes;
import com.feria.utils.Validadores;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        GestorFeria gestor = new GestorFeria();
        Reportes reportes = new Reportes();

        // PATRON OBSERVER: registramos los observadores
        gestor.agregarObservador(new ObservadorConsola());
        gestor.agregarObservador(new ObservadorLog("feria-log.txt"));

        cargarEmprendedores(gestor);
        registrarVentas(gestor);
        mostrarReportes(gestor, reportes);
    }

    private static void cargarEmprendedores(GestorFeria gestor) {
        gestor.registrarEmprendedorConProductos(
                "Ana", "E001", "3423456789", "ana@gmail.com", "comida",
                Arrays.asList("Empanadas", "Tortas", "Alfajores"),
                Arrays.asList(500.0, 1500.0, 300.0),
                Arrays.asList(50, 10, 100)
        );

        Emprendedor emp2 = new Emprendedor("Carlos", "E002", "3423987654", "carlos@hotmail.com", "artesania");
        Producto p1 = new Producto("Collar", 2000.0, 5, "artesania", "E002");
        Producto p2 = new Producto("Pulsera", 800.0, 20, "artesania", "E002");
        emp2.agregarProducto(p1);
        emp2.agregarProducto(p2);
        gestor.getEmprendedores().add(emp2);
        gestor.getProductos().add(p1);
        gestor.getProductos().add(p2);
    }

    private static void registrarVentas(GestorFeria gestor) {
        gestor.registrarVenta("V001", "E001", "Empanadas", 10, 500.0, "2026-05-12");
        gestor.registrarVenta("V002", "E002", "Collar", 1, 2000.0, "2026-05-12");
    }

    private static void mostrarReportes(GestorFeria gestor, Reportes reportes) {
        System.out.println(reportes.generarReportePorCategoria(gestor, "comida"));
        gestor.procesarVentasPendientesYCobrar();
        reportes.imprimirResumenEjecutivo(gestor);
        System.out.println("Emprendedor Ana valido? " +
                Validadores.validarEmprendedorCompleto(gestor.getEmprendedores().get(0)));
        System.out.println(gestor.getEmprendedores().get(0).mostrarInfo());
    }
}