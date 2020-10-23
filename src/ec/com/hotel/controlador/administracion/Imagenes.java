package ec.com.hotel.controlador.administracion;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.Imagen;
import ec.com.hotel.modelo.ImagenDAO;
import ec.com.hotel.utils.Constantes;
import ec.com.hotel.utils.FileUtil;

public class Imagenes {
	@Wire Div dvImagenes;
	@Wire Window winImagenes;
	
	Habitacion habitacion;
	ImagenDAO imagenDAO = new ImagenDAO();
	
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		habitacion = (Habitacion) Executions.getCurrent().getArg().get("Habitacion");
		if(habitacion != null) {
			cargarImagenes();
		}
	}
	
	@GlobalCommand("Imagen.buscarPorhabitacion")
	@Command
	public void cargarImagenes() {
		try {
			dvImagenes.getChildren().clear();
			List<Imagen> imagenes = imagenDAO.obtenerImagenPorHabitacion(habitacion.getIdHabitacion(),Constantes.TIPO_IMAGEN_NORMAL);
			for(Imagen img : imagenes) {
				Image imagen = new Image();
				String pathAbsoluto = Executions.getCurrent()
						.getDesktop().getWebApp()
						.getRealPath("\\");
				String rutaImagen = pathAbsoluto + img.getRutaRelativa();
				
				rutaImagen = rutaImagen.replace("\\", "/");
				System.out.println(rutaImagen);
				AImage retorno = null;
				retorno = FileUtil.getImagenTamanoFijo(new AImage(rutaImagen), 100, -1);
				
				imagen.setContent(retorno);
				imagen.setStyle("margin-right: 10px;margin-bottom: 10px;");
				imagen.setWidth("200px");
				imagen.setWidth("175px");
				dvImagenes.getChildren().add(imagen);
			}
		}catch(Exception ex) {
		}
	}
	@Command
	public void agregarImagen() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Habitacion", habitacion);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/administracion/habitaciones/agregarImagen.zul", null, params);
		ventanaCargar.doModal();
	}
	@Command
	public void verPanoramicas() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Habitacion", habitacion);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/administracion/habitaciones/panoramicas.zul", null, params);
		ventanaCargar.doModal();
	}
	@Command
	public void salir() {
		winImagenes.detach();
	}

	public Habitacion getHabitacion() {
		return habitacion;
	}
	public void setHabitacion(Habitacion habitacion) {
		this.habitacion = habitacion;
	}
}
