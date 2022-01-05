package com.web.libreria.servicios;

import com.web.libreria.entidades.Foto;
import com.web.libreria.entidades.Libro;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.repositorios.LibroRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

//Servicio de la entidad Libro
@Service
public class LibroServicio {

    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private EditorialServicio editorialServicio;
    @Autowired
    private FotoServicio fotoServicio;

    //------------------------------METODOS CRUD-----------------------------------
    /**
     * Metodo para registrar Libro
     *
     * @param archivo
     * @param isbn
     * @param titulo
     * @param descripcion
     * @param anio
     * @param ejemplares
     * @param idAutor
     * @param nombreAutor
     * @param idEditorial
     * @param nombreEditorial
     * @throws ErrorServicio
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void agregarLibro(MultipartFile archivo, String isbn, String titulo, String descripcion, Integer anio, Integer ejemplares, String idAutor, String nombreAutor, String idEditorial, String nombreEditorial) throws ErrorServicio {

        Libro libro = new Libro();

        validar(titulo, descripcion, isbn, anio, ejemplares);

        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setDescripcion(descripcion);
        libro.setAnio(anio);
        libro.setEjemplares(ejemplares);
        libro.setEjemplaresPrestados(0);
        libro.setEjemplaresRestantes(ejemplares);
        libro.setAlta(true);
        Foto foto = fotoServicio.guardar(archivo);
        libro.setFoto(foto);

        if (nombreAutor == null || nombreAutor.isEmpty()) {
            libro.setAutor(autorServicio.getOne(idAutor));
            validarAutor(libro.getAutor().getNombre());
        } else {
            libro.setAutor(autorServicio.ingresarAutor(nombreAutor));
            validarAutor(libro.getAutor().getNombre());
        }

        if (nombreEditorial == null || nombreEditorial.isEmpty()) {
            libro.setEditorial(editorialServicio.getOne(idEditorial));
            validarEditorial(libro.getEditorial().getNombre());
        } else {
            libro.setEditorial(editorialServicio.ingresarEditorial(nombreEditorial));
            validarEditorial(libro.getEditorial().getNombre());
        }

        libroRepositorio.save(libro);
    }

    /**
     * Metodo para modificar libro
     *
     * @param archivo
     * @param id
     * @param titulo
     * @param descripcion
     * @param isbn
     * @param anio
     * @param ejemplares
     * @param idAutor
     * @param nombreAutor
     * @param idEditorial
     * @param nombreEditorial
     * @throws ErrorServicio
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void modificarLibro(MultipartFile archivo, String id, String titulo, String descripcion, String isbn, Integer anio, Integer ejemplares, String idAutor, String nombreAutor, String idEditorial, String nombreEditorial) throws ErrorServicio {

        validar(titulo, descripcion, isbn, anio, ejemplares);

        Optional<Libro> respuesta = libroRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();

            libro.setIsbn(isbn);
            libro.setTitulo(titulo);
            libro.setDescripcion(descripcion);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);
            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados());
            libro.setEjemplaresRestantes(libro.getEjemplaresRestantes());
            libro.setAlta(true);
            Foto foto = fotoServicio.guardar(archivo);
            libro.setFoto(foto);

            if (nombreAutor == null || nombreAutor.isEmpty()) {
                libro.setAutor(autorServicio.getOne(idAutor));
                validarAutor(libro.getAutor().getNombre());
            } else {
                libro.setAutor(autorServicio.ingresarAutor(nombreAutor));
                validarAutor(libro.getAutor().getNombre());
            }

            if (nombreEditorial == null || nombreEditorial.isEmpty()) {
                libro.setEditorial(editorialServicio.getOne(idEditorial));
                validarEditorial(libro.getEditorial().getNombre());
            } else {
                libro.setEditorial(editorialServicio.ingresarEditorial(nombreEditorial));
                validarEditorial(libro.getEditorial().getNombre());
            }

            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("No se encontro el libro solicitado");
        }
    }

    /**
     * Metodo para dar de alta un libro
     *
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Libro alta(String id) {

        Libro entidad = libroRepositorio.getOne(id);
        entidad.setAlta(true);

        return libroRepositorio.save(entidad);
    }

    /**
     * Metodo para dar de baja un libro
     *
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Libro baja(String id) {

        Libro entidad = libroRepositorio.getOne(id);
        entidad.setAlta(false);

        return libroRepositorio.save(entidad);
    }

    /**
     * Metodo para eliminar un libro
     *
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void delete(String id) {

        Libro libro = getOne(id);
        libroRepositorio.delete(libro);
    }

    //----------------------------METODOS VALIDACION-----------------------------------
    /**
     * Metodo para validar datos del libro
     *
     * @param titulo
     * @param descripcion
     * @param isbn
     * @param anio
     * @param ejemplares
     * @throws ErrorServicio
     */
    public void validar(String titulo, String descripcion, String isbn, Integer anio, Integer ejemplares) throws ErrorServicio {

        if (titulo == null || titulo.isEmpty()) {
            throw new ErrorServicio("El titulo no puede ser nulo");
        }
        if (descripcion == null || descripcion.isEmpty()) {
            throw new ErrorServicio("La descripcion no puede ser nula");
        }
        if (isbn == null || isbn.isEmpty() || isbn.length() != 14) {
            throw new ErrorServicio("El isbn no puede ser nulo y debe contener ni 14 numeros");
        }
        if (anio == 0 || anio > 2021) {
            throw new ErrorServicio("El año no puede ser menor a 1800 o mayor a 2021");
        }
        if (ejemplares < 1) {
            throw new ErrorServicio("Ejemplares no pueden ser menor a 1");
        }
//        if (ejemplaresPrestados > ejemplares) {
//            throw new ErrorServicio("Los ejemplares prestados no pueden ser mas que la cantidad de ejemplares");
//        }
//        if (ejemplaresRestantes > ejemplares) {
//            throw new ErrorServicio("Los ejemplares restantes no pueden ser mas que la cantidad de ejemplares");
//        }
    }

    /**
     * Metodo para validar nombre autor
     *
     * @param nombreAutor
     * @throws ErrorServicio
     */
    public void validarAutor(String nombreAutor) throws ErrorServicio {
        if (nombreAutor == null || nombreAutor.isEmpty()) {
            throw new ErrorServicio("El autor no puede ser nulo");
        }
    }

    /**
     * Metodo para validar nombre editorial
     *
     * @param nombreEditorial
     * @throws ErrorServicio
     */
    public void validarEditorial(String nombreEditorial) throws ErrorServicio {
        if (nombreEditorial == null || nombreEditorial.isEmpty()) {
            throw new ErrorServicio("La editorial no puede ser nulo");
        }
    }

    /**
     * metodo para restar ejemplares prestados
     *
     * @param libro
     * @throws Exception
     */
    @Transactional
    public void prestamoLibro(Libro libro) throws Exception {
        if (libro.getEjemplaresRestantes() >= 1) {
            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + 1);
            libro.setEjemplaresRestantes(libro.getEjemplares() - libro.getEjemplaresPrestados());
        } else {
            throw new Exception("No hay suficientes ejemplares disponibles para realizar el préstamo.");
        }
    }

    //----------------------------METODOS CONSULTA DB------------------------------
    /**
     * Metodo para buscar autor por nombre
     *
     * @param idAutor
     * @return
     */
    @Transactional(readOnly = true)
    public List<Libro> buscarPorAutor(String idAutor) {
        return libroRepositorio.buscarPorAutor(idAutor);
    }

    /**
     * Metodo para buscar editorial por nombre
     *
     * @param idEditorial
     * @return
     */
    @Transactional(readOnly = true)
    public List<Libro> buscarPorEditorial(String idEditorial) {
        return libroRepositorio.buscarPorEditorial(idEditorial);
    }

    /**
     * Metodo para traer un libro por id
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Libro getById(String id) {
        return libroRepositorio.getById(id);
    }

    /**
     * Metodo para traer un libro por id
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Libro getOne(String id) {
        return libroRepositorio.getOne(id);
    }

    /**
     * Metodo para traer una lista de todos los libros ingresados
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Libro> listarLibros() {
        return libroRepositorio.findAll();
    }

    /**
     * Metodo para traer una lista de todos los libros ingresados que esten activos
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Libro> listarLibrosActivos() {
        return libroRepositorio.listalibrosActivos();
    }

}
