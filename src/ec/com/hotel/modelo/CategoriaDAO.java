package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class CategoriaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Categoria> getListaCategoriasActivos() {
		List<Categoria> retorno = new ArrayList<Categoria>();
		Query query = getEntityManager().createNamedQuery("Categoria.bucarActivos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		retorno = (List<Categoria>) query.getResultList();
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> getCategorias(String value) {
		List<Categoria> resultado; 
		String patron = value;

		if (value == null || value.length() == 0) {
			patron = "%";
		}else{
			patron = "%" + patron.toLowerCase() + "%";
		}
		Query query = getEntityManager().createNamedQuery("Categoria.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron", patron);
		resultado = (List<Categoria>) query.getResultList();
		return resultado;
	}
}
