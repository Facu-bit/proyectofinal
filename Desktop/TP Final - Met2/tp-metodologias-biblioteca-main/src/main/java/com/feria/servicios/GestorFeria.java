package com.feria.servicios;

import com.feria.modelos.Emprendedor;
import com.feria.modelos.Producto;
import com.feria.modelos.Venta;
import java.util.ArrayList;
import java.util.List;

public class GestorFeria {

    private List<Emprendedor> emprendedores;
    private List<Producto> productos;
    private List<Venta> ventas;
    private List<ObservadorFeria> observadores;

    public GestorFeria() {
        emprendedores = new ArrayList<>();
        productos = new ArrayList<>();
        ventas = new ArrayList<>();
        observadores = new ArrayList<>();
    }

    public void agregarObservador(ObservadorFeria observador) {
        observadores.add(observador);
    }

    public void quitarObservador(ObservadorFeria observador) {
        observadores.remove(observador);
    }

    private void notificar(String evento, String detalle) {
        for (ObservadorFeria obs : observadores) {
            obs.actualizar(evento, detalle);
        }
    }

    public void registrarEmprendedorConProductos(String nombre, String id, String telefono,
                                                 String email, String categoria,
                                                 List<String> nombresProductos,
                                                 List<Double> precios,
                                                 List<Integer> stocks) {
        if (!validarDatosEmprendedor(nombre, email)) return;

        Emprendedor e = new Emprendedor(nombre, id, telefono, email, categoria);

        for (int i = 0; i < nombresProductos.size(); i++) {
            Producto p = new Producto(nombresProductos.get(i), precios.get(i), stocks.get(i), categoria, id);
            e.agregarProducto(p);
            productos.add(p);
        }

        emprendedores.add(e);
        notificar("REGISTRO", "Emprendedor registrado: " + nombre + " con " + nombresProductos.size() + " productos");
    }

    private boolean validarDatosEmprendedor(String nombre, String email) {
        if (nombre == null || nombre.length() < 2) {
            notificar("ERROR", "Nombre de emprendedor invalido");
            return false;
        }
        if (email == null || !email.contains("@")) {
            notificar("ERROR", "Email de emprendedor invalido");
            return false;
        }
        return true;
    }

    public void registrarVenta(String idVenta, String empId, String prodNombre,
                               int cantidad, double precio, String fecha) {
        Producto productoEncontrado = buscarProducto(prodNombre, empId);

        if (productoEncontrado == null) {
            notificar("ERROR", "Producto no encontrado: " + prodNombre);
            return;
        }

        if (productoEncontrado.getStock() < cantidad) {
            notificar("ERROR", "Stock insuficiente para: " + prodNombre);
            return;
        }

        Venta v = new Venta(idVenta, empId, prodNombre, cantidad, precio, fecha);
        ventas.add(v);
        productoEncontrado.reducirStock(cantidad);
        notificar("VENTA", "Venta registrada: " + prodNombre + " - Nuevo stock: " + productoEncontrado.getStock());
    }

    private Producto buscarProducto(String nombre, String empId) {
        for (Producto p : productos) {
            if (p.getNombre().equals(nombre) && p.getEmprendedorId().equals(empId)) {
                return p;
            }
        }
        return null;
    }

    public void procesarVentasPendientesYCobrar() {
        double totalRecaudado = 0;
        for (Venta v : ventas) {
            if (!v.isPagoRealizado()) {
                double monto = v.calcularTotalConDescuento();
                totalRecaudado += monto;
                v.setPagoRealizado(true);
                notificar("COBRO", "Cobrada venta " + v.getIdVenta() + " por $" + monto);
            }
        }
        notificar("COBRO", "Total recaudado: $" + totalRecaudado);
    }

    public List<Emprendedor> getEmprendedoresConStockBajo() {
        List<Emprendedor> resultado = new ArrayList<>();
        for (Emprendedor e : emprendedores) {
            for (Producto p : e.getProductos()) {
                if (p.isStockBajo()) {
                    resultado.add(e);
                    break;
                }
            }
        }
        return resultado;
    }

    public List<Emprendedor> getEmprendedores() { return emprendedores; }
    public List<Producto> getProductos()         { return productos; }
    public List<Venta> getVentas()               { return ventas; }
}