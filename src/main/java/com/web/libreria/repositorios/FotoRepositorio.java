package com.web.libreria.repositorios;

import com.web.libreria.entidades.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FotoRepositorio extends JpaRepository<Foto, String> {

    
}
