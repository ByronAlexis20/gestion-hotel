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

import ec.com.hotel.modelo.Nivel;
import ec.com.hotel.modelo.NivelDAO;

public class NivelEditar {

	@Wire private Window winNivelEditar;
	@Wire private Textbox descripcion;

	private NivelDAO nivelDAO = new NivelDAO();
	private Nivel nivel;

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		nivel = (Nivel) Executions.getCurrent().getArg().get("Nivel");
		if (nivel == null) {
			nivel = new Nivel();
			nivel.setEstado("A");
		}
	}

	public boolean isValidarDatos() {
		try {
			Boolean retorna = true;
			if(descripcion.getText().isEmpty()) {
				Clients.showNotification("Obligatoria regitrar la descripcion del nivel","info",descripcion,"end_center",2000);
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
						nivelDAO.getEntityManager().getTransaction().begin();			
						if (nivel.getIdNivel() == null) {
							nivelDAO.getEntityManager().persist(nivel);
						}else{
							nivel = (Nivel) nivelDAO.getEntityManager().merge(nivel);
						}			
						nivelDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Nivel.buscarPorPatron", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						nivelDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}	

	@Command
	public void salir(){
		BindUtils.postGlobalCommand(null, null, "Nivel.buscarPorPatron", null);
		winNivelEditar.detach();
	}
	public Nivel getNivel() {
		return nivel;
	}
	public void setNivel(Nivel nivel) {
		this.nivel = nivel;
	}
}
