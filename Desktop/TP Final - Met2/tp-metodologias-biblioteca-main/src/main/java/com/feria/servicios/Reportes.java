package com.feria.servicios;

import com.feria.modelos.Emprendedor;
import com.feria.modelos.Producto;
import com.feria.modelos.Venta;

public class Reportes {

    // SMELL 5 CORREGIDO: habia dos metodos casi identicos
    // generarReportePorCategoria() y generarReportePorCategoriaAlternativo()
    // se elimina el duplicado y se deja uno solo completo
    public String generarReportePorCategoria(GestorFeria gestor, String categoria) {
        String reporte = "=== REPORTE DE EMPRENDEDORES - CATEGORÍA: " + categoria + " ===\n";
        for (Emprendedor e : gestor.getEmprendedores()) {
            if (e.getCategoria().equals(categoria)) {
                reporte += e.mostrarInfo();
                reporte += "---\n";
            }
        }
        return reporte;
    }

    public double calcularVentasTotales(GestorFeria gestor) {
        double total = 0;
        for (Venta v : gestor.getVentas()) {
            total += v.calcularTotalConDescuento();
        }
        return total;
    }

    public void imprimirResumenEjecutivo(GestorFeria gestor) {
        System.out.println("========== RESUMEN EJECUTIVO ==========");
        System.out.println("Total emprendedores: " + gestor.getEmprendedores().size());
        System.out.println("Total productos: " + gestor.getProductos().size());
        System.out.println("Total ventas: " + gestor.getVentas().size());
        System.out.println("Total facturado: $" + calcularVentasTotales(gestor));
        System.out.println("Emprendedores con stock bajo: " + contarEmprendedoresConStockBajo(gestor));
        System.out.println("=======================================");
    }

    // SMELL 2 CORREGIDO: logica de contar stock bajo estaba duplicada dentro de
    // imprimirResumenEjecutivo(), ahora es un metodo separado (SRP)
    private int contarEmprendedoresConStockBajo(GestorFeria gestor) {
        int contador = 0;
        for (Emprendedor e : gestor.getEmprendedores()) {
            for (Producto p : e.getProductos()) {
                if (p.isStockBajo()) {
                    contador++;
                    break;
                }
            }
        }
        return contador;
    }
}