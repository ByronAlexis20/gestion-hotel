package ec.com.hotel.controlador.administracion;

import java.io.IOException;

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

import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.Imagen;
import ec.com.hotel.modelo.ImagenDAO;
import ec.com.hotel.utils.Constantes;

public class AgregarPanoramica {
	@Wire Window winAgregarPanoramicas;
	@Wire Textbox txtNombre;
	@Wire Textbox txtFuente;
	
	Habitacion habitacion;
	Imagen imagen;
	ImagenDAO imagenDAO = new ImagenDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		habitacion = (Habitacion) Executions.getCurrent().getArg().get("Habitacion");
		imagen = new Imagen();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void grabar() {
		if(txtNombre.getText().isEmpty()) {
			Clients.showNotification("Debe registrar el Nombre de la Imagen","info",txtNombre,"end_center",2000);
			txtNombre.setFocus(true);
			return;
		}
		if(txtFuente.getText().isEmpty()) {
			Clients.showNotification("Debe registrar la fuente de origen de la imagen panoramica","info",txtFuente,"end_center",2000);
			txtFuente.setFocus(true);
			return;
		}
		
		Messagebox.show("Desea guardar el registro?", "Confirmación de Guardar", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {		
					try {	
						imagen.setHabitacion(habitacion);
						imagen.setEstado("A");
						imagen.setFuentePanoramica(txtFuente.getText().toString());
						imagen.setIdImagen(null);
						imagen.setNombreImagenPanoramica(txtNombre.getText().toString());
						imagen.setTipoImagen(Constantes.TIPO_IMAGEN_PANORAMICA);
						imagenDAO.getEntityManager().getTransaction().begin();
						imagenDAO.getEntityManager().persist(imagen);
						imagenDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Imagen.buscarPorhabitacion", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						imagenDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}
	
	@Command
	public void salir() {
		winAgregarPanoramicas.detach();
	}
	public Habitacion getHabitacion() {
		return habitacion;
	}

	public void setHabitacion(Habitacion habitacion) {
		this.habitacion = habitacion;
	}

	public Imagen getImagen() {
		return imagen;
	}

	public void setImagen(Imagen imagen) {
		this.imagen = imagen;
	}
	
}
