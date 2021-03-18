package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

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
	
	@SuppressWarnings("unchecked")
	public List<Usuario> getListaUsuariosBuscar(String value) {
		List<Usuario> resultado = new ArrayList<Usuario>(); 
		Query query = getEntityManager().createNamedQuery("Usuario.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Usuario>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> getValidarUsuarioExistente(String cedulaUsuario) {
		List<Usuario> resultado; 
		Query query = getEntityManager().createNamedQuery("Usuario.buscarPorCedula");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("cedula", cedulaUsuario);
		resultado = (List<Usuario>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Usuario> getValidarUsuarioExistenteDiferente(String cedulaUsuario,Integer idUsuario) {
		List<Usuario> resultado; 
		Query query = getEntityManager().createNamedQuery("Usuario.buscarPorCedulaDiferenteAlUsuarioActual");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("cedula", cedulaUsuario);
		query.setParameter("idUsuario", idUsuario);
		resultado = (List<Usuario>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> getBuscarUsuario(String value,Integer idUsuario) {
		List<Usuario> resultado = new ArrayList<Usuario>(); 
		Query query = getEntityManager().createNamedQuery("Usuario.buscarPorUsuario");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron", value.toLowerCase());
		query.setParameter("idUsuario", idUsuario);
		resultado = (List<Usuario>) query.getResultList();
		return resultado;
	}
	
	public Usuario getBuscarUsuarioPorCedula(String cedula) {
		Usuario resultado; 
		Query query = getEntityManager().createNamedQuery("Usuario.buscarUsuarioPorCedula");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("cedula", cedula);
		resultado = (Usuario) query.getSingleResult();
		return resultado;
	}
}
