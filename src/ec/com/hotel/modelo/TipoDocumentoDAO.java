package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class TipoDocumentoDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<TipoDocumento> getListaTiposDocumentosActivos() {
		List<TipoDocumento> retorno = new ArrayList<TipoDocumento>();
		Query query = getEntityManager().createNamedQuery("TipoDocumento.bucarActivos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		retorno = (List<TipoDocumento>) query.getResultList();
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoDocumento> getTipoDocumentos(String value) {
		List<TipoDocumento> resultado; 
		String patron = value;

		if (value == null || value.length() == 0) {
			patron = "%";
		}else{
			patron = "%" + patron.toLowerCase() + "%";
		}
		Query query = getEntityManager().createNamedQuery("TipoDocumento.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron", patron);
		resultado = (List<TipoDocumento>) query.getResultList();
		return resultado;
	}
}
