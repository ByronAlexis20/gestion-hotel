package ec.com.hotel.controlador.recepcion;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

import ec.com.hotel.modelo.Reserva;
import ec.com.hotel.modelo.ReservaDAO;

public class IngresoLista {
	@Wire Listbox lstReserva;
	@Wire Label lblTitulo;
	public String textoBuscar;
	
	Reserva reservaSeleccionada;
	List<Reserva> listaReserva;
	ReservaDAO reservaDAO = new ReservaDAO();
	Date fecha;
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar = "";
		fecha = new Date();
		buscar();
		lblTitulo.setValue("Registro de ingresos (Reservas de hoy " + new SimpleDateFormat("dd/MM/yyyy").format(fecha) + ")");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Reserva.buscarPendientesDiaActual")
	@Command
	@NotifyChange({"listaReserva"})
	public void buscar(){
		if (listaReserva != null) {
			listaReserva = null; 
		}
		listaReserva = reservaDAO.getReservasPendientesDiaActual(fecha,textoBuscar);
		lstReserva.setModel(new ListModelList(listaReserva));
		if(listaReserva.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
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
	@Command
	public void registrarIngreso(@BindingParam("reserva") Reserva seleccion){
		if (seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Reserva", seleccion);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/reservas_recepcion/ingresos/registrarIngreso.zul", null, params);
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
	public Reserva getReservaSeleccionada() {
		return reservaSeleccionada;
	}
	public void setReservaSeleccionada(Reserva reservaSeleccionada) {
		this.reservaSeleccionada = reservaSeleccionada;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}	
	
}
