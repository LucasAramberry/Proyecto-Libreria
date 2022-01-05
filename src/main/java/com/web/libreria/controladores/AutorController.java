package com.web.libreria.controladores;

import com.web.libreria.entidades.Autor;
import com.web.libreria.entidades.Libro;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.servicios.AutorServicio;
import com.web.libreria.servicios.EditorialServicio;
import com.web.libreria.servicios.LibroServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador para gestionar la entidad autor en donde se realizan las
 * funciones de registrar, modificar, eliminar o dar de alta/baja
 *
 * @author Lucas Aramberry / aramberrylucas@gmail.com
 */
@Controller
@RequestMapping("/autores")
public class AutorController {

    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private EditorialServicio editorialServicio;

    /**
     * Funcion que muestra los libros filtrados por autor en la vista /autores
     *
     * @param model
     * @param idAutor
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/autores")
    public String autores(ModelMap model, String idAutor) {
        List<Autor> listaActivos = autorServicio.listarActivos();
        model.addAttribute("autoresA", listaActivos);
        model.addAttribute("autorSelected", autorServicio.getById(idAutor));
//        List<Autor> autores = autorServicio.listarAutores();
//        model.addAttribute("autores", autores);
        List<Libro> libros = libroServicio.buscarPorAutor(idAutor);
        model.addAttribute("libros", libros);
        return "redirect:/autores";
    }

    /**
     * Funcion para ingresar a la vista de registro de autor
     *
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registroAutor")
    public String registroAutor() {
        return "registroAutor.html";
    }

    /**
     * Funcion para registrar un autor, se envian los datos ingresados al
     * servicio quien se encarga de ingresar el autor a la DB y si sale bien
     * retorna la vista exito y si hay un error retorna la vista error
     *
     * @param modelo
     * @param nombreAutor
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registroAutor")
    public String guardar(ModelMap modelo, @RequestParam String nombreAutor) {
        try {
            autorServicio.ingresarAutor(nombreAutor);

        } catch (ErrorServicio ex) {
//            modelo.put("error", "Falto algun dato");

            return "registroAutor.html";
        }
        modelo.put("titulo", "Bienvenido a la Libreria.");
        modelo.put("descripcion", "El autor fue registrado de manera satisfactoria.");
        return "exito.html";
    }

    /**
     * Funcion para ingresar a la vista de modificacion del autor seleccionado
     *
     * @param id
     * @param modelo
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar/{id}") //PATHVARIABLE
    public String modificar(@PathVariable String id, ModelMap modelo) {

        modelo.put("autor", autorServicio.getOne(id));

        return "modificarAutor.html";
    }

    /**
     * Funcion para realizar la modificacion del autor seleccionado enviando los
     * datos ingresados al servicio
     *
     * @param modelo
     * @param id
     * @param nombre
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modificar/{id}")
    public String modificar(ModelMap modelo, @PathVariable String id, @RequestParam String nombre) {
        try {
            autorServicio.modificarAutor(id, nombre);
            return "redirect:/autores";
        } catch (Exception e) {

            return "modificarAutor.html";
        }
    }

    /**
     * Funcion para dar de baja autor seleccionado mediante ID
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/baja/{id}")
    public String baja(@PathVariable String id) {

        try {
            autorServicio.baja(id);
            return "redirect:/autores";
        } catch (Exception e) {
            return "redirect:/";
        }

    }

    /**
     * Controlador para dar de alta autor seleccionado mediante ID
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/alta/{id}")
    public String alta(@PathVariable String id) {

        try {
            autorServicio.alta(id);
            return "redirect:/autores";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    /**
     * Controlador para eliminar autor seleccionado mediante ID
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {

        try {
            autorServicio.delete(id);
            return "redirect:/autores";
        } catch (Exception e) {
            return "redirect:/autores";
        }
    }

}
