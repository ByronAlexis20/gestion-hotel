package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class ClienteDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Cliente> getListaClientesBuscar(String value) {
		List<Cliente> resultado = new ArrayList<Cliente>(); 
		Query query = getEntityManager().createNamedQuery("Cliente.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Cliente>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Cliente> getListaClientesBuscarCedula(String cedula) {
		List<Cliente> resultado = new ArrayList<Cliente>(); 
		Query query = getEntityManager().createNamedQuery("Cliente.buscarPorCedula");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("cedula",cedula);
		resultado = (List<Cliente>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Cliente> getValidarClienteExistente(String cedulaUsuario) {
		List<Cliente> resultado; 
		Query query = getEntityManager().createNamedQuery("Cliente.buscarPorCedula");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("cedula", cedulaUsuario);
		resultado = (List<Cliente>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Cliente> getValidarClienteExistenteDiferente(String cedulaUsuario,Integer idCliente) {
		List<Cliente> resultado; 
		Query query = getEntityManager().createNamedQuery("Cliente.buscarPorCedulaDiferenteAlUsuarioActual");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("cedula", cedulaUsuario);
		query.setParameter("idCliente", idCliente);
		resultado = (List<Cliente>) query.getResultList();
		return resultado;
	}
}
