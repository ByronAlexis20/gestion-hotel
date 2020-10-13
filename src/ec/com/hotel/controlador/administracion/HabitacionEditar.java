package ec.com.hotel.controlador.administracion;

import java.util.List;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Categoria;
import ec.com.hotel.modelo.CategoriaDAO;
import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.HabitacionDAO;
import ec.com.hotel.modelo.Nivel;
import ec.com.hotel.modelo.NivelDAO;
import ec.com.hotel.utils.Constantes;

public class HabitacionEditar {
	@Wire private Window winHabitacionEditar;
	@Wire private Combobox cboNivel;	
	@Wire private Combobox cboCategoria;
	@Wire private Textbox txtNumHabitacion;
	@Wire private Textbox txtNumCamas;
	@Wire private Textbox txtDetalles;
	@Wire private Doublebox txtPrecio;
	
	NivelDAO nivelDAO = new NivelDAO();
	CategoriaDAO categoriaDAO = new CategoriaDAO();
	
	HabitacionDAO habitacionDAO = new HabitacionDAO();
	Habitacion habitacion;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		habitacion = (Habitacion) Executions.getCurrent().getArg().get("Habitacion");
		if (habitacion == null) {
			habitacion = new Habitacion();
			habitacion.setEstado("A");
		}else {
			cboCategoria.setText(habitacion.getCategoria().getCategoria());
			cboNivel.setText(habitacion.getNivel().getNivel());
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void grabar(){
		if(validarDatos() == false) {
			return;
		}
		Messagebox.show("Desea guardar el registro?", "Confirmación de Guardar", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {		
					try {						
						habitacion.setCategoria((Categoria)cboCategoria.getSelectedItem().getValue());
						habitacion.setNivel((Nivel)cboNivel.getSelectedItem().getValue());
						habitacion.setEstadoReserva(Constantes.HABITACION_DISPONIBLE);
						
						habitacionDAO.getEntityManager().getTransaction().begin();			
						if (habitacion.getIdHabitacion() == null) {
							habitacionDAO.getEntityManager().persist(habitacion);
						}else{
							habitacion = (Habitacion) habitacionDAO.getEntityManager().merge(habitacion);
						}			
						habitacionDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Habitacion.buscarPorPatron", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						nivelDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}	
	
	
	public boolean validarDatos() {
		if(txtNumHabitacion.getText() == "") {
			Clients.showNotification("Debe registrar el número de la habitación","info",txtNumHabitacion,"end_center",2000);
			txtNumHabitacion.setFocus(true);
			return false;
		}
		if(cboNivel.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar nivel","info",cboNivel,"end_center",2000);
			return false;
		}
		if(cboCategoria.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar categoría","info",cboCategoria,"end_center",2000);
			return false;
		}
		if(txtNumCamas.getText() == "") {
			Clients.showNotification("Debe registrar el número de camas","info",txtNumCamas,"end_center",2000);
			txtNumCamas.setFocus(true);
			return false;
		}
		if(txtDetalles.getText() == "") {
			Clients.showNotification("Debe registrar detalles de la habitación","info",txtDetalles,"end_center",2000);
			txtDetalles.setFocus(true);
			return false;
		}
		if(txtPrecio.getText() == "") {
			Clients.showNotification("Debe registrar el precio de la habitación","info",txtPrecio,"end_center",2000);
			txtPrecio.setFocus(true);
			return false;
		}
		return true;
	}
	@Command
	public void salir(){
		BindUtils.postGlobalCommand(null, null, "Habitacion.buscarPorPatron", null);
		winHabitacionEditar.detach();
	}
	
	public List<Nivel> getNiveles(){
		return nivelDAO.getListaNivelesActivos();
	}
	public List<Categoria> getCategorias(){
		return categoriaDAO.getListaCategoriasActivos();
	}
	public Habitacion getHabitacion() {
		return habitacion;
	}
	public void setHabitacion(Habitacion habitacion) {
		this.habitacion = habitacion;
	}
	
}
