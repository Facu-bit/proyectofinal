package com.barclub.service;

import com.barclub.dto.*;
import com.barclub.entity.*;
import com.barclub.exception.BusinessException;
import com.barclub.exception.ResourceNotFoundException;
import com.barclub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    // ---- Listar todos ----
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ---- Listar activos (para el panel de cocina) ----
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarActivos() {
        return pedidoRepository.findPedidosActivos()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ---- Listar por estado ----
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ---- Listar por fecha ----
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPorFecha(LocalDate fecha) {
        return pedidoRepository.findByFecha(fecha)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ---- Obtener por id ----
    @Transactional(readOnly = true)
    public PedidoResponseDTO obtenerPorId(Long id) {
        return toDTO(pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id)));
    }

    // ---- Crear pedido con detalles ----
    public PedidoResponseDTO crear(PedidoRequestDTO dto) {
        // Validar usuario
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioId()));

        // Validar que si es DELIVERY tenga dirección
        if (dto.getTipo() == TipoPedido.DELIVERY
                && (dto.getDireccionEntrega() == null || dto.getDireccionEntrega().isBlank())) {
            throw new BusinessException("El delivery requiere una dirección de entrega");
        }

        // Construir el pedido
        Pedido pedido = Pedido.builder()
                .fecha(LocalDate.now())
                .hora(LocalTime.now())
                .estado(EstadoPedido.PENDIENTE)
                .tipo(dto.getTipo())
                .total(0.0)
                .usuario(usuario)
                .nombreCliente(dto.getNombreCliente())
                .telefonoCliente(dto.getTelefonoCliente())
                .direccionEntrega(dto.getDireccionEntrega())
                .horarioEntrega(dto.getHorarioEntrega())
                .build();

        // Asociar cliente registrado (opcional)
        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getClienteId()));
            pedido.setCliente(cliente);
        }

        // Guardar pedido primero para obtener el id
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Agregar detalles y calcular total
        double total = 0.0;
        for (DetallePedidoRequestDTO detalleDTO : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", detalleDTO.getProductoId()));

            if (!producto.getActivo()) {
                throw new BusinessException("El producto '" + producto.getNombre() + "' no está disponible");
            }

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedidoGuardado)
                    .producto(producto)
                    .cantidad(detalleDTO.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(producto.getPrecio() * detalleDTO.getCantidad())
                    .build();

            pedidoGuardado.getDetalles().add(detalle);
            total += detalle.getSubtotal();
        }

        // Actualizar total
        pedidoGuardado.setTotal(total);
        return toDTO(pedidoRepository.save(pedidoGuardado));
    }

    // ---- Cambiar estado del pedido ----
    public PedidoResponseDTO cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        // Validar transiciones de estado
        validarTransicionEstado(pedido.getEstado(), nuevoEstado);

        pedido.setEstado(nuevoEstado);
        return toDTO(pedidoRepository.save(pedido));
    }

    // ---- Cancelar pedido (con cláusula de 30 minutos) ----
    public PedidoResponseDTO cancelar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new BusinessException("No se puede cancelar un pedido ya entregado");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new BusinessException("El pedido ya está cancelado");
        }
        // Solo PENDIENTE puede cancelarse (con límite de tiempo)
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException("Solo se pueden cancelar pedidos en estado PENDIENTE. Este pedido está en: " + pedido.getEstado());
        }

        // Verificar la cláusula de 30 minutos
        LocalDateTime creacion = LocalDateTime.of(pedido.getFecha(), pedido.getHora());
        LocalDateTime limite = creacion.plusMinutes(30);
        if (LocalDateTime.now().isAfter(limite)) {
            throw new BusinessException("El tiempo para cancelar este pedido ha vencido (30 minutos desde la creación)");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        return toDTO(pedidoRepository.save(pedido));
    }

    // ---- Agregar producto a pedido existente ----
    public PedidoResponseDTO agregarDetalle(Long pedidoId, DetallePedidoRequestDTO detalleDTO) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", pedidoId));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException("Solo se pueden modificar pedidos en estado PENDIENTE");
        }

        Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", detalleDTO.getProductoId()));

        // Si el producto ya está en el pedido, sumar cantidad
        pedido.getDetalles().stream()
                .filter(d -> d.getProducto().getId().equals(producto.getId()))
                .findFirst()
                .ifPresentOrElse(
                        detalle -> {
                            detalle.setCantidad(detalle.getCantidad() + detalleDTO.getCantidad());
                            detalle.setSubtotal(detalle.getPrecioUnitario() * detalle.getCantidad());
                        },
                        () -> {
                            DetallePedido nuevo = DetallePedido.builder()
                                    .pedido(pedido)
                                    .producto(producto)
                                    .cantidad(detalleDTO.getCantidad())
                                    .precioUnitario(producto.getPrecio())
                                    .subtotal(producto.getPrecio() * detalleDTO.getCantidad())
                                    .build();
                            pedido.getDetalles().add(nuevo);
                        }
                );

        recalcularTotal(pedido);
        return toDTO(pedidoRepository.save(pedido));
    }

    // ---- Eliminar detalle de pedido ----
    public PedidoResponseDTO eliminarDetalle(Long pedidoId, Long detalleId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", pedidoId));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException("Solo se pueden modificar pedidos en estado PENDIENTE");
        }

        pedido.getDetalles().removeIf(d -> d.getId().equals(detalleId));
        recalcularTotal(pedido);
        return toDTO(pedidoRepository.save(pedido));
    }

    // ---- Helpers ----
    private void recalcularTotal(Pedido pedido) {
        double total = pedido.getDetalles().stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
        pedido.setTotal(total);
    }

    private void validarTransicionEstado(EstadoPedido actual, EstadoPedido nuevo) {
        boolean valido = switch (actual) {
            case PENDIENTE -> nuevo == EstadoPedido.PREPARACION || nuevo == EstadoPedido.CANCELADO;
            case PREPARACION -> nuevo == EstadoPedido.LISTO;
            case LISTO -> nuevo == EstadoPedido.ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
        if (!valido) {
            throw new BusinessException(
                "Transición de estado inválida: " + actual + " -> " + nuevo
            );
        }
    }

    private boolean esCancelable(Pedido pedido) {
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) return false;
        LocalDateTime creacion = LocalDateTime.of(pedido.getFecha(), pedido.getHora());
        return LocalDateTime.now().isBefore(creacion.plusMinutes(30));
    }

    // ---- Mapper ----
    public PedidoResponseDTO toDTO(Pedido p) {
        List<DetallePedidoResponseDTO> detalles = p.getDetalles().stream()
                .map(d -> DetallePedidoResponseDTO.builder()
                        .id(d.getId())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .producto(productoService.toDTO(d.getProducto()))
                        .build())
                .collect(Collectors.toList());

        return PedidoResponseDTO.builder()
                .id(p.getId())
                .fecha(p.getFecha())
                .hora(p.getHora())
                .estado(p.getEstado())
                .tipo(p.getTipo())
                .total(p.getTotal())
                .nombreCliente(p.getNombreCliente())
                .telefonoCliente(p.getTelefonoCliente())
                .direccionEntrega(p.getDireccionEntrega())
                .horarioEntrega(p.getHorarioEntrega())
                .cliente(p.getCliente() != null ? clienteService.toDTO(p.getCliente()) : null)
                .usuario(p.getUsuario() != null ? usuarioService.toDTO(p.getUsuario()) : null)
                .detalles(detalles)
                .cancelable(esCancelable(p))
                .build();
    }
}
