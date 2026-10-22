package com.proyecto.safebox.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.safebox.model.Mensajes;

public interface  MensajesRepository extends JpaRepository<Mensajes, Long> { //Hereda operaciones CRUD y funcionalidad JPA completa.

  List<Mensajes> findByUsuarioId(Long usuarioId); //Devuelve una lista de mensajes filtrados por el ID del usuario al que pertenecen.

  Page<Mensajes> findAllByOrderByIdDesc(Pageable pageable); 



}
