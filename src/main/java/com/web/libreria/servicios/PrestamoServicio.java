package com.web.libreria.servicios;

import com.web.libreria.entidades.Libro;
import com.web.libreria.entidades.Usuario;
import com.web.libreria.entidades.Prestamo;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.repositorios.LibroRepositorio;
import com.web.libreria.repositorios.PrestamoRepositorio;
import com.web.libreria.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoServicio {

    @Autowired
    private PrestamoRepositorio prestamoRepositorio;
    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void agregarPrestamo(Date fechaPrestamo, Date fechaDevolucion, String idLibro, String idUsuario) throws ErrorServicio, Exception {

        Usuario usuario = usuarioRepositorio.findById(idUsuario).get();
        Libro libro = libroRepositorio.findById(idLibro).get();
        Prestamo prestamo = new Prestamo();
        validar(fechaPrestamo, fechaDevolucion, idLibro, idUsuario);
        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setFechaDevolucion(fechaDevolucion);
        prestamo.setAlta(true);

        try {
            validarEjemplaresLibroPrestamo(libro);
            prestamo.setLibro(libro);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        prestamo.setUsuario(usuario);

        prestamoRepositorio.save(prestamo);
    }

    public void validar(Date fechaPrestamo, Date fechaDevolucion, String idLibro, String idUsuario) throws Exception {
        if (fechaPrestamo == null) {
            throw new Exception("Fecha de Préstamo no válida.");
        }
        if (fechaDevolucion == null) {
            throw new Exception("Fecha de Devolución no válida.");
        }
        if (fechaPrestamo.after(fechaDevolucion)) {
            throw new Exception("La fecha de retiro del Libro ingresada es posterior a la de devolución.");
        }
        if (idLibro == null || libroServicio.getOne(idLibro) == null) {
            throw new Exception("Id de Libro no válido.");
        }
        if (idUsuario == null || usuarioServicio.getById(idUsuario) == null) {
            throw new Exception("Id de Usuario no válido.");
        }
    }

    public void validarEjemplaresLibroPrestamo(Libro libro) throws Exception {
        try {
            libroServicio.prestamoLibro(libro);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Metodo para eliminar un prestamo
     *
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void delete(String id) {

        Prestamo prestamo = getOne(id);
        prestamoRepositorio.delete(prestamo);
    }
    
    /**
     * Metodo para traer un prestamo por id
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Prestamo getOne(String id) {
        return prestamoRepositorio.getOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<Prestamo> listaPrestamos() {
        return prestamoRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listaPrestamosUsuario(String idUsuario) {
        return prestamoRepositorio.prestamosUsuario(idUsuario);
    }
}
