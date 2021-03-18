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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.TipoDocumento;
import ec.com.hotel.modelo.TipoDocumentoDAO;


public class DocumentoLista {
	public String textoBuscar;

	public TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
	private List<TipoDocumento> listaDocumentos;

	@Wire Listbox lstDocumentos;

	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}


	@GlobalCommand("TipoDocumento.buscarPorPatron")
	@Command
	@NotifyChange({"listaDocumentos"})
	public void buscar(){
		if (listaDocumentos != null) {
			listaDocumentos = null; 
		}
		listaDocumentos = tipoDocumentoDAO.getTipoDocumentos(textoBuscar);

		if(listaDocumentos.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	@Command
	public void nuevo(){
		Window ventanaCargar = (Window) Executions.createComponents("/forms/configuracion/tipo_documento/documentoEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	/**
	 * Edita una persona
	 */
	@Command
	public void editar(@BindingParam("documento") TipoDocumento documentoSeleccionada){
		if(documentoSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		tipoDocumentoDAO.getEntityManager().refresh(documentoSeleccionada);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("TipoDocumento", documentoSeleccionada);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/configuracion/tipo_documento/documentoEditar.zul", null, params);
		ventanaCargar.doModal();
	}


	/**
	 * Borra "logicamente" un registro
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("documento") TipoDocumento documentoSeleccionada){

		if (documentoSeleccionada == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}

		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						tipoDocumentoDAO.getEntityManager().getTransaction().begin();
						documentoSeleccionada.setEstado("I");
						tipoDocumentoDAO.getEntityManager().merge(documentoSeleccionada);
						tipoDocumentoDAO.getEntityManager().getTransaction().commit();;
						// Actualiza la lista
						//BindUtils.postGlobalCommand(null, null, "Categoria.buscarPorPatron", null);
						buscar();
						Clients.showNotification("Transaccion ejecutada con exito.");
					} catch (Exception e) {
						e.printStackTrace();
						tipoDocumentoDAO.getEntityManager().getTransaction().rollback();
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
	public List<TipoDocumento> getListaDocumentos() {
		return listaDocumentos;
	}
	public void setListaDocumentos(List<TipoDocumento> listaDocumentos) {
		this.listaDocumentos = listaDocumentos;
	}
}
