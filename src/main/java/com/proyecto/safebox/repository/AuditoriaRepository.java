
package com.proyecto.safebox.repository;

import com.proyecto.safebox.model.AuditoriaEvento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

//Repositorio JPA para la entidad AuditoriaEvento. Hereda de JpaRepository y permite almacenar y consultar eventos de auditoría.

public interface AuditoriaRepository extends JpaRepository<AuditoriaEvento, Long> {

     Page<AuditoriaEvento> findAllByOrderByTimestampDesc(Pageable pageable);

}
