package com.web.libreria.controladores;

import com.web.libreria.entidades.Libro;
import com.web.libreria.entidades.Prestamo;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.repositorios.LibroRepositorio;
import com.web.libreria.servicios.PrestamoServicio;
import com.web.libreria.servicios.UsuarioServicio;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador para gestionar la entidad Prestamo en donde se realizan las
 * funciones de realizar prestamos
 *
 * @author Lucas Aramberry / aramberrylucas@gmail.com
 */
@PreAuthorize("hasAnyRole('ROLE_USUARIO')")
@Controller
@RequestMapping("/prestamo")
public class PrestamoController {

    @Autowired
    private PrestamoServicio prestamoServicio;
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    /**
     * Funcion para ingresar a la vista de registrar prestamo
     *
     * @param modelo
     * @return
     */
    @GetMapping("/registrar")
    public String prestamo(ModelMap modelo) {
        List<Libro> libros = libroRepositorio.findAll();
        modelo.put("libros", libros);
        return "prestamo.html";
    }

    /**
     * Funcion para realizar el registro del prestamo enviando los datos
     * ingresados a la BD
     *
     * @param modelo
     * @param fechaPrestamo
     * @param fechaDevolucion
     * @param idLibro
     * @param idUsuario
     * @return
     * @throws Exception
     */
    @PostMapping("/registrar")
    public String prestamo(ModelMap modelo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, @RequestParam String idLibro, @RequestParam String idUsuario) throws Exception {

        try {
            prestamoServicio.agregarPrestamo(fechaPrestamo, fechaDevolucion, idLibro, idUsuario);
        } catch (ErrorServicio ex) {
            List<Libro> libros = libroRepositorio.findAll();
            modelo.put("libros", libros);
            modelo.put("idUsuario", idUsuario);
            modelo.put("error", ex.getMessage());
            return "prestamo.html";
        }
        modelo.put("titulo", "Bienvenido a Tinder de Mascotas.");
        modelo.put("descripcion", "Tu prestamo fue registrado de manera satisfactoria.");
        return "exito.html";
    }

    /**
     * Funcion para ver los prestamos que realizo un usuario
     *
     * @param idUsuario
     * @param modelo
     * @return
     */
    @GetMapping("/mis-prestamos/{idUsuario}")
    public String mostrarPrestamos(@PathVariable String idUsuario, ModelMap modelo) {
        List<Prestamo> listaPrestamos = prestamoServicio.listaPrestamosUsuario(idUsuario);
        modelo.put("prestamos", listaPrestamos);

        return "mostrarPrestamos.html";
    }
    
    /**
     * Controlador para eliminar un prestamo
     * @param id
     * @return 
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {

        try {
            prestamoServicio.delete(id);
            return "redirect:/inicio";
        } catch (Exception e) {
            return "redirect:/inicio";
        }
    }
}
