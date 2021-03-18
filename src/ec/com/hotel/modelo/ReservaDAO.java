package ec.com.hotel.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

public class ReservaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Reserva> getConsultarDisponibilidad(Integer idHabitacion, Date fecha) {
		List<Reserva> resultado = new ArrayList<Reserva>(); 
		Query query = getEntityManager().createNamedQuery("Reserva.buscarDisponibilidad");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idHabitacion",idHabitacion);
		query.setParameter("fecha", fecha);
		resultado = (List<Reserva>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Reserva> getReservasPendientes() {
		List<Reserva> resultado = new ArrayList<Reserva>(); 
		Query query = getEntityManager().createNamedQuery("Reserva.buscarPendientes");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Reserva>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Reserva> getReservasPendientesPorCliente(Integer idCliente) {
		List<Reserva> resultado = new ArrayList<Reserva>(); 
		Query query = getEntityManager().createNamedQuery("Reserva.buscarPendientesPorCliente");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idCliente", idCliente);
		resultado = (List<Reserva>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Reserva> getReservasPendientesDiaActual(Date fecha,String patron) {
		List<Reserva> resultado = new ArrayList<Reserva>(); 
		Query query = getEntityManager().createNamedQuery("Reserva.buscarPendientesDiaActual");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("fecha", fecha);
		query.setParameter("patron","%" + patron.toLowerCase() + "%");
		resultado = (List<Reserva>) query.getResultList();
		return resultado;
	} 
	
	@SuppressWarnings("unchecked")
	public List<Reserva> buscarPorHabitacion(String patron,String numeroHabitacion) {
		List<Reserva> resultado = new ArrayList<Reserva>(); 
		Query query = getEntityManager().createNamedQuery("Reserva.buscarPorHabitacion");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("numeroHabitacion", numeroHabitacion);
		query.setParameter("patron","%" + patron.toLowerCase() + "%");
		resultado = (List<Reserva>) query.getResultList();
		return resultado;
	}
}
