
package com.proyecto.safebox.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Entidad JPA que representa los mensajes seguros almacenados por la aplicación. Contiene tanto el texto plano (cuando se trabaja en memoria) como el contenido cifrado 
// y los metadatos necesarios para descifrarlo. Cada mensaje pertenece a un usuario (relación ManyToOne).

@Entity
@Table(name = "mensajes")
public class Mensajes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 120)
  private String titulo;

  // Campo en texto plano (solo en memoria)
  @Transient // No se persiste en la base de datos
  private String contenido;

  // Contenido cifrado almacenado en BD (Base64)
  @Lob
  @Column(name = "contenido_enc", nullable = false)
  private String contenidoEnc;

  // Nonce único por mensaje (Base64)
  @Column(name = "nonce", nullable = false, length = 32)
  private String nonce;

  @Column(nullable = false, updatable = false)
  private LocalDateTime fechaCreacion;

  @Column(nullable = false)
  private LocalDateTime fechaActualizacion;

  @ManyToOne(fetch = FetchType.LAZY, optional = false) // fetch controla la carga: LAZY (diferida) vs EAGER (inmediata)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuarios usuario;

  @PrePersist
  public void prePersist() {
    this.fechaCreacion = LocalDateTime.now();
    this.fechaActualizacion = this.fechaCreacion;
  }

  @PreUpdate // Método @PreUpdate que actualiza la fecha de actualización (fechaActualizacion) cada vez que el mensaje se modifica.
  public void preUpdate() {
    this.fechaActualizacion = LocalDateTime.now();
  }

  // Getters y setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getContenido() {
    return contenido;
  }

  public void setContenido(String contenido) {
    this.contenido = contenido;
  }

  public String getContenidoEnc() {
    return contenidoEnc;
  }

  public void setContenidoEnc(String contenidoEnc) {
    this.contenidoEnc = contenidoEnc;
  }

  public String getNonce() {
    return nonce;
  }

  public void setNonce(String nonce) { 
    this.nonce = nonce;
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

  public Usuarios getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuarios usuario) {
    this.usuario = usuario;
  }
}
