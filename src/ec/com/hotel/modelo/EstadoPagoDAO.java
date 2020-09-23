package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class EstadoPagoDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<EstadoPago> getPagoPorId(Integer id) {
		List<EstadoPago> resultado = new ArrayList<EstadoPago>(); 
		Query query = getEntityManager().createNamedQuery("EstadoPago.buscarPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("id",id);
		resultado = (List<EstadoPago>) query.getResultList();
		return resultado;
	}
}
