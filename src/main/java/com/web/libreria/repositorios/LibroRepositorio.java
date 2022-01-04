package com.web.libreria.repositorios;

import com.web.libreria.entidades.Libro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepositorio extends JpaRepository<Libro, String> {

    // Método que devuelve el/los Libro/s vinculado a un Autor:
    @Query("SELECT a FROM Libro a WHERE a.autor.id = :id")
    public List<Libro> buscarPorAutor(@Param("id") String id);

    // Método que devuelve el/los Libro/s vinculado a una Editorial:
    @Query("SELECT a FROM Libro a WHERE a.editorial.id = :id")
    public List<Libro> buscarPorEditorial(@Param("id") String id);

    // Método que devuelve los libros activos
    @Query("SELECT a FROM Libro a WHERE a.alta = true")
    public List<Libro> listalibrosActivos();
    
}
