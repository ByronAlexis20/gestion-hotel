package ec.com.hotel.controlador.administracion;

import java.io.IOException;
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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.HabitacionDAO;


public class HabitacionLista {
	public String textoBuscar;
	HabitacionDAO habitacionDAO = new HabitacionDAO();
	List<Habitacion> habitacionLista;
	@Wire Listbox lstHabitaciones;
	Habitacion habitacionSeleccionada;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Habitacion.buscarPorPatron")
	@Command
	@NotifyChange({"habitacionLista"})
	public void buscar(){
		if (habitacionLista != null) {
			habitacionLista = null; 
		}
		habitacionLista = habitacionDAO.getListaHabitaciones(textoBuscar);
		lstHabitaciones.setModel(new ListModelList(habitacionLista));
		habitacionSeleccionada = null;
		if(habitacionLista.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	@Command
	public void nuevo(){
		Window ventanaCargar = (Window) Executions.createComponents("/forms/administracion/habitaciones/habitacionEditar.zul", null, null);
		ventanaCargar.doModal();
	}

	@Command
	public void editar(@BindingParam("habitacion") Habitacion habitacionSeleccionada){
		if(habitacionSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		habitacionDAO.getEntityManager().refresh(habitacionSeleccionada);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Habitacion", habitacionSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/administracion/habitaciones/habitacionEditar.zul", null, params);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("habitacion") Habitacion habitacionSeleccionada){
		if (habitacionSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						habitacionDAO.getEntityManager().getTransaction().begin();
						habitacionSeleccionada.setEstado("I");
						habitacionDAO.getEntityManager().merge(habitacionSeleccionada);
						habitacionDAO.getEntityManager().getTransaction().commit();;
						// Actualiza la lista
						//BindUtils.postGlobalCommand(null, null, "Categoria.buscarPorPatron", null);
						buscar();
						Clients.showNotification("Transaccion ejecutada con exito.");
					} catch (Exception e) {
						e.printStackTrace();
						habitacionDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});		
	}
	@Command
	public void imagenes() {
		if(habitacionSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		habitacionDAO.getEntityManager().refresh(habitacionSeleccionada);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Habitacion", habitacionSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/administracion/habitaciones/imagenes.zul", null, params);
		ventanaCargar.doModal();
	}
	public List<Habitacion> getHabitacionLista() {
		return habitacionLista;
	}
	public void setHabitacionLista(List<Habitacion> habitacionLista) {
		this.habitacionLista = habitacionLista;
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	public Habitacion getHabitacionSeleccionada() {
		return habitacionSeleccionada;
	}
	public void setHabitacionSeleccionada(Habitacion habitacionSeleccionada) {
		this.habitacionSeleccionada = habitacionSeleccionada;
	}	
}
