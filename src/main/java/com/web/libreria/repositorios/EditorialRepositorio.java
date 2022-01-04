package com.web.libreria.repositorios;

import com.web.libreria.entidades.Editorial;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorialRepositorio extends JpaRepository<Editorial, String> {
    
    // Devuelve una Lista con autores activos.
    @Query("SELECT a FROM Editorial a WHERE a.alta = true")
    public List<Editorial> buscarActivos();
}
