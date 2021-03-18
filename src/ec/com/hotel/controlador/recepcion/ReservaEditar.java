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
import org.zkoss.zul.Doublebox;
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
import ec.com.hotel.utils.ControllerHelper;
import ec.com.hotel.utils.EnviarCorreo;

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
	@Wire Doublebox txtAdelanto;
	
	
	Reserva reserva;
	Habitacion habitacion;
	Date fecha;
	Cliente huesped;
	TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
	EstadoPagoDAO estadoPagoDAO = new EstadoPagoDAO();
	ReservaDAO reservaDAO = new ReservaDAO();
	TipoDocumento documentoSeleccionado;
	ControllerHelper helper = new ControllerHelper();
	
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
		
		//bloquear los datos del cliente hasta q se seleccione el tipo de documento
		txtNumeroDocumento.setDisabled(true);
		txtNombres.setDisabled(true);
		txtApellidos.setDisabled(true);		
		txtCorreo.setDisabled(true);
		documentoSeleccionado = null;
	}
	
	@Command
	public void cambiarTipoDocumento() {
		if(documentoSeleccionado != null) {
			txtNumeroDocumento.setDisabled(false);
			txtNumeroDocumento.setText("");
			txtNumeroDocumento.setMaxlength(documentoSeleccionado.getDigitos());
			txtNombres.setDisabled(false);
			txtApellidos.setDisabled(false);
			txtCorreo.setDisabled(false);
		}else {
			txtNumeroDocumento.setDisabled(true);
			txtNumeroDocumento.setText("");
			txtNombres.setDisabled(true);
			txtApellidos.setDisabled(true);
			txtCorreo.setDisabled(true);
		}
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
	    
		System.out.println("Hay "+dias+" dias de diferencia reserva");
		txtNumNoches.setText(String.valueOf(dias));
	    
	    //calcular el precio total
	    Float precio = Float.parseFloat(txtPrecio.getText().toString());
	    Float total = dias * precio;
	    txtPrecioTotal.setText(String.valueOf(total));
	    
	}

	public boolean isValidarDatos() {
		try {
			Boolean retorna = true;
			if(cboTipoDocumento.getSelectedIndex() == -1) {
				Clients.showNotification("Debe seleccionar tipo de documento","info",cboTipoDocumento,"end_center",2000);
				return retorna;
			}
			//validar el tipo de documento
			if(documentoSeleccionado.getDigitos() == 10) {// es una cedula
				if(txtNumeroDocumento.getText().length() < documentoSeleccionado.getDigitos()) {
					Clients.showNotification("El número de cédula no tiene la cantidad de digitos obligatorios [10]","info",txtNumeroDocumento,"end_center",2000);
					txtNumeroDocumento.focus();
					return true;
				}
				if(!helper.validarDeCedula(txtNumeroDocumento.getText())) {
					Clients.showNotification("Número de CÉDULA NO VÁLIDA!","info",txtNumeroDocumento,"end_center",2000);
					txtNumeroDocumento.focus();
					return true;
				}
			}else if(documentoSeleccionado.getDigitos() == 13) {// es un ruc
				if(txtNumeroDocumento.getText().length() < documentoSeleccionado.getDigitos()) {
					Clients.showNotification("El número de ruc no tiene la cantidad de digitos obligatorios [13]","info",txtNumeroDocumento,"end_center",2000);
					txtNumeroDocumento.focus();
					return true;
				}
				if(!helper.validarRuc(txtNumeroDocumento.getText())) {
					Clients.showNotification("Número de RUC NO VÁLIDO!","info",txtNumeroDocumento,"end_center",2000);
					txtNumeroDocumento.focus();
					return true;
				}
			}
			//luego preguntar si el numero de documento ya se encuentra sobre los registros
			if(validarClienteExistente() == true) {
				Clients.showNotification("Ya hay un Cliente con el número de documento " + txtNumeroDocumento.getText() + "!","info",txtNumeroDocumento,"end_center",2000);
				txtNumeroDocumento.focus();
				return true;
			}
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
	/** Validar si el cliente existe a traves de la cedula */
	private boolean validarClienteExistente() {
		try {
			boolean bandera = false;
			List<Cliente> listaUsuario;
			if (huesped.getIdCliente() == null) {
				listaUsuario = huespedDAO.getValidarClienteExistente(txtNumeroDocumento.getText().toString());
			}else {
				listaUsuario = huespedDAO.getValidarClienteExistenteDiferente(txtNumeroDocumento.getText().toString(),huesped.getIdCliente());
			}
			
			if(listaUsuario.size() != 0)
				bandera = true;
			else
				bandera = false;
			return bandera;
		}catch(Exception ex) {
			return false;
		}
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
						enviarCorreo();
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
	
	private void enviarCorreo() {
		if(txtCorreo.getText().isEmpty()) {
			return;
		}
		String adjunto = "";
		String[] adjuntos = adjunto.split(",");
		String asunto;
		String destinatario;
		String mensaje;
		int servidor;
		String[] destinatarios;
		asunto = "Reserva Generada";
		destinatario = huesped.getCorreo();	
		destinatarios = destinatario.split(";");
		servidor = 0;
		mensaje = "¡Gracias, " + huesped.getNombres() + " " + huesped.getApellidos() + "!\n";
		mensaje = mensaje + "Se ha realizado con exito tu reserva en Conjunto Residencial Montoya\n\n";
		mensaje = mensaje + "Te esperamos el dia: " + new SimpleDateFormat("dd/MM/yyyy").format(reserva.getFechaEntrada());
		mensaje = mensaje + "\nPagarás directamente en la recepción de la Residencia.";
		mensaje = mensaje + "\n\nDetalle de la Habitacion:";
		mensaje = mensaje + "\nNúmero: " + reserva.getHabitacion().getNumero();
		mensaje = mensaje + "\nPiso: " + reserva.getHabitacion().getNivel().getIdNivel();
		mensaje = mensaje + "\nCategoría: " + reserva.getHabitacion().getCategoria().getCategoria();
		mensaje = mensaje + "\nDetalles: " + reserva.getHabitacion().getDetalles();
		mensaje = mensaje + "\n\nDisfruta Tu estadía";
		mensaje = mensaje + "\n\n\nNota: De no llegar el dia solicitado, la reserva quedará anulada!";
		mensaje = mensaje + "\n\n\n\nResidencia Montoya";
		mensaje = mensaje + "\n" + Constantes.CORREO_ORIGEN;
		
		EnviarCorreo miHilo = new EnviarCorreo(adjunto, adjuntos, destinatarios, servidor, asunto, mensaje);
		miHilo.enviarCorreo();
		Clients.showNotification("Notificación enviada al correo del Gobernador/a");

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
	public TipoDocumento getDocumentoSeleccionado() {
		return documentoSeleccionado;
	}
	public void setDocumentoSeleccionado(TipoDocumento documentoSeleccionado) {
		this.documentoSeleccionado = documentoSeleccionado;
	}
}
