package com.web.libreria.controladores;

import com.web.libreria.entidades.Editorial;
import com.web.libreria.entidades.Libro;
import com.web.libreria.errores.ErrorServicio;
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
 * Controlador para gestionar la entidad editorial en donde se realizan las
 * funciones de registrar, modificar, eliminar o dar de alta/baja
 *
 * @author Lucas Aramberry / aramberrylucas@gmail.com
 */
@Controller
@RequestMapping("/editoriales")
public class EditorialController {

    @Autowired
    private EditorialServicio editorialServicio;
    @Autowired
    private LibroServicio libroServicio;

    /**
     * Funcion que muestra las editoriales para filtrar libro por editorial en
     * la vista /editoriales
     *
     * @param model
     * @param idEditorial
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/editoriales")
    public String autores(ModelMap model, String idEditorial) {
        List<Editorial> listaActivos = editorialServicio.listarEditorialesActivas();
        model.addAttribute("editorialesA", listaActivos);
        model.addAttribute("autorSelected", editorialServicio.getById(idEditorial));
        List<Libro> libros = libroServicio.buscarPorAutor(idEditorial);
        model.addAttribute("libros", libros);
        return "redirect:/editoriales";
    }

    /**
     * Funcion para ingresar al registro de editorial
     *
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registroEditorial")
    public String registroEditorial() {
        return "registroEditorial.html";
    }

    /**
     * Funcion para realizar el registro de una editorial enviando los datos
     * ingresados en el form al servicio
     *
     * @param modelo
     * @param nombreEditorial
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registroEditorial")
    public String guardar(ModelMap modelo, @RequestParam String nombreEditorial) {
        try {
            editorialServicio.ingresarEditorial(nombreEditorial);

//            modelo.put("exito", "Registro exitoso");
//            return "registroEditorial.html";
        } catch (ErrorServicio ex) {
//            modelo.put("error", "Falto algun dato");

            return "registroEditorial.html";
        }
        modelo.put("titulo", "Bienvenido a la Libreria.");
        modelo.put("descripcion", "La editorial fue registrada de manera satisfactoria.");
        return "exito.html";
    }

    /**
     * Funcion para ingresar a la vista para modificar la editorial seleccionada
     * por id
     *
     * @param id
     * @param modelo
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar/{id}") //PATHVARIABLE
    public String modificar(@PathVariable String id, ModelMap modelo) {

        modelo.put("editorial", editorialServicio.getOne(id));

        return "modificarEditorial.html";
    }

    /**
     * Funcion para realizar la modificacion de la editorial seleccionada
     * enviando los datos cargados al servicio
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
            editorialServicio.modificarEditorial(id, nombre);
            return "redirect:/editoriales";
        } catch (Exception e) {

            return "modificarEditorial.html";
        }
    }

    /**
     * Funcion para da de baja una editorial
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/baja/{id}")
    public String baja(@PathVariable String id) {

        try {
            editorialServicio.baja(id);
            return "redirect:/editoriales";
        } catch (Exception e) {
            return "redirect:/";
        }

    }

    /**
     * Funcion para dar de alta una editorial
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/alta/{id}")
    public String alta(@PathVariable String id) {

        try {
            editorialServicio.alta(id);
            return "redirect:/editoriales";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    /**
     * Funcion para eliminar una editorial
     * @param id
     * @return 
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {

        try {
            editorialServicio.delete(id);
            return "redirect:/editoriales";
        } catch (Exception e) {
            return "redirect:/autores";
        }
    }
}
