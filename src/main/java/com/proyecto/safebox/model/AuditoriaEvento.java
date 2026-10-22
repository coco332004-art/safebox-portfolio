
package com.proyecto.safebox.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// Entidad JPA que registra los eventos de auditoría del sistema: logins correctos o fallidos, operaciones sobre ítems (mensajes) y otros eventos relevantes. 
// Permite reconstruir qué usuario hizo qué acción y cuándo.

@Entity
@Table(name = "auditoria_eventos")
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @Column(length = 50, nullable = false)
    private String tipo; // LOGIN_SUCCESS, LOGIN_FAILURE, CREATE_ITEM, READ_ITEM, UPDATE_ITEM, DELETE_ITEM

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(length = 255)
    private String detalle;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
