package com.web.libreria.repositorios;

import com.web.libreria.entidades.Prestamo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestamoRepositorio extends JpaRepository<Prestamo, String> {
    
    
    @Query("SELECT a FROM Prestamo a WHERE a.usuario.id = :id")
    public List<Prestamo> prestamosUsuario(@Param("id") String id);
}
