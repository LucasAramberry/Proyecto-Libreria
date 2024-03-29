package com.web.libreria.servicios;

import com.web.libreria.entidades.Foto;
import com.web.libreria.entidades.Usuario;
import com.web.libreria.entidades.Zona;
import com.web.libreria.enums.Rol;
import com.web.libreria.errores.ErrorServicio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.web.libreria.repositorios.UsuarioRepositorio;
import com.web.libreria.repositorios.ZonaRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private FotoServicio fotoServicio;
    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void registrarUsuario(MultipartFile archivo, String nombre, String apellido, String mail, String clave, String clave2, String documento, String telefono, String idZona) throws ErrorServicio {

        Zona zona = zonaRepositorio.getById(idZona);
        validar(nombre, apellido, mail, clave, clave2, documento, telefono, zona);
        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);
        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);
        usuario.setDocumento(documento);
        usuario.setTelefono(telefono);
        usuario.setRol(Rol.USUARIO);
        usuario.setAlta(new Date());
        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);
        usuario.setZona(zona);

        usuarioRepositorio.save(usuario);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void modificarUsuario(MultipartFile archivo, String id, String nombre, String apellido, String mail, String clave, String clave2, String documento, String telefono, String idZona) throws ErrorServicio {

        Zona zona = zonaRepositorio.getOne(idZona);
        validar(nombre, apellido, mail, clave, clave2, documento, telefono, zona);
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setMail(mail);
            String encriptada = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptada);
            usuario.setDocumento(documento);
            usuario.setTelefono(telefono);
            Foto foto = fotoServicio.guardar(archivo);
            usuario.setFoto(foto);
            usuario.setZona(zona);

            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }

    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(new Date());
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }

    @Transactional
    public void habilitar(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            if (usuario.getBaja() == null) {
                throw new ErrorServicio("El usuario no se encuentra dado de baja.");
            } else {
                usuario.setBaja(null); // Borramos la fecha de baja
                usuarioRepositorio.save(usuario);
            }
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }

    public void validar(String nombre, String apellido, String mail, String clave, String clave2, String documento, String telefono, Zona zona) throws ErrorServicio {

        if (documento == null || documento.isEmpty() || documento.length() != 8) {
            throw new ErrorServicio("El documento no puede ser nulo");
        }
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre no puede ser nulo");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido no puede ser nulo");
        }
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("El mail no puede ser nulo");
        }
        if (clave == null || clave.isEmpty() || clave.length() <= 6) {
            throw new ErrorServicio("La clave del usuario no puede ser nula y tiene que tener mas de seis digitos");
        }
        if (!clave.equals(clave2)) {
            throw new ErrorServicio("Las claves deben ser iguales");
        }
        if (telefono == null || telefono.isEmpty()) {
            throw new ErrorServicio("El telefono no puede ser nulo");
        }
        if (zona == null) {
            throw new ErrorServicio("No se encontró la zona solicitada");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList<>();

            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + usuario.getRol());
            permisos.add(p1);

            //Esto me permite guardar el OBJETO USUARIO LOG, para luego ser utilizado
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            User user = new User(usuario.getMail(), usuario.getClave(), permisos);
            return user;

        } else {
            return null;
        }
    }

    /**
     * Metodo para dar de alta un usuario
     *
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Usuario alta(String id) {

        Usuario entidad = usuarioRepositorio.getOne(id);
        entidad.setAlta(new Date());
        entidad.setBaja(null);

        return usuarioRepositorio.save(entidad);
    }

    /**
     * Metodo para dar de baja un usuario
     *
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Usuario baja(String id) {

        Usuario entidad = usuarioRepositorio.getOne(id);
        entidad.setBaja(new Date());
        entidad.setAlta(null);

        return usuarioRepositorio.save(entidad);
    }

    //*****************************METODOS REPOSITORIO*********************************
    @Transactional(readOnly = true)
    public Usuario buscarPorId(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            return usuario;
        } else {
            throw new ErrorServicio("No se encontró el usuario solicitado");
        }
    }

    @Transactional(readOnly = true)
    public Usuario getById(String id) {
        return usuarioRepositorio.getById(id);
    }

    @Transactional(readOnly = true)
    public Usuario getOne(String id) {
        return usuarioRepositorio.getOne(id);
    }

    public List<Usuario> findAll() {
        return usuarioRepositorio.findAll();
    }

    public List<Usuario> buscarActivos() {
        return usuarioRepositorio.buscarActivos();
    }

    public List<Usuario> buscarInactivos() {
        return usuarioRepositorio.buscarInactivos();
    }
}
