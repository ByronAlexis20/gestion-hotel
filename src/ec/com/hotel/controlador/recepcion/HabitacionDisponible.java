package ec.com.hotel.controlador.recepcion;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.HabitacionDAO;
import ec.com.hotel.modelo.Reserva;
import ec.com.hotel.modelo.ReservaDAO;
import ec.com.hotel.utils.Constantes;

public class HabitacionDisponible {
	@Wire Listbox lstHabitaciones;
	@Wire Label lblTitulo;
	public String textoBuscar;
	
	List<Habitacion> listaHabitacionesDisponibles;
	HabitacionDAO habitacionDAO = new HabitacionDAO();
	ReservaDAO reservaDAO = new ReservaDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar = "";
		buscar();
		lblTitulo.setValue("Recepción (" + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ")");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Habitacion.buscarPorPatron")
	@Command
	@NotifyChange({"listaHabitacionesDisponibles"})
	public void buscar(){
		if (listaHabitacionesDisponibles != null) {
			listaHabitacionesDisponibles = null; 
		}
		List<Habitacion> lista = habitacionDAO.getListaHabitaciones("");
		if(lista.size() > 0) {
			List<Habitacion> habitaciones = new ArrayList<>();
			List<String> estados = new ArrayList<>();
			estados.add(Constantes.HABITACION_DISPONIBLE);
			estados.add(Constantes.HABITACION_LIMPIEZA);
			estados.add(Constantes.HABITACION_OCUPADA);
			for(String est : estados) {
				for(Habitacion hab : lista) {
					//si es mayor a cero.. quiere decir que si se encuentra reservada
					//si es menor o igual a cero. la habitacion se encuentra disponible
					if(hab.getEstadoReserva().equals(est))
						habitaciones.add(hab);
				}
			}
			listaHabitacionesDisponibles = habitaciones;
			lstHabitaciones.setModel(new ListModelList(listaHabitacionesDisponibles));
		}
		/*
		if (listaReserva != null) {
			listaReserva = null; 
		}
		listaReserva = reservaDAO.getReservasPendientesDiaActual(new Date(),textoBuscar);
		lstReserva.setModel(new ListModelList(listaReserva));
		if(listaReserva.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
		*/
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("reserva") Reserva seleccion){

		if (seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}

		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						reservaDAO.getEntityManager().getTransaction().begin();
						seleccion.setEstado("I");
						reservaDAO.getEntityManager().merge(seleccion);
						reservaDAO.getEntityManager().getTransaction().commit();;
						// Actualiza la lista
						//BindUtils.postGlobalCommand(null, null, "Categoria.buscarPorPatron", null);
						buscar();
						Clients.showNotification("Transaccion ejecutada con exito.");
					} catch (Exception e) {
						e.printStackTrace();
						reservaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void registrarIngreso(@BindingParam("habitacion") Habitacion seleccion){
		if (seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}
		if(seleccion.getEstadoReserva().equals(Constantes.HABITACION_OCUPADA)) {
			Clients.showNotification("La Habita.");
			return;
		}
		if(seleccion.getEstadoReserva().equals(Constantes.HABITACION_LIMPIEZA)) {
			Messagebox.show("Terminar el proceso de LIMPIEZA?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

				@Override
				public void onEvent(Event event) throws Exception {
					if (event.getName().equals("onYes")) {
						try {
							reservaDAO.getEntityManager().getTransaction().begin();
							seleccion.setEstadoReserva(Constantes.HABITACION_DISPONIBLE);
							reservaDAO.getEntityManager().merge(seleccion);
							reservaDAO.getEntityManager().getTransaction().commit();;
							// Actualiza la lista
							//BindUtils.postGlobalCommand(null, null, "Categoria.buscarPorPatron", null);
							buscar();
							Clients.showNotification("Transaccion ejecutada con exito.");
						} catch (Exception e) {
							e.printStackTrace();
							reservaDAO.getEntityManager().getTransaction().rollback();
						}
					}
				}
			});	
		}else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("Habitacion", seleccion);
			params.put("Fecha", new Date());
			Window ventanaCargar = (Window) Executions.createComponents("/forms/reservas_recepcion/recepcion/registroRecepcion.zul", null, params);
			ventanaCargar.doModal();
		}
	}
	
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}

	public List<Habitacion> getListaHabitacionesDisponibles() {
		return listaHabitacionesDisponibles;
	}

	public void setListaHabitacionesDisponibles(List<Habitacion> listaHabitacionesDisponibles) {
		this.listaHabitacionesDisponibles = listaHabitacionesDisponibles;
	}

}
