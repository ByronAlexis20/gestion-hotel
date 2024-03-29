package ec.com.hotel.controlador.recepcion;

import java.io.IOException;
import java.util.List;

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

import ec.com.hotel.modelo.Cliente;
import ec.com.hotel.modelo.ClienteDAO;
import ec.com.hotel.modelo.Reserva;
import ec.com.hotel.modelo.ReservaDAO;
import ec.com.hotel.modelo.Usuario;
import ec.com.hotel.modelo.UsuarioDAO;
import ec.com.hotel.utils.SecurityUtil;

public class RealizarReserva {
	@Wire Listbox lstReserva;
	
	List<Reserva> listaReserva;
	ReservaDAO reservaDAO = new ReservaDAO();
	Cliente cliente;
	ClienteDAO clienteDAO = new ClienteDAO();
	UsuarioDAO usuarioDAO = new UsuarioDAO();
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		cargarDatosCliente();
		buscar();
	}
	
	private void cargarDatosCliente() {
		Usuario usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername());
		List<Cliente> cliList = clienteDAO.getListaClientesBuscarCedula(usuario.getDocumento());
		if(cliList.size() > 0)
			cliente = cliList.get(0);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Reserva.buscarPendientes")
	@Command
	@NotifyChange({"listaReserva"})
	public void buscar(){
		if (listaReserva != null) {
			listaReserva = null; 
		}
		listaReserva = reservaDAO.getReservasPendientesPorCliente(cliente.getIdCliente());
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
	public void nuevo(){
		//Map<String, Object> params = new HashMap<String, Object>();
		//params.put("Categoria", new Categoria());
		Window ventanaCargar = (Window) Executions.createComponents("/forms/reservas_recepcion/reservas/cliente/seleccionarHabitacion.zul", null, null);
		ventanaCargar.doModal();
	}
	public List<Reserva> getListaReserva() {
		return listaReserva;
	}
	public void setListaReserva(List<Reserva> listaReserva) {
		this.listaReserva = listaReserva;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
}
