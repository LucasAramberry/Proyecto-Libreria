package com.web.libreria.servicios;

import com.web.libreria.entidades.Libro;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.repositorios.LibroRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroServicio {

    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private EditorialServicio editorialServicio;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void agregarLibro(Integer isbn, String titulo, Integer anio, Integer ejemplares, Integer ejemplaresPrestados, Integer ejemplaresRestantes, String nombreAutor, String nombreEditorial) throws ErrorServicio {

        validar(titulo, isbn, anio, ejemplares, ejemplaresPrestados, ejemplaresRestantes, nombreAutor, nombreEditorial);

        Libro libro = new Libro();

        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setAnio(anio);
        libro.setEjemplares(ejemplares);
        libro.setEjemplaresPrestados(ejemplaresPrestados);
        libro.setEjemplaresRestantes(ejemplaresRestantes);
        libro.setAlta(true);

        libro.setAutor(autorServicio.ingresarAutor(nombreAutor));

        libro.setEditorial(editorialServicio.ingresarEditorial(nombreEditorial));

        libroRepositorio.save(libro);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void modificarLibro(String id, String titulo, Integer isbn, Integer anio, Integer ejemplares, Integer ejemplaresPrestados, Integer ejemplaresRestantes, String nombreAutor, String nombreEditorial) throws ErrorServicio {

        validar(titulo, isbn, anio, ejemplares, ejemplaresPrestados, ejemplaresRestantes, nombreAutor, nombreEditorial);

        Optional<Libro> respuesta = libroRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();

            libro.setIsbn(isbn);
            libro.setTitulo(titulo);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);
            libro.setEjemplaresPrestados(ejemplaresPrestados);
            libro.setEjemplaresRestantes(ejemplaresRestantes);
            libro.setAlta(true);

            libro.setAutor(autorServicio.modificarAutor(libro.getAutor().getId(), nombreAutor));

            libro.setEditorial(editorialServicio.modificarEditorial(libro.getEditorial().getId(), nombreEditorial));

            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("No se encontro el libro solicitado");
        }
    }

    @Transactional(readOnly = true)
    public List<Libro> listarLibros() {
        return libroRepositorio.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Libro alta(String id) {

        Libro entidad = libroRepositorio.getOne(id);

        entidad.setAlta(true);
        return libroRepositorio.save(entidad);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Libro baja(String id) {

        Libro entidad = libroRepositorio.getOne(id);

        entidad.setAlta(false);
        return libroRepositorio.save(entidad);
    }

    @Transactional(readOnly = true)
    public Libro getOne(String id) {
        return libroRepositorio.getOne(id);
    }

    public void validar(String titulo, Integer isbn, Integer anio, Integer ejemplares, Integer ejemplaresPrestados, Integer ejemplaresRestantes, String nombreAutor, String nombreEditorial) throws ErrorServicio {

        if (titulo == null || titulo.isEmpty()) {
            throw new ErrorServicio("El titulo no puede ser nulo");
        }
//        if (Integer.toString(isbn).length() != 14) {
//            throw new ErrorServicio("El isbn no contener ni menos ni mas de 14 numeros");
//        }
        if (anio == 0 || anio > 2021) {
            throw new ErrorServicio("El año no puede ser menor a 1800 o mayor a 2021");
        }
        if (ejemplares < 1) {
            throw new ErrorServicio("Ejemplares no pueden ser menor a 1");
        }
        if (ejemplaresPrestados > ejemplares) {
            throw new ErrorServicio("Los ejemplares prestados no pueden ser mas que la cantidad de ejemplares");
        }
        if (ejemplaresRestantes > ejemplares) {
            throw new ErrorServicio("Los ejemplares restantes no pueden ser mas que la cantidad de ejemplares");
        }
        if (nombreAutor == null || nombreAutor.isEmpty()) {
            throw new ErrorServicio("El nomrbe del Autor no puede ser nulo");
        }
        if (nombreEditorial == null || nombreEditorial.isEmpty()) {
            throw new ErrorServicio("El nombre de la editorial no puede ser nulo");
        }
    }
}
