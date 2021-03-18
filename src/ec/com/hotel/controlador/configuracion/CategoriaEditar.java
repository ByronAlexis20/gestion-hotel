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

import ec.com.hotel.modelo.Categoria;
import ec.com.hotel.modelo.CategoriaDAO;

public class CategoriaEditar {

	@Wire private Window winCategoriaEditar;
	@Wire private Textbox descripcion;

	private CategoriaDAO categoriaDao = new CategoriaDAO();
	private Categoria categoria;

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		categoria = (Categoria) Executions.getCurrent().getArg().get("Categoria");
		if (categoria == null) {
			categoria = new Categoria();
			categoria.setEstado("A");
		}
	}

	public boolean isValidarDatos() {
		try {
			Boolean retorna = true;
			if(descripcion.getText().isEmpty()) {
				Clients.showNotification("Obligatoria regitrar la descripcion de la categoría","info",descripcion,"end_center",2000);
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
						categoriaDao.getEntityManager().getTransaction().begin();			
						if (categoria.getIdCategoria() == null) {
							categoriaDao.getEntityManager().persist(categoria);
						}else{
							categoria = (Categoria) categoriaDao.getEntityManager().merge(categoria);
						}			
						categoriaDao.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Categoria.buscarPorPatron", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						categoriaDao.getEntityManager().getTransaction().rollback();
					}
				}else {
					
				}
			}
		});
	}	

	@Command
	public void salir(){
		BindUtils.postGlobalCommand(null, null, "Categoria.buscarPorPatron", null);
		winCategoriaEditar.detach();
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
}
