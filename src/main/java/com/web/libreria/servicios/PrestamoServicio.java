package com.web.libreria.servicios;

import com.web.libreria.entidades.Libro;
import com.web.libreria.entidades.Usuario;
import com.web.libreria.entidades.Prestamo;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.repositorios.LibroRepositorio;
import com.web.libreria.repositorios.PrestamoRepositorio;
import com.web.libreria.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.Optional;
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void agregarPrestamo(Date fechaPrestamo, Date fechaDevolucion, String idLibro, String idUsuario) throws ErrorServicio {

        Usuario usuario = usuarioRepositorio.findById(idUsuario).get();
        Libro libro = libroRepositorio.findById(idLibro).get();
        Prestamo prestamo = new Prestamo();

        prestamo.setFechaPrestamo(new Date());
        prestamo.setFechaDevolucion(new Date());
        prestamo.setAlta(true);
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);

        prestamoRepositorio.save(prestamo);
    }

//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
//    public void modificarPrestamo(String id, Long documento, String nombre, String apellido, String telefono) throws ErrorServicio {
//        
//        validar(documento, nombre, apellido, telefono);
//        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
//        
//        if (respuesta.isPresent()) {
//            Usuario cliente = respuesta.get();
//            
//            cliente.setNombre(nombre);
//            cliente.setApellido(apellido);
//            cliente.setTelefono(telefono);
//            cliente.setAlta(true);
//            
//            clienteRepositorio.save(cliente);
//        } else {
//            throw new ErrorServicio("No se encontro el cliente solicitado");
//        }
//    }
//    public void validar(Long documento, String nombre, String apellido, String telefono) throws ErrorServicio {
//        
//        if (Long.toString(documento).length() != 14) {
//            throw new ErrorServicio("El documento no puede contener ni menos ni mas de 8 numeros");
//        }
//        if (nombre == null || nombre.isEmpty()) {
//            throw new ErrorServicio("El nombre no puede ser nulo");
//        }
//        if (apellido == null || apellido.isEmpty()) {
//            throw new ErrorServicio("El apellido no puede ser nulo");
//        }
//        if (telefono == null || telefono.isEmpty()) {
//            throw new ErrorServicio("El telefono no puede ser nulo");
//        }
//    }
}
