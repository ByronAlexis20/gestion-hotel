package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class AdicionalDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Adicional> buscarPorReserva(Integer id) {
		List<Adicional> resultado = new ArrayList<Adicional>(); 
		Query query = getEntityManager().createNamedQuery("Adicional.buscarPorReserva");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("id",id);
		resultado = (List<Adicional>) query.getResultList();
		return resultado;
	}
}
