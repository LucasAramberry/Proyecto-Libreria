package com.web.libreria.controladores;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class PortalController {

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/autores")
    public String autores() {
        return "autores.html";
    }

    @GetMapping("/libros")
    public String libros() {
        return "libros.html";
    }

    @GetMapping("/editoriales")
    public String editoriales() {
        return "editoriales.html";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap model) {
        if (error == null) {
            model.put("error", "Nombre de usuario o clave incorrectos.");
        }
        if (logout != null) {
            model.put("logout", "Ha salido correctamente de la plataforma.");
        }
        return "login.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/inicio")
    public String inicio() {
        return "inicio.html";
    }
}
