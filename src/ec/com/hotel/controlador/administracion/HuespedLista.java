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

import ec.com.hotel.modelo.Cliente;
import ec.com.hotel.modelo.ClienteDAO;



public class HuespedLista {
	
	public String textoBuscar;
	ClienteDAO clienteDAO = new ClienteDAO();
	List<Cliente> clienteLista;
	@Wire private Listbox lstHuespedes;
	
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Cliente.buscarPorPatron")
	@Command
	@NotifyChange({"clienteLista"})
	public void buscar(){
		if (clienteLista != null) {
			clienteLista = null; 
		}
		clienteLista = clienteDAO.getListaClientesBuscar(textoBuscar);
		lstHuespedes.setModel(new ListModelList(clienteLista));
		if(clienteLista.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	@Command
	public void nuevo(){
		Window ventanaCargar = (Window) Executions.createComponents("/forms/administracion/huespedes/huespedEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	@Command
	public void editar(@BindingParam("huesped") Cliente huespedSeleccionada){
		if(huespedSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		clienteDAO.getEntityManager().refresh(huespedSeleccionada);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Huesped", huespedSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/administracion/huespedes/huespedEditar.zul", null, params);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("huesped") Cliente huespedSeleccionada){

		if (huespedSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}

		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						clienteDAO.getEntityManager().getTransaction().begin();
						huespedSeleccionada.setEstado("I");
						clienteDAO.getEntityManager().merge(huespedSeleccionada);
						clienteDAO.getEntityManager().getTransaction().commit();;
						// Actualiza la lista
						//BindUtils.postGlobalCommand(null, null, "Categoria.buscarPorPatron", null);
						buscar();
						Clients.showNotification("Transaccion ejecutada con exito.");
					} catch (Exception e) {
						e.printStackTrace();
						clienteDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});		
	}

	public List<Cliente> getClienteLista() {
		return clienteLista;
	}
	public void setClienteLista(List<Cliente> clienteLista) {
		this.clienteLista = clienteLista;
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	
}
