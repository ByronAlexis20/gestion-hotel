package ec.com.hotel.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ec.com.hotel.modelo.Perfil;
import ec.com.hotel.modelo.Permiso;
import ec.com.hotel.modelo.Usuario;
import ec.com.hotel.modelo.UsuarioDAO;

public class ServicioAutenticacion implements UserDetailsService {

	/**
	 * Este metodo es invocado en el momento de la autenticacion paraa recuperar 
	 * los datos del usuario.
	 * 
	 */
	@Override
	public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
		try {
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			Usuario usuario; 
			User usuarioSpring;
			List<GrantedAuthority> privilegios; 
			usuario = usuarioDAO.getUsuario(nombreUsuario);
			privilegios = obtienePrivilegios(usuario.getPerfil());
			
			usuarioSpring = new User(usuario.getUsuario(), usuario.getClave(), privilegios);

			return usuarioSpring;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
		
	}

	/**
	 * Elabora una lista de objetos del tipo GrantedAuthority en base a los permisos
	 * del usuario.
	 * 
	 * @param usuario
	 * @return
	 */
	private List<GrantedAuthority> obtienePrivilegios(Perfil tipoUsuario) {
		List<GrantedAuthority> listaPrivilegios = new ArrayList<GrantedAuthority>(); 
		
	    for(Permiso permiso  : tipoUsuario.getPermisos())
	    	listaPrivilegios.add(new SimpleGrantedAuthority(permiso.getPerfil().getDescripcion()));

		return listaPrivilegios;
	}

}
