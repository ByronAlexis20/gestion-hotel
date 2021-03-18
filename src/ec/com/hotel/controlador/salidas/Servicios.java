package ec.com.hotel.controlador.salidas;

import java.io.IOException;
import java.util.List;

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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Adicional;
import ec.com.hotel.modelo.AdicionalDAO;
import ec.com.hotel.modelo.Reserva;
import ec.com.hotel.modelo.Servicio;
import ec.com.hotel.modelo.ServicioDAO;

public class Servicios {
	List<Servicio> listaServicios;
	@Wire Window winServicios;
	@Wire Listbox lstServicios;

	ServicioDAO servicioDAO = new ServicioDAO();
	
	Reserva reserva;
	AdicionalDAO adicionalDAO = new AdicionalDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		reserva = (Reserva) Executions.getCurrent().getArg().get("Reserva");
		if(reserva != null)
			cargarServicios();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GlobalCommand("Servicio.buscarActivos")
	@NotifyChange({"listaServicios"})
	private void cargarServicios() {
		if(listaServicios != null)
			listaServicios = null;
		listaServicios = servicioDAO.buscarActivos();
		lstServicios.setModel(new ListModelList(listaServicios));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void seleccionarServicio(@BindingParam("servicio") Servicio seleccion) {
		if (seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}
		Messagebox.show("Desea guardar el registro?", "Confirmación de Guardar", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {		
					try {		
						Adicional adicional = new Adicional();
						adicional.setCantidad(1);
						adicional.setEstado("A");
						adicional.setIdAdicional(null);
						adicional.setPrecio(seleccion.getPrecio());
						adicional.setReserva(reserva);
						adicional.setServicio(seleccion);
						adicional.setTotal(seleccion.getPrecio());
						
						adicionalDAO.getEntityManager().getTransaction().begin();
						
						adicionalDAO.getEntityManager().persist(adicional);
						
						adicionalDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Adicional.buscarPorReserva", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						adicionalDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}
	@Command
	public void salir() {
		winServicios.detach();
	}
	public List<Servicio> getListaServicios() {
		return listaServicios;
	}
	public void setListaServicios(List<Servicio> listaServicios) {
		this.listaServicios = listaServicios;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}
}
