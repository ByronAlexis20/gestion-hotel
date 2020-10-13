package ec.com.hotel.controlador.salidas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.HabitacionDAO;
import ec.com.hotel.modelo.Reserva;
import ec.com.hotel.modelo.ReservaDAO;
import ec.com.hotel.utils.Constantes;

public class HospedajeSalida {
@Wire Listbox lstReserva;
	
	List<Reserva> listaReserva;
	String textoBuscar;
	ReservaDAO reservaDAO = new ReservaDAO();
	HabitacionDAO habitacionDAO = new HabitacionDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar = "";
		buscar();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Reserva.buscarPendientesDiaActual")
	@Command
	@NotifyChange({"listaReserva"})
	public void buscar(){
		if (listaReserva != null) {
			listaReserva = null; 
		}
		List<Habitacion> listaHabitacion = habitacionDAO.buscarPorEstadoReserva("", Constantes.HABITACION_OCUPADA);
		List<Reserva> lista = new ArrayList<>();
		for(Habitacion hab : listaHabitacion) {
			List<Reserva> porHabitacion = reservaDAO.buscarPorHabitacion(textoBuscar,hab.getNumero());
			if(porHabitacion.size() > 0)
				lista.add(porHabitacion.get(0));
		}
		listaReserva = lista;
		lstReserva.setModel(new ListModelList(listaReserva));
		if(listaReserva.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	@Command
	public void registrarServicio(@BindingParam("reserva") Reserva seleccion) {
		if (seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Reserva", seleccion);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/servicios_salidas/salidas/confirmarSalida.zul", null, params);
		ventanaCargar.doModal();
	}
	public List<Reserva> getListaReserva() {
		return listaReserva;
	}
	public void setListaReserva(List<Reserva> listaReserva) {
		this.listaReserva = listaReserva;
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
}
