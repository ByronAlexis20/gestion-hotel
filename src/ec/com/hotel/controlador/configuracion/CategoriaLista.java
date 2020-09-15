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

import ec.com.hotel.modelo.Categoria;
import ec.com.hotel.modelo.CategoriaDAO;

public class CategoriaLista {
	public String textoBuscar;
	
	public CategoriaDAO categoriaDao = new CategoriaDAO();
	private List<Categoria> categoria;

	@Wire Listbox lstCategoria;

	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Categoria.buscarPorPatron")
	@Command
	@NotifyChange({"categoria"})
	public void buscar(){
		if (categoria != null) {
			categoria = null; 
		}
		categoria = categoriaDao.getCategorias(textoBuscar);
		lstCategoria.setModel(new ListModelList(categoria));
		if(categoria.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}

	@Command
	public void nuevo(){
		//Map<String, Object> params = new HashMap<String, Object>();
		//params.put("Categoria", new Categoria());
		Window ventanaCargar = (Window) Executions.createComponents("/forms/configuracion/categoria/categoriaEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	
	@Command
	public void editar(@BindingParam("categoria") Categoria categoriaSeleccionada){
		if(categoriaSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		categoriaDao.getEntityManager().refresh(categoriaSeleccionada);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Categoria", categoriaSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/configuracion/categoria/categoriaEditar.zul", null, params);
		ventanaCargar.doModal();
	}


	/**
	 * Borra "logicamente" un registro
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("categoria") Categoria categoriaSeleccionada){

		if (categoriaSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}

		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						categoriaDao.getEntityManager().getTransaction().begin();
						categoriaSeleccionada.setEstado("I");
						categoriaDao.getEntityManager().merge(categoriaSeleccionada);
						categoriaDao.getEntityManager().getTransaction().commit();;
						// Actualiza la lista
						//BindUtils.postGlobalCommand(null, null, "Categoria.buscarPorPatron", null);
						buscar();
						Clients.showNotification("Transaccion ejecutada con exito.");
					} catch (Exception e) {
						e.printStackTrace();
						categoriaDao.getEntityManager().getTransaction().rollback();
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

	public List<Categoria> getCategoria() {
		return categoria;
	}

	public void setCategoria(List<Categoria> categoria) {
		this.categoria = categoria;
	}
}
