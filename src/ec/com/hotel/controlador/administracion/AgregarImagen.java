package ec.com.hotel.controlador.administracion;

import java.io.IOException;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.Imagen;
import ec.com.hotel.modelo.ImagenDAO;
import ec.com.hotel.utils.FileUtil;

public class AgregarImagen {
	@Wire Window winAgregarImagen;
	
	Habitacion habitacion;
	Imagen imagen;
	ImagenDAO imagenDAO = new ImagenDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		habitacion = (Habitacion) Executions.getCurrent().getArg().get("Habitacion");
		imagen = new Imagen();
	}
	@Command
	@NotifyChange("imagenHabitacion")
	public void subir(@ContextParam(ContextType.BIND_CONTEXT) BindContext contexto) {
		String pathRetornado; 
		UploadEvent eventoCarga = (UploadEvent) contexto.getTriggerEvent();
		pathRetornado = FileUtil.cargaArchivo(eventoCarga.getMedia());
		String rutaRelativa = FileUtil.rutaArchivo(eventoCarga.getMedia());
		imagen.setRutaImagen(pathRetornado);
		imagen.setRutaRelativa(rutaRelativa);
	}
	
	
	
	public AImage getImagenHabitacion() {
		AImage retorno = null;
		if (imagen.getRutaImagen() != null) {
			try {
				retorno = FileUtil.getImagenTamanoFijo(new AImage(imagen.getRutaImagen()), 100, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retorno; 
	}
	@Command
	public void grabar() {
		try {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						// Inicia la transaccion
						imagenDAO.getEntityManager().getTransaction().begin();
						imagen.setHabitacion(habitacion);
						imagen.setEstado("A");
						imagen.setIdImagen(null);
						imagenDAO.getEntityManager().persist(imagen);
						// Cierra la transaccion.
						imagenDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Imagen.buscarPorhabitacion", null);
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		} catch (Exception e) {
			e.printStackTrace();
			// Si hay error, reversa la transaccion.
			imagenDAO.getEntityManager().getTransaction().rollback();
		}
	}
	@Command
	public void salir() {
		winAgregarImagen.detach();
	}
	public Imagen getImagen() {
		return imagen;
	}
	public void setImagen(Imagen imagen) {
		this.imagen = imagen;
	}
}
