package ec.com.hotel.controlador.recepcion;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.HabitacionDAO;
import ec.com.hotel.modelo.ReservaDAO;

public class SeleccionHabitacion {
	@Wire Window winSeleccionHabitacion;
	@Wire Datebox dtpFecha;
	@Wire Listbox lstHabitaciones;

	List<Habitacion> listaHabitacion;
	HabitacionDAO habitacionDAO = new HabitacionDAO();
	ReservaDAO reservaDAO = new ReservaDAO();

	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		dtpFecha.setConstraint("after " + new SimpleDateFormat("yyyyMMdd").format(new Date()));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GlobalCommand("Habitacion.buscarPorPatron")
	@Command
	@NotifyChange({"listaHabitacion"})
	public void buscarHabitaciones() {
		if(dtpFecha.getValue() == null) {
			Clients.showNotification("Debe seleccionar la fecha","info",dtpFecha,"end_center",2000);
			return;
		}
		if(listaHabitacion != null)
			listaHabitacion = null;
		//primero busco las habitaciones activas
		List<Habitacion> habitacionesActivas = new ArrayList<>();
		habitacionesActivas = habitacionDAO.getListaHabitaciones("");
		List<Habitacion> habitacionesDisponibles = new ArrayList<>();
		for(Habitacion hab : habitacionesActivas) {
			//si es mayor a cero.. quiere decir que si se encuentra reservada
			//si es menor o igual a cero. la habitacion se encuentra disponible
			if(reservaDAO.getConsultarDisponibilidad(hab.getIdHabitacion(), dtpFecha.getValue()).size() <= 0) {
				habitacionesDisponibles.add(hab);
			}
		}
		listaHabitacion = habitacionesDisponibles;
		lstHabitaciones.setModel(new ListModelList(listaHabitacion));
	}
	//evidencias
	@Command
	public void seleccionarHabitacion(@BindingParam("habitacion") Habitacion seleccion){
		if(seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		habitacionDAO.getEntityManager().refresh(seleccion);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Habitacion", seleccion);
		params.put("Fecha", dtpFecha.getValue());
		Window ventanaCargar = (Window) Executions.createComponents("/forms/reservas_recepcion/reservas/reservaEditar.zul", null, params);
		ventanaCargar.doModal();
	}
	@Command
	public void salir() {
		BindUtils.postGlobalCommand(null, null, "Reserva.buscarPendientes", null);
		
		winSeleccionHabitacion.detach();
	}
	public List<Habitacion> getListaHabitacion() {
		return listaHabitacion;
	}

	public void setListaHabitacion(List<Habitacion> listaHabitacion) {
		this.listaHabitacion = listaHabitacion;
	}

}
