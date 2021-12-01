package com.web.libreria.servicios;

import com.web.libreria.entidades.Autor;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.repositorios.AutorRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutorServicio {

    @Autowired
    private AutorRepositorio autorRepositorio;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Autor ingresarAutor(String nombre) throws ErrorServicio {

        validarAutor(nombre);

        Autor autor = new Autor();

        autor.setNombre(nombre);
        autor.setAlta(true);

        return autorRepositorio.save(autor);
    }

    @Transactional
    public Autor modificarAutor(String id, String nombre) throws ErrorServicio {
        
        validarAutor(nombre);
        Optional<Autor> respuesta = autorRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autor.setNombre(nombre);
            autor.setAlta(true);
            return autorRepositorio.save(autor);
            
        } else {
            throw new ErrorServicio("No se encontro el autor solicitado.");
        }
    }

    @Transactional(readOnly = true)
    public List<Autor> listarAutores() {
        return autorRepositorio.findAll();
    }
    
    @Transactional(readOnly = true)
    public Autor getOne(String id) {
        return autorRepositorio.getOne(id);
    }
    
    public Autor alta(String id) {

        Autor entidad = autorRepositorio.getOne(id);

        entidad.setAlta(true);
        return autorRepositorio.save(entidad);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Autor baja(String id) {

        Autor entidad = autorRepositorio.getOne(id);

        entidad.setAlta(false);
        return autorRepositorio.save(entidad);
    }

    private void validarAutor(String nombre) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del autor no puede ser nulo");
        }
    }
}
