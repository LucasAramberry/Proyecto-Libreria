package com.web.libreria.repositorios;

import com.web.libreria.entidades.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {
    
    @Query("SELECT c FROM Usuario c WHERE c.mail = :mail")
    public Usuario buscarPorMail(@Param("mail") String mail);
    
    // Devuelve una Lista con Usuarios dados de alta.
    @Query("SELECT u FROM Usuario u WHERE u.baja IS null")
    public List<Usuario> buscarActivos();

    // Devuelve una Lista con Usuarios dados de baja.
    @Query("SELECT u FROM Usuario u WHERE u.baja IS NOT null")
    public List<Usuario> buscarInactivos();
    
}
