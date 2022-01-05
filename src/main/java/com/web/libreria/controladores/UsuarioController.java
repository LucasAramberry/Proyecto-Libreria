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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador para gestionar la entidad Usuario en donde se realizan las
 * funciones de registrar, modificar o dar de alta/baja
 *
 * @author Lucas Aramberry / aramberrylucas@gmail.com
 */
@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private ZonaRepositorio zonaRepositorio;
    @Autowired
    private LibroRepositorio libroRepositorio;

    /**
     * Controlador para registrarse
     *
     * @param modelo
     * @return
     */
    @GetMapping("/registro")
    public String registrar(ModelMap modelo) {
        List<Zona> zonas = zonaRepositorio.findAll();
        modelo.put("zonas", zonas);
        return "registroUsuario.html";
    }

    /**
     * Controlador para relizar el registro del usuario
     *
     * @param modelo
     * @param archivo
     * @param nombre
     * @param apellido
     * @param mail
     * @param clave
     * @param clave2
     * @param documento
     * @param telefono
     * @param idZona
     * @return
     */
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

    /**
     * Controlador para ingresar a editar el perfil
     *
     * @param session
     * @param id
     * @param model
     * @return
     */
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

    /**
     * Controlador para realizar un cambio en el perfil
     *
     * @param modelo
     * @param session
     * @param archivo
     * @param id
     * @param nombre
     * @param apellido
     * @param mail
     * @param clave
     * @param clave2
     * @param documento
     * @param telefono
     * @param idZona
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @PostMapping("/actualizar-perfil")
    public String actualizarPerfil(ModelMap modelo, HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail, @RequestParam String clave, @RequestParam String clave2, @RequestParam String documento, @RequestParam String telefono, @RequestParam String idZona) {

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

    /**
     * Controlador para dar de alta un usuario
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/baja/{id}")
    public String baja(@PathVariable String id) {

        try {
            usuarioServicio.baja(id);
            return "redirect:/admin-usuarios";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    /**
     * Controlador para dar de baja un usuario
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/alta/{id}")
    public String alta(@PathVariable String id) {

        try {
            usuarioServicio.alta(id);
            return "redirect:/admin-usuarios";
        } catch (Exception e) {
            return "redirect:/";
        }
    }
}
