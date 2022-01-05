package com.web.libreria.controladores;

import com.web.libreria.entidades.Autor;
import com.web.libreria.entidades.Editorial;
import com.web.libreria.entidades.Libro;
import com.web.libreria.entidades.Usuario;
import com.web.libreria.repositorios.LibroRepositorio;
import com.web.libreria.servicios.AutorServicio;
import com.web.libreria.servicios.EditorialServicio;
import com.web.libreria.servicios.LibroServicio;
import com.web.libreria.servicios.PrestamoServicio;
import com.web.libreria.servicios.UsuarioServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador principal
 *
 * @author Lucas Aramberry / aramberrylucas@gmail.com
 */
@Controller
@RequestMapping("/")
public class PortalController {

    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private EditorialServicio editorialServicio;
    @Autowired
    private PrestamoServicio prestamoServicio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/libros")
    public String libros(ModelMap modelo) {

        List<Libro> listaLibros = libroServicio.listarLibros();
        modelo.addAttribute("libros", listaLibros);

        return "libros.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/autores")
    public String autores(ModelMap modelo, String idAutor) {

        List<Autor> listaActivos = autorServicio.listarActivos();
        modelo.addAttribute("autoresA", listaActivos);
        List<Autor> autores = autorServicio.listarAutores();
        modelo.addAttribute("autores", autores);
        modelo.addAttribute("autorSelected", null);
        List<Libro> libros = libroServicio.buscarPorAutor(idAutor);
        modelo.addAttribute("libros", libros);

        return "autores.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/editoriales")
    public String editoriales(ModelMap modelo, String idEditorial) {

        List<Editorial> listaEditoriales = editorialServicio.listarEditoriales();
        modelo.addAttribute("editoriales", listaEditoriales);

        List<Editorial> listaActivos = editorialServicio.listarEditorialesActivas();
        modelo.addAttribute("editorialesA", listaActivos);
        modelo.addAttribute("editorialSelected", null);
        List<Libro> libros = libroServicio.buscarPorEditorial(idEditorial);
        modelo.addAttribute("libros", libros);

        return "editoriales.html";
    }

    @GetMapping("/admin-usuarios")
    public String administrarUsuario(ModelMap modelo) {
        
        List<Usuario> listaUsuarios = usuarioServicio.findAll();
        modelo.addAttribute("usuarios", listaUsuarios);
        
        return "admin-usuarios.html";
    }

    @GetMapping("/")
    public String login(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap model) {

        if (error != null) {
            model.put("error", "Nombre de usuario o clave incorrectos.");
        }
        if (logout != null) {
            model.put("logout", "Ha salido correctamente de la plataforma.");
        }
        return "index.html";
    }

    @GetMapping("/login")
    public String iniciarSesion(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap model) {
        if (error != null) {
            model.put("error", "Nombre de usuario o clave incorrectos.");
        }
        if (logout != null) {
            model.put("logout", "Ha salido correctamente de la plataforma.");
        }
        return "login.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/inicio")
    public String inicio(ModelMap modelo) {

        List<Libro> libros = libroRepositorio.findAll();
        modelo.put("libros", libros);

        return "inicio.html";
    }

}
