package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class ImagenDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Imagen> obtenerImagenPorHabitacion(Integer idHabitacion,String tipoImagen) {
		List<Imagen> resultado = new ArrayList<Imagen>(); 
		Query query = getEntityManager().createNamedQuery("Imagen.buscarPorhabitacion");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idHabitacion",idHabitacion);
		query.setParameter("tipoImagen",tipoImagen);
		resultado = (List<Imagen>) query.getResultList();
		return resultado;
	}
	
}
