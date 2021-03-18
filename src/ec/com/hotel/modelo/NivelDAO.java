package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


public class NivelDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Nivel> getListaNivelesActivos() {
		List<Nivel> retorno = new ArrayList<Nivel>();
		Query query = getEntityManager().createNamedQuery("Nivel.bucarActivos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		retorno = (List<Nivel>) query.getResultList();
		return retorno;
	}
	@SuppressWarnings("unchecked")
	public List<Nivel> getNiveles(String value) {
		List<Nivel> resultado; 
		String patron = value;

		if (value == null || value.length() == 0) {
			patron = "%";
		}else{
			patron = "%" + patron.toLowerCase() + "%";
		}
		Query query = getEntityManager().createNamedQuery("Nivel.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron", patron);
		resultado = (List<Nivel>) query.getResultList();
		return resultado;
	}
}
