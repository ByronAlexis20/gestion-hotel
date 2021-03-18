package ec.com.hotel.controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

import ec.com.hotel.modelo.Comprobante;
import ec.com.hotel.modelo.ComprobanteDAO;
import ec.com.hotel.modelo.ComprobanteDetalle;
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
	
	float totalRecaudado = 0;
	
	HabitacionDAO habitacionDAO = new HabitacionDAO();
	ReservaDAO reservaDAO = new ReservaDAO();
	ComprobanteDAO comprobanteDAO = new ComprobanteDAO();
	
	@Wire Listbox lstReserva;
	List<Reserva> listaReserva;
	List<Recaudaciones> listaRecaudacion;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		cargarDatos();
		buscar();
		consultarRecaudaciones();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Reserva.buscarPendientes")
	@Command
	@NotifyChange({"listaReserva"})
	public void buscar(){
		if (listaReserva != null) {
			listaReserva = null; 
		}
		listaReserva = reservaDAO.getReservasPendientesDiaActual(new Date(), "");
		lstReserva.setModel(new ListModelList(listaReserva));
		if(listaReserva.size() == 0) {
			//Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	public void consultarRecaudaciones() {
		try {
			List<Comprobante> listaComprobante = comprobanteDAO.buscarPorFecha(new Date());
			List<Recaudaciones> recaudaciones = new ArrayList<>();
			for(Comprobante c : listaComprobante) {
				Recaudaciones recaudacion = new Recaudaciones();
				String detalle = "";
				recaudacion.setCodigoFactura(c.getIdComprobante());
				recaudacion.setTotal(c.getTotal());
				recaudacion.setFecha(c.getFecha());
				totalRecaudado += c.getTotal();
				detalle = "POR SERVICIO DE HOTEL EN HABITACIÓN : " + c.getReserva().getHabitacion().getNumero();
				for(ComprobanteDetalle d : c.getComprobanteDetalles()) {
					if(d.getServicio().getIdServicio() != Constantes.ID_SERVICIO_HOTEL) {
						detalle += "; " + d.getServicio().getServicio();
					}
				}
				recaudacion.setDetalle(detalle);
				recaudaciones.add(recaudacion);
			}
			listaRecaudacion = recaudaciones;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
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

	public List<Reserva> getListaReserva() {
		return listaReserva;
	}

	public void setListaReserva(List<Reserva> listaReserva) {
		this.listaReserva = listaReserva;
	}
	
	public List<Recaudaciones> getListaRecaudacion() {
		return listaRecaudacion;
	}

	public void setListaRecaudacion(List<Recaudaciones> listaRecaudacion) {
		this.listaRecaudacion = listaRecaudacion;
	}

	public float getTotalRecaudado() {
		return totalRecaudado;
	}

	public void setTotalRecaudado(float totalRecaudado) {
		this.totalRecaudado = totalRecaudado;
	}

	public class Recaudaciones {
		private Integer codigoFactura;
		private Date fecha;
		private String detalle;
		private float total;
		public Integer getCodigoFactura() {
			return codigoFactura;
		}
		public void setCodigoFactura(Integer codigoFactura) {
			this.codigoFactura = codigoFactura;
		}
		public Date getFecha() {
			return fecha;
		}
		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}
		public String getDetalle() {
			return detalle;
		}
		public void setDetalle(String detalle) {
			this.detalle = detalle;
		}
		public float getTotal() {
			return total;
		}
		public void setTotal(float total) {
			this.total = total;
		}
		
	}
}
