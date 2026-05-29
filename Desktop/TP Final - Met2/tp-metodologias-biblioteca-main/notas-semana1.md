# Semana 1 - Refactorización inicial

## Code Smells detectados

### Smell 1 - Cryptic Names (Emprendedor.java)
Los atributos de la clase Emprendedor tenían nombres de una sola letra:
n (nombre), t (teléfono), m (email), cat (categoría).
Era imposible entender el código sin leer los comentarios al lado.
**Solución:** se renombraron a nombres descriptivos: nombre, telefono, email, categoria.

### Smell 2 - God Method / Long Method
- mostrarInfoYValidar() en Emprendedor.java hacía dos cosas a la vez:
  mostrar información Y validar datos.
- El método main() en Main.java tenía demasiadas responsabilidades mezcladas.
- imprimirResumenEjecutivo() en Reportes.java tenía lógica de conteo incrustada.
  **Solución:** cada método fue dividido en métodos más cortos con una sola responsabilidad.

### Smell 3 - Campos públicos (Data Class)
Casi todos los atributos de Emprendedor, Producto, Venta y GestorFeria
eran public, permitiendo modificaciones sin ningún control desde cualquier clase.
**Solución:** todos los atributos pasaron a private con getters y setters donde corresponde.

### Smell 4 - Duplicate Code
- Producto.java tenía dos métodos idénticos: hayStockBajo() e isStockBajo().
- Reportes.java tenía dos métodos casi iguales: generarReportePorCategoria()
  y generarReportePorCategoriaAlternativo().
- La lógica de calcular ventas totales estaba duplicada en dos métodos distintos.
  **Solución:** se eliminaron los duplicados dejando una sola versión en cada caso.

## Refactorizaciones aplicadas

| Archivo | Cambio realizado |
|---|---|
| Emprendedor.java | Renombrado de variables crípticas, separación de mostrarInfo() y validarCompleto() |
| Producto.java | Campos privados, eliminado método duplicado hayStockBajo() |
| Venta.java | Campos privados con getters y setters |
| GestorFeria.java | Listas privadas, extraídos métodos validarDatosEmprendedor() y buscarProducto() |
| Reportes.java | Eliminado método duplicado, extraído contarEmprendedoresConStockBajo() |
| Main.java | Dividido en cargarEmprendedores(), registrarVentas(), mostrarReportes() |
| Validadores.java | Actualizado para usar getters en lugar de campos públicos |

## Principios SOLID aplicados

**SRP - Single Responsibility Principle**
Cada clase y método tiene una única responsabilidad.
Ejemplo: mostrarInfoYValidar() fue separado en mostrarInfo() y validarCompleto().

**OCP - Open/Closed Principle**
Las clases están abiertas a extensión pero cerradas a modificación.
Los campos privados con getters permiten extender sin romper el comportamiento existente.

## Verificación
El programa fue ejecutado antes y después de la refactorización
produciendo exactamente el mismo output en ambos casos.