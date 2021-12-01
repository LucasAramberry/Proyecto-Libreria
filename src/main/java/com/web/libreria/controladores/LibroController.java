package com.web.libreria.controladores;

import com.web.libreria.entidades.Libro;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.servicios.AutorServicio;
import com.web.libreria.servicios.EditorialServicio;
import com.web.libreria.servicios.LibroServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private EditorialServicio editorialServicio;

    @GetMapping("/registro")
    public String registroLibro() {
        return "registroLibro.html";
    }

    @PostMapping("/registro")
    public String registroLibro(ModelMap modelo, @RequestParam Integer isbn, @RequestParam String titulo, @RequestParam Integer anio, @RequestParam Integer ejemplares, @RequestParam Integer ejemplaresPrestados, @RequestParam Integer ejemplaresRestantes, @RequestParam String nombreAutor, @RequestParam String nombreEditorial) {
        try {
            libroServicio.agregarLibro(isbn, titulo, anio, ejemplares, ejemplaresPrestados, ejemplaresRestantes, nombreAutor, nombreEditorial);

//            modelo.put("exito", "Registro exitoso");
//            return "registroLibro";
        } catch (ErrorServicio ex) {

            modelo.put("error", "Falto algun dato");
            modelo.put("error", ex.getMessage());
            modelo.put("isbn", isbn);
            modelo.put("titulo", titulo);
            modelo.put("anio", anio);
            modelo.put("ejemplares", ejemplares);
            modelo.put("ejPrestados", ejemplaresPrestados);
            modelo.put("ejRestantes", ejemplaresRestantes);
            modelo.put("nombreAutor", nombreAutor);
            modelo.put("nombreEditorial", nombreEditorial);
            return "registroLibro";
        }

        modelo.put("titulo", "Bienvenido a la Libreria.");
        modelo.put("descripcion", "Tu libro fue registrado de manera satisfactoria.");
        return "exito.html";
    }

    @GetMapping("/mostrarLibros")
    public String mostrarLibros(ModelMap modelo) {
        List<Libro> listaLibros = libroServicio.listarLibros();

        modelo.addAttribute("libros", listaLibros);

        return "mostrarLibros.html";
    }

    @GetMapping("/baja/{id}")
    public String baja(@PathVariable String id) {

        try {
            libroServicio.baja(id);
            return "redirect:/libros/mostrarLibros";
        } catch (Exception e) {
            return "redirect:/";
        }

    }

    @GetMapping("/alta/{id}")
    public String alta(@PathVariable String id) {

        try {
            libroServicio.alta(id);
            return "redirect:/libros/mostrarLibros";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("/modificar/{id}") //PATHVARIABLE
    public String modificar(@PathVariable String id, ModelMap modelo) {

        modelo.put("libro", libroServicio.getOne(id));
        
        return "modificarLibro.html";

    }

    @PostMapping("/modificar/{id}")
    public String modificar(ModelMap modelo, @PathVariable String id, @RequestParam Integer isbn, @RequestParam String titulo, @RequestParam Integer anio, @RequestParam Integer ejemplares, @RequestParam Integer ejemplaresPrestados, @RequestParam Integer ejemplaresRestantes, @RequestParam String nombreAutor, @RequestParam String nombreEditorial) {

        try {
            libroServicio.modificarLibro(id, titulo, isbn, anio, ejemplares, ejemplaresPrestados, ejemplaresRestantes, nombreAutor, nombreEditorial);
            return "redirect:/libros/mostrarLibros";

        } catch (Exception e) {

            return "modificarLibro.html";
        }
    }
}
