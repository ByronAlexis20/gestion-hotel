package ec.com.hotel.modelo;

import javax.persistence.Query;

public class UsuarioDAO extends ClaseDAO{
	public Usuario getUsuario(String nombreUsuario) {
		Usuario usuario; 
		Query consulta;
		consulta = getEntityManager().createNamedQuery("Usuario.buscaUsuario");
		consulta.setParameter("nombreUsuario", nombreUsuario);
		usuario = (Usuario) consulta.getSingleResult();
		return usuario;
	}
}
