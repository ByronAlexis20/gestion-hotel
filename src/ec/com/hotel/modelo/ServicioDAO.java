package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class ServicioDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Servicio> buscarActivos() {
		List<Servicio> resultado = new ArrayList<Servicio>(); 
		Query query = getEntityManager().createNamedQuery("Servicio.buscarActivos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Servicio>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Servicio> buscarPorId(Integer id) {
		List<Servicio> resultado = new ArrayList<Servicio>(); 
		Query query = getEntityManager().createNamedQuery("Servicio.buscarPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("id", id);
		resultado = (List<Servicio>) query.getResultList();
		return resultado;
	}
}
