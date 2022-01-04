package com.web.libreria.controladores;

import com.web.libreria.entidades.Usuario;
import com.web.libreria.entidades.Zona;
import com.web.libreria.errores.ErrorServicio;
import com.web.libreria.repositorios.LibroRepositorio;
import com.web.libreria.repositorios.ZonaRepositorio;
import com.web.libreria.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private ZonaRepositorio zonaRepositorio;
    @Autowired
    private LibroRepositorio libroRepositorio;

    @GetMapping("/registro")
    public String registrar(ModelMap modelo) {
        List<Zona> zonas = zonaRepositorio.findAll();
        modelo.put("zonas", zonas);
        return "registroUsuario.html";
    }

    @PostMapping("/registro")
    public String registro(ModelMap modelo, MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail, @RequestParam String clave, @RequestParam String clave2, @RequestParam String documento, @RequestParam String telefono, @RequestParam String idZona) {
        try {
            usuarioServicio.registrarUsuario(archivo, nombre, apellido, mail, clave, clave2, documento, telefono, idZona);
        } catch (ErrorServicio ex) {
            List<Zona> zonas = zonaRepositorio.findAll();
            modelo.put("zonas", zonas);
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("mail", mail);
            modelo.put("clave", clave);
            modelo.put("clave2", clave2);
            modelo.put("documento", documento);
            modelo.put("telefono", telefono);

            return "registroUsuario.html";
        }
        modelo.put("titulo", "Bienvenido a Tinder de Mascotas.");
        modelo.put("descripcion", "Tu usuario fue registrado de manera satisfactoria.");
        return "exito.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam String id, ModelMap model) {

        List<Zona> zonas = zonaRepositorio.findAll();
        model.put("zonas", zonas);
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        try {
            Usuario usuario = usuarioServicio.buscarPorId(id);
            model.addAttribute("usuario", usuario);
        } catch (ErrorServicio e) {
            model.addAttribute("error", e.getMessage());
        }
        return "modificarUsuario.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @PostMapping("/actualizar-perfil")
    public String registrar(ModelMap modelo, HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail, @RequestParam String clave, @RequestParam String clave2, @RequestParam String documento, @RequestParam String telefono, @RequestParam String idZona) {

        Usuario usuario = null;
        try {

            Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }

            usuario = usuarioServicio.buscarPorId(id);
            usuarioServicio.modificarUsuario(archivo, id, nombre, apellido, mail, clave, clave2, documento, telefono, idZona);
            session.setAttribute("usuariosession", usuario);

            return "redirect:/inicio";
        } catch (ErrorServicio ex) {
            List<Zona> zonas = zonaRepositorio.findAll();
            modelo.put("zonas", zonas);
            modelo.put("error", ex.getMessage());
            modelo.put("usuario", usuario);

            return "modificarUsuario.html";
        }
    }
}
