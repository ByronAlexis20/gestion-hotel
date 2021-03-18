package ec.com.hotel.controlador.seguridad;

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

import ec.com.hotel.modelo.Perfil;
import ec.com.hotel.modelo.PerfilDAO;

public class PerfilEditar {
	@Wire private Window winPerfilEditar;
	@Wire private Textbox txtDescripcion;
	@Wire private Textbox txtPerfil;

	private PerfilDAO perfilDAO = new PerfilDAO();
	private Perfil perfil;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		perfil = (Perfil) Executions.getCurrent().getArg().get("Perfil");
	
		if (perfil == null) {
			perfil = new Perfil();
			perfil.setEstado("A");
		}
	}

	public boolean isValidarDatos() {
		try {
			Boolean retorna = true;
			if(txtPerfil.getText().isEmpty()) {
				Clients.showNotification("Obligatoria regitrar el Perfil","info",txtPerfil,"end_center",2000);
				txtPerfil.setFocus(true);
				return retorna;
			}
			if(txtDescripcion.getText().isEmpty()) {
				Clients.showNotification("Obligatoria regitrar la descripcion del Perfil","info",txtDescripcion,"end_center",2000);
				txtDescripcion.setFocus(true);
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
						perfilDAO.getEntityManager().getTransaction().begin();			
						if (perfil.getIdPerfil() == null) {
							perfilDAO.getEntityManager().persist(perfil);
						}else{
							perfil = (Perfil) perfilDAO.getEntityManager().merge(perfil);
						}			
						perfilDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						perfilDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}	

	@Command
	public void salir(){
		BindUtils.postGlobalCommand(null, null, "Perfil.buscarPorPatron", null);
		winPerfilEditar.detach();
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
}
