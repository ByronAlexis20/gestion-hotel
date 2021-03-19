package ec.com.hotel.controlador.salidas;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Adicional;
import ec.com.hotel.modelo.AdicionalDAO;
import ec.com.hotel.modelo.Comprobante;
import ec.com.hotel.modelo.ComprobanteDAO;
import ec.com.hotel.modelo.ComprobanteDetalle;
import ec.com.hotel.modelo.Empresa;
import ec.com.hotel.modelo.EmpresaDAO;
import ec.com.hotel.modelo.EstadoPago;
import ec.com.hotel.modelo.EstadoPagoDAO;
import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.Reserva;
import ec.com.hotel.modelo.ReservaDAO;
import ec.com.hotel.modelo.Servicio;
import ec.com.hotel.modelo.ServicioDAO;
import ec.com.hotel.utils.Constantes;
import ec.com.hotel.utils.PrintReport;

public class ConfirmarSalida {
	@Wire Window winConfirmarSalida;
	@Wire Textbox txtNumeroHabitacion;
	@Wire Textbox txtCategoria;
	@Wire Textbox txtDetalles;
	@Wire Textbox txtNivel;
	@Wire Textbox txtCliente;
	@Wire Textbox txtPrecioDia;
	@Wire Textbox txtFechaInicio;
	@Wire Datebox dtpFechaFin;
	@Wire Textbox txtDias;
	@Wire Textbox txtTotalHospedaje;
	@Wire Listbox lstServicios;
	@Wire Textbox txtSubtotal;
	@Wire Textbox txtAdelanto;
	@Wire Textbox txtAdicional;
	@Wire Textbox txtDescuento;
	@Wire Textbox txtTotalPagar;
	@Wire Textbox txtIncidencias;
	List<Adicional> listaServicios;
	Reserva reserva;
	AdicionalDAO adicionalDAO = new AdicionalDAO();
	ReservaDAO reservDAO = new ReservaDAO();
	ComprobanteDAO comprobanteDAO = new ComprobanteDAO();
	ServicioDAO servicioDAO = new ServicioDAO();
	EstadoPagoDAO estadoPagoDAO = new EstadoPagoDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		reserva = (Reserva) Executions.getCurrent().getArg().get("Reserva");
		if(reserva != null) {
			cargarDatosReserva();
			dtpFechaFin.setConstraint("after " + new SimpleDateFormat("yyyyMMdd").format(reserva.getFechaEntrada()));
			cargarAdicionales();
			sumarValoresTotales();
		}
	}

	private void cargarDatosReserva() {
		txtNumeroHabitacion.setText(reserva.getHabitacion().getNumero());
		txtCategoria.setText(reserva.getHabitacion().getCategoria().getCategoria());
		txtDetalles.setText(reserva.getHabitacion().getDetalles());
		txtNivel.setText(reserva.getHabitacion().getNivel().getNivel());
		txtCliente.setText("No. Doc.: " + reserva.getCliente().getNumeroDocumento() + " - " + reserva.getCliente().getNombres() + " " + reserva.getCliente().getApellidos());
		txtPrecioDia.setText(String.valueOf(reserva.getHabitacion().getPrecio()));
		txtFechaInicio.setText(new SimpleDateFormat("dd/MM/yyyy").format(reserva.getFechaEntrada()));
		dtpFechaFin.setValue(reserva.getFechaSalida());
		txtDias.setText(String.valueOf(reserva.getCantidadDias()));
		txtTotalHospedaje.setText(String.valueOf(reserva.getPrecioTotal()));
	}

	@Command
	public void seleccionaFechaFin() {
		int dias=(int) ((dtpFechaFin.getValue().getTime() - reserva.getFechaEntrada().getTime())/86400000);
		txtDias.setText(String.valueOf(dias));
		//calcular el precio total
		Float precio = reserva.getHabitacion().getPrecio();
		Float total = dias * precio;
		txtTotalHospedaje.setText(String.valueOf(total));
		sumarValoresTotales();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GlobalCommand("Adicional.buscarPorReserva")
	@Command
	@NotifyChange({"listaServicios"})
	public void cargarAdicionales() {
		if(listaServicios != null)
			listaServicios = null;
		listaServicios = adicionalDAO.buscarPorReserva(reserva.getIdReserva());
		lstServicios.setModel(new ListModelList(listaServicios));
	}
	
	@Command
	public void sumarValoresTotales() {
		//cargas otros valores
		double totalServicios = 0.0;
		for(Adicional ad : listaServicios) {
			totalServicios = totalServicios + ad.getTotal();
		}
		double subtotal = totalServicios + Double.valueOf(txtTotalHospedaje.getText());
		txtSubtotal.setText(String.valueOf(subtotal));
		txtAdelanto.setText(String.valueOf(reserva.getPrecioAdelanto()));
		
		double adicional = 0.0;
		if(!txtAdicional.getText().isEmpty()) {
			adicional = Double.valueOf(txtAdicional.getText()); 
		}
		double descuento = 0.0;
		if(!txtDescuento.getText().isEmpty()) {
			descuento = Double.valueOf(txtDescuento.getText()); 
		}
		
		double totalPagar = (subtotal + adicional - descuento) - reserva.getPrecioAdelanto();
		
		txtTotalPagar.setText(String.format("%.2f",totalPagar));

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void grabar() {
		Messagebox.show("Desea guardar el registro?", "Confirmación de Guardar", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {		
					try {	
						Comprobante comprobante = new Comprobante();
						comprobante.setEstado("A");
						comprobante.setFecha(new Date());
						comprobante.setIdComprobante(null);
						comprobante.setObservacion(txtIncidencias.getText());
						comprobante.setReserva(reserva);
						float total = Float.valueOf(txtTotalPagar.getText()) + reserva.getPrecioAdelanto();
						comprobante.setTotal(total);
						
						//el primer detalle es por el hospedaje
						List<ComprobanteDetalle> listaDetalle = new ArrayList<>();
						ComprobanteDetalle detalleServicio = new ComprobanteDetalle();
						detalleServicio.setCantidad(Integer.valueOf(txtDias.getText()));
						detalleServicio.setComprobante(comprobante);
						detalleServicio.setDescripcion("POR HOSPEDAJE EN EL HOTEL EN HABITACIÓN " + reserva.getHabitacion().getNumero());
						detalleServicio.setEstado("A");
						detalleServicio.setIdDetalleComprobante(null);
						detalleServicio.setTotal(Float.valueOf(txtTotalHospedaje.getText()));
						List<Servicio> servicios = servicioDAO.buscarPorId(Constantes.ID_SERVICIO_HOTEL);
						if(servicios.size() > 0)
							detalleServicio.setServicio(servicios.get(0));
						detalleServicio.setPrecio(reserva.getHabitacion().getPrecio());
						
						listaDetalle.add(detalleServicio);
						
						//los detalles por los servicios
						for(Adicional adicional : listaServicios) {
							ComprobanteDetalle detalle = new ComprobanteDetalle();
							detalle.setCantidad(1);
							detalle.setComprobante(comprobante);
							detalle.setDescripcion("Por solicitar servicio de " + adicional.getServicio().getServicio());
							detalle.setEstado("A");
							detalle.setIdDetalleComprobante(null);
							detalle.setPrecio(adicional.getPrecio());
							detalle.setServicio(adicional.getServicio());
							detalle.setTotal(adicional.getTotal());
							
							listaDetalle.add(detalle);
						}
						
						comprobante.setComprobanteDetalles(listaDetalle);
						//ademas hay q poner a la habitacion en estado de limpieza
						Habitacion habitacion = new Habitacion();
						habitacion = reserva.getHabitacion();
						habitacion.setEstadoReserva(Constantes.HABITACION_LIMPIEZA);
						//hay q poner en estado PAGADO a la reserva y si se modifico la fecha de salida
						List<EstadoPago> estadosPagos = estadoPagoDAO.getPagoPorId(Constantes.ID_PAGO_CONFIRMADO);
						reserva.setEstadoPago(estadosPagos.get(0));
						reserva.setFechaSalida(dtpFechaFin.getValue());
						reserva.setCantidadDias(Integer.valueOf(txtDias.getText()));
						float adicional = 0;
						if(!txtAdicional.getText().isEmpty()) {
							adicional = Float.valueOf(txtAdicional.getText()); 
						}
						float descuento = 0;
						if(!txtDescuento.getText().isEmpty()) {
							descuento = Float.valueOf(txtDescuento.getText()); 
						}
						reserva.setPrecioAdicinal(adicional);
						reserva.setPrecioDescuento(descuento);
						reserva.setPrecioTotal(total);
						reserva.setIncidencias(txtIncidencias.getText());
						
						comprobante.setTotal(total);
						
						
						comprobanteDAO.getEntityManager().getTransaction().begin();
						comprobanteDAO.getEntityManager().merge(reserva);
						comprobanteDAO.getEntityManager().merge(habitacion);
						comprobanteDAO.getEntityManager().persist(comprobante);
						comprobanteDAO.getEntityManager().getTransaction().commit();
						
						imprimirReporte(comprobante);
						
						System.out.println(comprobante.getIdComprobante());
						System.out.println(comprobante.getReserva().getCliente().getNombres());
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Reserva.buscarPendientesDiaActual", null);
						//salir();						
					} catch (Exception e) {
						e.printStackTrace();
						comprobanteDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}	
	
	@SuppressWarnings("unused")
	public void imprimirReporte(Comprobante comprobante) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ID_COMPROBANTE",comprobante.getIdComprobante());
			String hotel = "", representante = "", direccion = "", email = "", telefono = "",ruc = "";
			EmpresaDAO empresaDAO = new EmpresaDAO();
			List<Empresa> empresas = empresaDAO.buscarEmpresaActiva();
			if(empresas.size() > 0) {
				hotel = empresas.get(0).getNombre();
				representante = empresas.get(0).getRepresentante();
				direccion = empresas.get(0).getDireccion();
				email = empresas.get(0).getEmail();
				telefono = empresas.get(0).getTelefono();
				ruc = empresas.get(0).getRuc();
			}
			
			params.put("HOTEL",hotel);
			params.put("REPRESENTANTE","Representante: " + representante);
			params.put("DIRECCION","Dirección: " + direccion);
			params.put("EMAIL","Correo: " + email);
			params.put("TELEFONO","Teléfono: " + telefono);
			params.put("RUC","Ruc: " + ruc);
			
			params.put("NO_COMPROBANTE",String.valueOf(comprobante.getIdComprobante()));
			params.put("NO_HABITACION",comprobante.getReserva().getHabitacion().getNumero());
			params.put("NIVEL",comprobante.getReserva().getHabitacion().getNivel().getNivel());
			params.put("CATEGORIA",comprobante.getReserva().getHabitacion().getCategoria().getCategoria());
			params.put("DETALLES",comprobante.getReserva().getHabitacion().getDetalles());
			
			params.put("HUESPED",(comprobante.getReserva().getCliente().getNombres() + " " + comprobante.getReserva().getCliente().getApellidos()));
			params.put("NO_DOCUMENTO",comprobante.getReserva().getCliente().getNumeroDocumento());
			params.put("TELEFONO_CLIENTE",comprobante.getReserva().getCliente().getCelular());
			params.put("DIRECCION_CLIENTE",comprobante.getReserva().getCliente().getDireccion());
			params.put("TOTAL",String.valueOf(comprobante.getTotal()));
			
			params.put("ADICIONAL",String.valueOf(comprobante.getReserva().getPrecioAdicinal()));
			params.put("DESCUENTO",String.valueOf(comprobante.getReserva().getPrecioDescuento()));
			params.put("SUBTOTAL",String.valueOf(txtSubtotal.getText()));
			
			PrintReport obj = new PrintReport();
			obj.crearReporte("/reportes/rpComprobante.jasper",comprobanteDAO, params);
			
			salir();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void salir() {
		winConfirmarSalida.detach();
	}
	public List<Adicional> getListaServicios() {
		return listaServicios;
	}
	public void setListaServicios(List<Adicional> listaServicios) {
		this.listaServicios = listaServicios;
	}
	public Reserva getReserva() {
		return reserva;
	}
	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}
}
