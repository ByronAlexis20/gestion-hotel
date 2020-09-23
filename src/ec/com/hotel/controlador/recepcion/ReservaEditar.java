package ec.com.hotel.controlador.recepcion;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import ec.com.hotel.modelo.Cliente;
import ec.com.hotel.modelo.ClienteDAO;
import ec.com.hotel.modelo.EstadoPagoDAO;
import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.Reserva;
import ec.com.hotel.modelo.ReservaDAO;
import ec.com.hotel.modelo.TipoDocumento;
import ec.com.hotel.modelo.TipoDocumentoDAO;
import ec.com.hotel.utils.Constantes;

public class ReservaEditar {
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
	@Wire Textbox txtAdelanto;
	
	
	Reserva reserva;
	Habitacion habitacion;
	Date fecha;
	Cliente huesped;
	TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
	EstadoPagoDAO estadoPagoDAO = new EstadoPagoDAO();
	ReservaDAO reservaDAO = new ReservaDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		habitacion = (Habitacion) Executions.getCurrent().getArg().get("Habitacion");
		fecha = (Date) Executions.getCurrent().getArg().get("Fecha");
		if(habitacion != null) {
			txtNumeroHabitacion.setText(habitacion.getNumero());
			txtCategoria.setText(habitacion.getCategoria().getCategoria());
			txtPrecio.setText(String.valueOf(habitacion.getPrecio()));
			txtNivel.setText(habitacion.getNivel().getNivel());
			txtDetalles.setText(habitacion.getDetalles());
			
			dtpFechaInicio.setValue(fecha);
			dtpFechaSalida.setConstraint("after " + new SimpleDateFormat("yyyyMMdd").format(fecha));
		}
		reserva = new Reserva();
	}
	
	
	@Command
	public void buscarCiente() {
		List<Cliente> listado = new ArrayList<>();
		listado = huespedDAO.getListaClientesBuscarCedula(txtNumeroDocumento.getText().toString());
		if(listado.size() > 0) {//tiene datos el cliente, se recupera
			txtNumeroDocumento.setText(listado.get(0).getNumeroDocumento());
			txtNombres.setText(listado.get(0).getNombres());
			txtApellidos.setText(listado.get(0).getApellidos());
			txtCorreo.setText(listado.get(0).getCorreo());
			huesped = listado.get(0);
		}else {
			huesped = new Cliente();
			huesped.setEstado("A");
			huesped.setIdCliente(null);
		}
	}
	
	@Command
	public void seleccionaFechaFin() {
	    
	    int dias=(int) ((dtpFechaSalida.getValue().getTime()-dtpFechaInicio.getValue().getTime())/86400000);
	    
		System.out.println("Hay "+dias+" dias de diferencia");
		txtNumNoches.setText(String.valueOf(dias));
	    
	    //calcular el precio total
	    Float precio = Float.parseFloat(txtPrecio.getText().toString());
	    Float total = dias * precio;
	    txtPrecioTotal.setText(String.valueOf(total));
	    
	}

	public boolean isValidarDatos() {
		try {
			Boolean retorna = true;
			if(txtNumeroDocumento.getText().isEmpty()) {
				Clients.showNotification("Obligatoria regitrar el número de documento","info",txtNumeroDocumento,"end_center",2000);
				return retorna;
			}
			if(txtNombres.getText().isEmpty()) {
				Clients.showNotification("Obligatoria regitrar el nombre del huesped","info",txtNombres,"end_center",2000);
				return retorna;
			}
			if(txtApellidos.getText().isEmpty()) {
				Clients.showNotification("Obligatoria regitrar el número de documento","info",txtApellidos,"end_center",2000);
				return retorna;
			}			
			if(dtpFechaSalida.getValue() == null) {
				Clients.showNotification("Obligatoria fecha de salida","info",dtpFechaSalida,"end_center",2000);
				return retorna;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void grabar(){
		if(isValidarDatos() == true) {
			return;
		}
		Messagebox.show("Desea guardar el registro?", "Confirmación de Guardar", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {		
					try {		
						
						reservaDAO.getEntityManager().getTransaction().begin();
						copiarDatos();
						
						if (huesped.getIdCliente() == null) {
							reservaDAO.getEntityManager().persist(huesped);
						}else{
							reservaDAO.getEntityManager().merge(huesped);
						}			
						reservaDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Reserva.buscarPendientes", null);
						BindUtils.postGlobalCommand(null, null, "Habitacion.buscarPorPatron", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						reservaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}	
	private void copiarDatos() {
		reserva.setCantidadDias(Integer.parseInt(txtNumNoches.getText()));
		reserva.setEstado("A");
		reserva.setEstadoPago(estadoPagoDAO.getPagoPorId(Constantes.ID_PAGO_PENDIENTE).get(0));
		reserva.setEstadoReservaS(Constantes.RESERVA_PENDIENTE);
		reserva.setFechaEntrada(dtpFechaInicio.getValue());
		reserva.setFechaSalida(dtpFechaSalida.getValue());
		reserva.setHabitacion(habitacion);
		reserva.setPrecioTotal(Float.parseFloat(txtPrecioTotal.getText()));
		reserva.setFechaRegistro(new Date());
		if(!txtAdelanto.getText().isEmpty()) {
			reserva.setPrecioAdelanto(Float.parseFloat(txtAdelanto.getText()));
		}
		
		reserva.setCliente(huesped);
		if(huesped.getIdCliente() != null) {
			huesped.setTipoDocumento((TipoDocumento)cboTipoDocumento.getSelectedItem().getValue());
			huesped.setApellidos(txtApellidos.getText());
			huesped.setNombres(txtNombres.getText());
			huesped.setCorreo(txtCorreo.getText());
			huesped.setEstado("A");
			huesped.setNumeroDocumento(txtNumeroDocumento.getText());
			
			if(huesped.getReservas().size() > 0) {
				huesped.addReserva(reserva);
			}else {
				List<Reserva> lista = new ArrayList<>();
				lista.add(reserva);
				huesped.setReservas(lista);
			}
		}else {
			huesped.setTipoDocumento((TipoDocumento)cboTipoDocumento.getSelectedItem().getValue());
			huesped.setApellidos(txtApellidos.getText());
			huesped.setNombres(txtNombres.getText());
			huesped.setCorreo(txtCorreo.getText());
			huesped.setEstado("A");
			huesped.setNumeroDocumento(txtNumeroDocumento.getText());
			List<Reserva> lista = new ArrayList<>();
			lista.add(reserva);
			huesped.setReservas(lista);
		}
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
	public Habitacion getHabitacion() {
		return habitacion;
	}
	public void setHabitacion(Habitacion habitacion) {
		this.habitacion = habitacion;
	}
	public Cliente getHuesped() {
		return huesped;
	}
	public void setHuesped(Cliente huesped) {
		this.huesped = huesped;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
}
