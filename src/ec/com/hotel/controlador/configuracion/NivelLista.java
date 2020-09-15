package ec.com.hotel.controlador.configuracion;

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

import ec.com.hotel.modelo.Nivel;
import ec.com.hotel.modelo.NivelDAO;

public class NivelLista {
	public String textoBuscar;
	
	public NivelDAO nivelDAO = new NivelDAO();
	private List<Nivel> listaNiveles;

	@Wire Listbox lstNivel;

	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Nivel.buscarPorPatron")
	@Command
	@NotifyChange({"listaNiveles"})
	public void buscar(){
		if (listaNiveles != null) {
			listaNiveles = null; 
		}
		listaNiveles = nivelDAO.getNiveles(textoBuscar);
		lstNivel.setModel(new ListModelList(listaNiveles));
		if(listaNiveles.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}

	@Command
	public void nuevo(){
		//Map<String, Object> params = new HashMap<String, Object>();
		//params.put("Categoria", new Categoria());
		Window ventanaCargar = (Window) Executions.createComponents("/forms/configuracion/niveles/nivelEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	/**
	 * Edita una persona
	 */
	@Command
	public void editar(@BindingParam("nivel") Nivel nivelSeleccionada){
		if(nivelSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		nivelDAO.getEntityManager().refresh(nivelSeleccionada);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Nivel", nivelSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/configuracion/niveles/nivelEditar.zul", null, params);
		ventanaCargar.doModal();
	}


	/**
	 * Borra "logicamente" un registro
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("nivel") Nivel nivelSeleccionada){

		if (nivelSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}

		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						nivelDAO.getEntityManager().getTransaction().begin();
						nivelSeleccionada.setEstado("I");
						nivelDAO.getEntityManager().merge(nivelSeleccionada);
						nivelDAO.getEntityManager().getTransaction().commit();;
						// Actualiza la lista
						//BindUtils.postGlobalCommand(null, null, "Categoria.buscarPorPatron", null);
						buscar();
						Clients.showNotification("Transaccion ejecutada con exito.");
					} catch (Exception e) {
						e.printStackTrace();
						nivelDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});		
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	public List<Nivel> getListaNiveles() {
		return listaNiveles;
	}
	public void setListaNiveles(List<Nivel> listaNiveles) {
		this.listaNiveles = listaNiveles;
	}
}
