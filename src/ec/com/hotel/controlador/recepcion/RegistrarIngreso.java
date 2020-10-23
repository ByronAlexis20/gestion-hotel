package ec.com.hotel.controlador.recepcion;

import java.io.IOException;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.ClienteDAO;
import ec.com.hotel.modelo.EstadoPagoDAO;
import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.Reserva;
import ec.com.hotel.modelo.ReservaDAO;
import ec.com.hotel.modelo.TipoDocumento;
import ec.com.hotel.modelo.TipoDocumentoDAO;
import ec.com.hotel.utils.Constantes;

public class RegistrarIngreso {
@Wire Window winReservaEditar;
	
	@Wire Textbox txtNumeroHabitacion;
	@Wire Textbox txtCategoria;
	@Wire Textbox txtPrecio;
	@Wire Textbox txtNivel;
	@Wire Textbox txtDetalles;
	
	//controler de la pantalla para el huesped
	@Wire Combobox cboTipoDocumento;
	@Wire Textbox txtNumeroDocumento;
	@Wire Button btnBuscarCliente;
	@Wire Textbox txtNombres;
	@Wire Textbox txtApellidos;
	@Wire Textbox txtCorreo;
	
	ClienteDAO huespedDAO = new ClienteDAO();
	
	//controles de la pantalla de datos del alojamiento
	@Wire Datebox dtpFechaInicio;
	@Wire Datebox dtpFechaSalida;
	@Wire Textbox txtNumNoches;
	@Wire Textbox txtPrecioTotal;
	
	
	Reserva reserva;
	TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
	EstadoPagoDAO estadoPagoDAO = new EstadoPagoDAO();
	ReservaDAO reservaDAO = new ReservaDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		reserva = (Reserva) Executions.getCurrent().getArg().get("Reserva");
		
		if(reserva != null) {
			cargarDatos();
		}
	}
	
	private void cargarDatos() {
		txtNumeroHabitacion.setText(reserva.getHabitacion().getNumero());
		txtCategoria.setText(reserva.getHabitacion().getCategoria().getCategoria());
		txtPrecio.setText(String.valueOf(reserva.getHabitacion().getPrecio()));
		txtNivel.setText(reserva.getHabitacion().getNivel().getNivel());
		txtDetalles.setText(reserva.getHabitacion().getDetalles());
		
		dtpFechaInicio.setValue(reserva.getFechaEntrada());
		dtpFechaInicio.setDisabled(true);
		
		dtpFechaSalida.setDisabled(true);
		dtpFechaSalida.setValue(reserva.getFechaSalida());
		
		//datos del huesped
		txtNumeroDocumento.setText(reserva.getCliente().getNumeroDocumento());
		txtNombres.setText(reserva.getCliente().getNombres());
		txtApellidos.setText(reserva.getCliente().getApellidos());
		txtCorreo.setText(reserva.getCliente().getCorreo());
		cboTipoDocumento.setText(reserva.getCliente().getTipoDocumento().getTipoDocumento());
		txtNumeroDocumento.setText(reserva.getCliente().getNumeroDocumento());
		
		//datos del alojamiento
		
		txtNumNoches.setText(String.valueOf(reserva.getCantidadDias()));
		txtPrecioTotal.setText(String.valueOf(reserva.getPrecioTotal()));
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void grabar(){
		Messagebox.show("Desea guardar el registro?", "Confirmación de Guardar", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {		
					try {		
						Habitacion habitacion = reserva.getHabitacion();
						habitacion.setEstadoReserva(Constantes.HABITACION_OCUPADA);
						reservaDAO.getEntityManager().getTransaction().begin();
						reserva.setEstadoReservaS(Constantes.RESERVA_CONFIRMADA);
						reservaDAO.getEntityManager().merge(reserva);
						reservaDAO.getEntityManager().merge(habitacion);
						reservaDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Reserva.buscarPendientesDiaActual", null);
						salir();
					} catch (Exception e) {
						e.printStackTrace();
						reservaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}	
	
	@Command
	public void salir() {
		winReservaEditar.detach();
	}
	public List<TipoDocumento> getTipoDocumentos(){
		return tipoDocumentoDAO.getListaTiposDocumentosActivos();
	}
	public Reserva getReserva() {
		return reserva;
	}
	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}
	
}
