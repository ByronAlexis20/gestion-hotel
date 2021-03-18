package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

public class ComprobanteDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Comprobante> buscarPorFecha(Date fecha) {
		List<Comprobante> resultado = new ArrayList<Comprobante>(); 
		Query query = getEntityManager().createNamedQuery("Comprobante.buscarPorFecha");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("fecha", fecha);
		resultado = (List<Comprobante>) query.getResultList();
		return resultado;
	} 
}
