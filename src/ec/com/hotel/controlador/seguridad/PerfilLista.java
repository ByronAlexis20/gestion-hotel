package ec.com.hotel.controlador.seguridad;

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

import ec.com.hotel.modelo.Perfil;
import ec.com.hotel.modelo.PerfilDAO;

public class PerfilLista {

	public String textoBuscar;
	PerfilDAO perfilDAO = new PerfilDAO();
	List<Perfil> perfilLista;
	@Wire private Listbox lstPerfiles;
	
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Perfil.buscarPorPatron")
	@Command
	@NotifyChange({"perfilLista"})
	public void buscar(){
		if (perfilLista != null) {
			perfilLista = null; 
		}
		perfilLista = perfilDAO.getPerfilesPorDescripcion(textoBuscar);
		lstPerfiles.setModel(new ListModelList(perfilLista));
		if(perfilLista.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	@Command
	public void nuevo(){
		Window ventanaCargar = (Window) Executions.createComponents("/forms/seguridad/perfiles/perfilEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	@Command
	public void editar(@BindingParam("perfil") Perfil perfilSeleccionado){
		if(perfilSeleccionado == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		perfilDAO.getEntityManager().refresh(perfilSeleccionado);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Perfil", perfilSeleccionado);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/seguridad/perfiles/perfilEditar.zul", null, params);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("perfil") Perfil perfilSeleccionado){

		if (perfilSeleccionado == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}

		Messagebox.show("Desea dar de baja el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						perfilDAO.getEntityManager().getTransaction().begin();
						perfilSeleccionado.setEstado("I");
						perfilDAO.getEntityManager().merge(perfilSeleccionado);
						perfilDAO.getEntityManager().getTransaction().commit();;
						// Actualiza la lista
						//BindUtils.postGlobalCommand(null, null, "Categoria.buscarPorPatron", null);
						buscar();
						Clients.showNotification("Transaccion ejecutada con exito.");
					} catch (Exception e) {
						e.printStackTrace();
						perfilDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});		
	}

	
	public List<Perfil> getPerfilLista() {
		return perfilLista;
	}
	public void setPerfilLista(List<Perfil> perfilLista) {
		this.perfilLista = perfilLista;
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}

}
