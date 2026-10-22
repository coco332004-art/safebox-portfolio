
package com.proyecto.safebox.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.security.SecureRandom;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Entidad JPA que representa a los usuarios de SafeBox. Incluye credenciales, rol de seguridad, estado de la cuenta, fecha de creación y un salt específico por usuario 
// para derivar claves de cifrado. Está relacionada en una relación uno-a-muchos con la entidad Mensajes.

@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
    }
)
public class Usuarios {

  public enum Role {
    USER, ADMIN
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El nombre no puede estar vacío")
  @Size(max = 120)
  private String nombreCompleto;

  @Email(message = "Formato de correo inválido")
  @NotBlank(message = "El correo es obligatorio")
  @Size(max = 150)
  @Column(nullable = false, unique = true)
  private String email;

  @NotBlank(message = "La contraseña es obligatoria")
  @Size(min = 6, max = 100)
  private String password;

  @Enumerated(EnumType.STRING)  // Enumeración interna que define los roles posibles en el sistema: ADMIN y USER. Se utiliza tanto en la lógica de negocio como en la integración con Spring Security
  @Column(nullable = false, length = 20)
  private Role rol = Role.USER;

  @Column(nullable = false)
  private boolean enabled = true;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  // Salt para la derivación de la llave de cifrado (perUserSalt)
  @Column(name = "encryption_salt", nullable = false, length = 32)
  private String encryptionSalt;

  @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Mensajes> mensajes = new ArrayList<>();

  private static final SecureRandom secureRandom = new SecureRandom(); // Generador seguro de números aleatorios para crear salts criptográficos

  // Método @PrePersist que se ejecuta antes de insertar el registro. Inicializa automáticamente la fecha de creación (createdAt) 
  // y genera un salt aleatorio (encryptionSalt) codificado en Base64, que más adelante se utiliza para derivar la clave de cifrado de los mensajes del usuario.

  @PrePersist 
  public void prePersist() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (encryptionSalt == null) { // Generar un salt aleatorio de 16 bytes
      byte[] salt = new byte[16];
      secureRandom.nextBytes(salt);
      encryptionSalt = Base64.getEncoder().encodeToString(salt);
    }
  }

  // Getters y setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombreCompleto() {
    return nombreCompleto;
  }

  public void setNombreCompleto(String nombreCompleto) {
    this.nombreCompleto = nombreCompleto;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRol() {
    return rol;
  }

  public void setRol(Role rol) {
    this.rol = rol;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getEncryptionSalt() {
    return encryptionSalt;
  }

  public void setEncryptionSalt(String encryptionSalt) {
    this.encryptionSalt = encryptionSalt;
  }

  public List<Mensajes> getMensajes() {
    return mensajes;
  }

  public void setMensajes(List<Mensajes> mensajes) {
    this.mensajes = mensajes;
  }
}
