package ec.com.hotel.controlador.salidas;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Adicional;
import ec.com.hotel.modelo.AdicionalDAO;
import ec.com.hotel.modelo.Reserva;
import ec.com.hotel.modelo.ReservaDAO;

public class ServiciosAdicionales {
	List<Adicional> listaServicios;
	@Wire Textbox txtNumeroHabitacion;
	@Wire Textbox txtCategoria;   
	@Wire Textbox txtDetalles;
	@Wire Textbox txtNivel;
	@Wire Textbox txtCliente;
	@Wire Listbox lstServicios;
	@Wire Window winServiciosAdicionales;
	
	Reserva reserva;
	ReservaDAO reservaDAO = new ReservaDAO();
	AdicionalDAO adicionalDAO = new AdicionalDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		reserva = (Reserva) Executions.getCurrent().getArg().get("Reserva");
		if(reserva != null) {
			cargarDatos();
			cargarAdicionales();
		}
			
	}
	private void cargarDatos() {
		txtNumeroHabitacion.setText(reserva.getHabitacion().getNumero());
		txtCategoria.setText(reserva.getHabitacion().getCategoria().getCategoria());   
		txtDetalles.setText(reserva.getHabitacion().getDetalles());
		txtNivel.setText(reserva.getHabitacion().getNivel().getNivel());
		txtCliente.setText("No. Doc.: " + reserva.getCliente().getNumeroDocumento() + " - " + reserva.getCliente().getNombres() + " " + reserva.getCliente().getApellidos());
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
	public void agregarServicio(){
		if(reserva == null) {
			Clients.showNotification("Debe seleccionar una reserva");
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Reserva", reserva);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/servicios_salidas/servicios/servicios.zul", null, params);
		ventanaCargar.doModal();
	}
	@Command
	public void salir() {
		winServiciosAdicionales.detach();
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
