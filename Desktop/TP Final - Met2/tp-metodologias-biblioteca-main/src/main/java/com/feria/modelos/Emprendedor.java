package com.feria.modelos;

import java.util.ArrayList;
import java.util.List;

public class Emprendedor {

    // SMELL 1 CORREGIDO: nombres de variables crypticos (n, t, m, cat)
    // renombrados a nombres descriptivos
    private String nombre;
    private String id;
    private String telefono;
    private String email;
    private String categoria;

    private List<Producto> productos;

    public Emprendedor(String nombre, String id, String telefono, String email, String categoria) {
        this.nombre = nombre;
        this.id = id;
        this.telefono = telefono;
        this.email = email;
        this.categoria = categoria;
        this.productos = new ArrayList<>();
    }

    // SMELL 2 CORREGIDO: mostrarInfoYValidar() hacia dos cosas a la vez
    // ahora son dos metodos separados (SRP)
    public String mostrarInfo() {
        String info = "Emprendedor: " + nombre + "\n";
        info += "ID: " + id + "\n";
        info += "Contacto: " + telefono + " | " + email + "\n";
        info += "Categoría: " + categoria + "\n";
        info += "Productos:\n";
        for (Producto p : productos) {
            info += "  - " + p.getNombre() + " ($" + p.getPrecio() + ")\n";
        }
        return info;
    }

    public boolean validarCompleto() {
        if (nombre == null || nombre.length() < 2) return false;
        if (email == null || !email.contains("@")) return false;
        if (categoria == null || (!categoria.equals("comida") && !categoria.equals("artesania")
                && !categoria.equals("tecnologia") && !categoria.equals("ropa"))) return false;
        return true;
    }

    public void agregarProducto(Producto p) {
        productos.add(p);
    }

    public int calcularValorTotalStock() {
        int total = 0;
        for (Producto p : productos) {
            total += p.getPrecio() * p.getStock();
        }
        return total;
    }

    // Getters
    public String getNombre()    { return nombre; }
    public String getId()        { return id; }
    public String getTelefono()  { return telefono; }
    public String getEmail()     { return email; }
    public String getCategoria() { return categoria; }
    public List<Producto> getProductos() { return productos; }
}