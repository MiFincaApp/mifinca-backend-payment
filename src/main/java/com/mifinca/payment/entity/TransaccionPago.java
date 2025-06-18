
package com.mifinca.payment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaccion_pago")
public class TransaccionPago {
    
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_transaccion_wompi", nullable = false, length = 100)
    private String idTransaccionWompi;

    @Column(name = "monto_centavos", nullable = false)
    private Integer montoCentavos;

    @Column(name = "moneda", nullable = false, length = 10)
    private String moneda;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "referencia", length = 100)
    private String referencia;

    @Column(name = "correo_cliente", length = 100)
    private String correoCliente;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdTransaccionWompi() {
        return idTransaccionWompi;
    }

    public void setIdTransaccionWompi(String idTransaccionWompi) {
        this.idTransaccionWompi = idTransaccionWompi;
    }

    public Integer getMontoCentavos() {
        return montoCentavos;
    }

    public void setMontoCentavos(Integer montoCentavos) {
        this.montoCentavos = montoCentavos;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    
}
