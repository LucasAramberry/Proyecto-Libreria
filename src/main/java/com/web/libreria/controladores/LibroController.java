package com.web.libreria.controladores;

import com.web.libreria.entidades.Autor;
import com.web.libreria.entidades.Editorial;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador para gestionar la entidad Libro en donde se realizan las
 * funciones de registrar, modificar, eliminar o dar de alta/baja
 *
 * @author Lucas Aramberry / aramberrylucas@gmail.com
 */
@Controller
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private EditorialServicio editorialServicio;

    /**
     * Controlador para traer el registro de un libro
     *
     * @param modelo
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registro")
    public String registroLibro(ModelMap modelo) {
        List<Autor> listaAutores = autorServicio.listarAutores();
        modelo.addAttribute("autores", listaAutores);
        List<Editorial> listaEditoriales = editorialServicio.listarEditoriales();
        modelo.addAttribute("editoriales", listaEditoriales);
        return "registroLibro.html";
    }

    /**
     * Controlador para enviar los datos del libro registrado a la DB
     *
     * @param modelo
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
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registro")
    public String registroLibro(ModelMap modelo, MultipartFile archivo, @RequestParam String isbn, @RequestParam String titulo, @RequestParam String descripcion, @RequestParam Integer anio, @RequestParam Integer ejemplares, String idAutor, String nombreAutor, String idEditorial, String nombreEditorial) {
        try {
            libroServicio.agregarLibro(archivo, isbn, titulo, descripcion, anio, ejemplares, idAutor, nombreAutor, idEditorial, nombreEditorial);

//            modelo.put("exito", "Registro exitoso");
//            return "registroLibro";
        } catch (ErrorServicio ex) {

            modelo.put("error", "Falto algun dato");
            modelo.put("error", ex.getMessage());
            modelo.put("isbn", isbn);
            modelo.put("titulo", titulo);
            modelo.put("descripcion", descripcion);
            modelo.put("anio", anio);
            modelo.put("ejemplares", ejemplares);
            modelo.put("nombreAutor", nombreAutor);
            modelo.put("nombreEditorial", nombreEditorial);
            return "registroLibro";
        }

        modelo.put("titulo", "Bienvenido a la Libreria.");
        modelo.put("descripcion", "Tu libro fue registrado de manera satisfactoria.");
        return "exito.html";
    }

    /**
     * Controlador para modificar un libro
     *
     * @param id
     * @param modelo
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar/{id}") //PATHVARIABLE
    public String modificar(@PathVariable String id, ModelMap modelo) {

        modelo.put("libro", libroServicio.getOne(id));
        List<Autor> listaAutores = autorServicio.listarAutores();
        modelo.addAttribute("autores", listaAutores);
        List<Editorial> listaEditoriales = editorialServicio.listarEditoriales();
        modelo.addAttribute("editoriales", listaEditoriales);

        return "modificarLibro.html";

    }

    /**
     * Controlador para completar la modificacion del libro
     *
     * @param modelo
     * @param archivo
     * @param id
     * @param isbn
     * @param titulo
     * @param descripcion
     * @param anio
     * @param ejemplares
     * @param idAutor
     * @param nombreAutor
     * @param idEditorial
     * @param nombreEditorial
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modificar/{id}")
    public String modificar(ModelMap modelo, MultipartFile archivo, @PathVariable String id, @RequestParam String isbn, @RequestParam String titulo, @RequestParam String descripcion, @RequestParam Integer anio, @RequestParam Integer ejemplares, String idAutor, String nombreAutor, String idEditorial, @RequestParam(required = false) String nombreEditorial) {

        try {
            libroServicio.modificarLibro(archivo, id, titulo, descripcion, isbn, anio, ejemplares, idAutor, nombreAutor, idEditorial, nombreEditorial);
            return "redirect:/libros";

        } catch (Exception e) {

            return "modificarLibro.html";
        }
    }

    /**
     * Controlador para mostrar informacion de un libro
     *
     * @param id
     * @param modelo
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/mostrarLibro/{id}")
    public String mostrarLibros(@PathVariable String id, ModelMap modelo) {

        modelo.put("libro", libroServicio.getOne(id));

        return "mostrarLibro.html";
    }

    /**
     * Controlador para dar de baja un libro
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/baja/{id}")
    public String baja(@PathVariable String id) {
        try {
            libroServicio.baja(id);
            return "redirect:/libros";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    /**
     * Controlador para dar de alta un libro
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/alta/{id}")
    public String alta(@PathVariable String id) {
        try {
            libroServicio.alta(id);
            return "redirect:/libros";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    /**
     * Controlador para eliminar un libro
     * @param id
     * @return 
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {

        try {
            libroServicio.delete(id);
            return "redirect:/libros";
        } catch (Exception e) {
            return "redirect:/libros";
        }
    }
}
