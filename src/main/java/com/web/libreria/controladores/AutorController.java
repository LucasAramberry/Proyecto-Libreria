package com.web.libreria.controladores;

import com.web.libreria.entidades.Autor;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.servicios.AutorServicio;
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
@RequestMapping("/autores")
public class AutorController {

    @Autowired
    private AutorServicio autorServicio;

    @GetMapping("/registroAutor")
    public String registroAutor() {
        return "registroAutor.html";
    }

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

    @GetMapping("/mostrarAutores")
    public String mostrarAutores(ModelMap modelo) {
        List<Autor> listaAutores = autorServicio.listarAutores();

        modelo.addAttribute("autores", listaAutores);

        return "mostrarAutores.html";
    }

    @GetMapping("/modificar/{id}") //PATHVARIABLE
    public String modificar(@PathVariable String id, ModelMap modelo) {

        modelo.put("autor", autorServicio.getOne(id));

        return "modificarAutor.html";
    }

    @PostMapping("/modificar/{id}")
    public String modificar(ModelMap modelo, @PathVariable String id, @RequestParam String nombre) {
        try {
            autorServicio.modificarAutor(id, nombre);
            return "redirect:/autores/mostrarAutores";
        } catch (Exception e) {

            return "modificarAutor.html";
        }
    }

    @GetMapping("/baja/{id}")
    public String baja(@PathVariable String id) {

        try {
            autorServicio.baja(id);
            return "redirect:/autores/mostrarAutores";
        } catch (Exception e) {
            return "redirect:/";
        }

    }

    @GetMapping("/alta/{id}")
    public String alta(@PathVariable String id) {

        try {
            autorServicio.alta(id);
            return "redirect:/autores/mostrarAutores";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

}
