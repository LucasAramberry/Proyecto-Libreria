package com.web.libreria.servicios;

import com.web.libreria.entidades.Autor;
import com.web.libreria.entidades.Editorial;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.repositorios.EditorialRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditorialServicio {

    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Editorial ingresarEditorial(String nombre) throws ErrorServicio {

        validar(nombre);

        Editorial editorial = new Editorial();

        editorial.setNombre(nombre);
        editorial.setAlta(true);

        return editorialRepositorio.save(editorial);
    }

    @Transactional
    public Editorial modificarEditorial(String id, String nombre) throws ErrorServicio {
        
        validar(nombre);
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            
            Editorial editorial = respuesta.get();
            editorial.setNombre(nombre);
            editorial.setAlta(true);
            
            return editorialRepositorio.save(editorial);
            
        } else {
            throw new ErrorServicio("No se encontro la editorial solicitada.");
        }
    }

    @Transactional(readOnly = true)
    public List<Editorial> listarEditoriales() {
        return editorialRepositorio.findAll();
    }
    
    @Transactional(readOnly = true)
    public Editorial getOne(String id) {
        return editorialRepositorio.getOne(id);
    }
    
    public Editorial alta(String id) {

        Editorial entidad = editorialRepositorio.getOne(id);

        entidad.setAlta(true);
        return editorialRepositorio.save(entidad);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Editorial baja(String id) {

        Editorial entidad = editorialRepositorio.getOne(id);

        entidad.setAlta(false);
        return editorialRepositorio.save(entidad);
    }
    
    public void validar(String nombre) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre no puede ser nulo");
        }
    }
}
