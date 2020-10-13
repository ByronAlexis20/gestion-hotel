package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class EmpresaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Empresa> buscarEmpresaActiva() {
		List<Empresa> retorno = new ArrayList<Empresa>();
		Query query = getEntityManager().createNamedQuery("Empresa.bucarActivos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		retorno = (List<Empresa>) query.getResultList();
		return retorno;
	}
}
