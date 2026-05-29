package com.feria.modelos;

public class Producto {

    // SMELL 3 CORREGIDO: campos publicos reemplazados por privados con getters
    private String nombre;
    private double precio;
    private int stock;
    private String categoriaProducto;
    private String emprendedorId;

    public Producto(String nombre, double precio, int stock, String categoriaProd, String empId) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoriaProducto = categoriaProd;
        this.emprendedorId = empId;
    }

    public double valorTotal() {
        return precio * stock;
    }

    public String mostrar() {
        return nombre + " - $" + precio + " (stock: " + stock + ")";
    }

    // SMELL 4 CORREGIDO: habia dos metodos identicos hayStockBajo() e isStockBajo()
    // se elimina el duplicado y se deja solo uno con nombre claro
    public boolean isStockBajo() {
        return stock < 5;
    }

    public void reducirStock(int cantidad) {
        this.stock -= cantidad;
    }

    // Getters y setter de stock
    public String getNombre()          { return nombre; }
    public double getPrecio()          { return precio; }
    public int getStock()              { return stock; }
    public String getCategoriaProducto() { return categoriaProducto; }
    public String getEmprendedorId()   { return emprendedorId; }
    public void setStock(int stock)    { this.stock = stock; }
}