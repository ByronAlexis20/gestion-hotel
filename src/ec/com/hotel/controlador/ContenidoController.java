package ec.com.hotel.controlador;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;

import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.HabitacionDAO;
import ec.com.hotel.modelo.Reserva;
import ec.com.hotel.modelo.ReservaDAO;
import ec.com.hotel.utils.Constantes;

public class ContenidoController {
	Integer numeroCamas;
	Integer habitacionesLibres;
	Integer habitacionesOcupadas;
	Integer habitacionesReservadas;
	
	HabitacionDAO habitacionDAO = new HabitacionDAO();
	ReservaDAO reservaDAO = new ReservaDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		cargarDatos();
	}

	private void cargarDatos() {
		try {
			List<Habitacion> listaHabitaciones = habitacionDAO.getListaHabitaciones("");
			List<Habitacion> listaOcupadas = habitacionDAO.buscarPorEstadoReserva("", Constantes.HABITACION_OCUPADA);
			List<Reserva> listaReserva = reservaDAO.getReservasPendientesDiaActual(new Date(), "");
			//numero de camas
			numeroCamas = listaHabitaciones.size();
			
			//habitaciones libres
			habitacionesLibres = listaHabitaciones.size() - listaOcupadas.size();
			
			//habitaciones ocupadas
			habitacionesOcupadas = listaOcupadas.size();
			
			//habitaciones reservadas
			habitacionesReservadas = listaReserva.size();
			
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	public Integer getNumeroCamas() {
		return numeroCamas;
	}

	public void setNumeroCamas(Integer numeroCamas) {
		this.numeroCamas = numeroCamas;
	}

	public Integer getHabitacionesLibres() {
		return habitacionesLibres;
	}

	public void setHabitacionesLibres(Integer habitacionesLibres) {
		this.habitacionesLibres = habitacionesLibres;
	}

	public Integer getHabitacionesOcupadas() {
		return habitacionesOcupadas;
	}

	public void setHabitacionesOcupadas(Integer habitacionesOcupadas) {
		this.habitacionesOcupadas = habitacionesOcupadas;
	}

	public Integer getHabitacionesReservadas() {
		return habitacionesReservadas;
	}

	public void setHabitacionesReservadas(Integer habitacionesReservadas) {
		this.habitacionesReservadas = habitacionesReservadas;
	}
	
}
