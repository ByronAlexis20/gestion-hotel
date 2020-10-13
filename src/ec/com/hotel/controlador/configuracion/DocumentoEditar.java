package ec.com.hotel.controlador.configuracion;

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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.TipoDocumento;
import ec.com.hotel.modelo.TipoDocumentoDAO;

public class DocumentoEditar {
	@Wire private Window winDocumentoEditar;
	@Wire private Textbox descripcion;
	@Wire private Textbox digitos;

	private TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
	private TipoDocumento tipoDocumento;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		tipoDocumento = (TipoDocumento) Executions.getCurrent().getArg().get("TipoDocumento");
	
		if (tipoDocumento == null) {
			tipoDocumento = new TipoDocumento();
			tipoDocumento.setEstado("A");
		}
	}

	public boolean isValidarDatos() {
		try {
			Boolean retorna = true;
			if(descripcion.getText().isEmpty()) {
				Clients.showNotification("Obligatoria regitrar la descripcion del tipo de documento","info",descripcion,"end_center",2000);
				return retorna;
			}			
			if(digitos.getText().isEmpty()) {
				Clients.showNotification("Obligatoria regitrar la cantidad de dígitos","info",digitos,"end_center",2000);
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
						tipoDocumentoDAO.getEntityManager().getTransaction().begin();			
						if (tipoDocumento.getIdTipoDocumento() == null) {
							tipoDocumentoDAO.getEntityManager().persist(tipoDocumento);
						}else{
							tipoDocumento = (TipoDocumento) tipoDocumentoDAO.getEntityManager().merge(tipoDocumento);
						}			
						tipoDocumentoDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						tipoDocumentoDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}	

	@Command
	public void salir(){
		BindUtils.postGlobalCommand(null, null, "TipoDocumento.buscarPorPatron", null);
		winDocumentoEditar.detach();
	}

	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

}
