package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class HabitacionDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Habitacion> getListaHabitaciones(String value) {
		List<Habitacion> resultado = new ArrayList<Habitacion>(); 
		Query query = getEntityManager().createNamedQuery("Habitacion.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Habitacion>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Habitacion> buscarPorEstadoReserva(String value,String estadoReserva) {
		List<Habitacion> resultado = new ArrayList<Habitacion>(); 
		Query query = getEntityManager().createNamedQuery("Habitacion.buscarPorEstadoReserva");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		query.setParameter("estadoReserva",estadoReserva);
		resultado = (List<Habitacion>) query.getResultList();
		return resultado;
	}
}
