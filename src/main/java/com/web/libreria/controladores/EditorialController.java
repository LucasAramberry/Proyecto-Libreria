package com.web.libreria.controladores;

import com.web.libreria.entidades.Editorial;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.servicios.EditorialServicio;
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
@RequestMapping("/editoriales")
public class EditorialController {
    
    @Autowired
    private EditorialServicio editorialServicio;
    
    @GetMapping("/registroEditorial")
    public String registroEditorial() {
        return "registroEditorial.html";
    }
    
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
    
    @GetMapping("/mostrarEditoriales")
    public String mostrarEditoriales(ModelMap modelo) {
        List<Editorial> listaEditoriales = editorialServicio.listarEditoriales();

        modelo.addAttribute("editoriales" , listaEditoriales);
        
        return "mostrarEditoriales.html";
    }
    
    @GetMapping("/modificar/{id}") //PATHVARIABLE
    public String modificar(@PathVariable String id, ModelMap modelo) {

        modelo.put("editorial", editorialServicio.getOne(id));

        return "modificarEditorial.html";
    }

    @PostMapping("/modificar/{id}")
    public String modificar(ModelMap modelo, @PathVariable String id, @RequestParam String nombre) {
        try {
            editorialServicio.modificarEditorial(id, nombre);
            return "redirect:/editoriales/mostrarEditoriales";
        } catch (Exception e) {

            return "modificarEditorial.html";
        }
    }

    @GetMapping("/baja/{id}")
    public String baja(@PathVariable String id) {

        try {
            editorialServicio.baja(id);
            return "redirect:/editoriales/mostrarEditoriales";
        } catch (Exception e) {
            return "redirect:/";
        }

    }

    @GetMapping("/alta/{id}")
    public String alta(@PathVariable String id) {

        try {
            editorialServicio.alta(id);
            return "redirect:/editoriales/mostrarEditoriales";
        } catch (Exception e) {
            return "redirect:/";
        }
    }
}
