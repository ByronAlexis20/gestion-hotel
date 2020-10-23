package ec.com.hotel.controlador.administracion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.Imagen;
import ec.com.hotel.modelo.ImagenDAO;
import ec.com.hotel.utils.Constantes;

public class Panoramicas {
	@Wire Window winPanoramicas;
	@Wire Tabbox tbImagenesPanoramicas;
	
	Habitacion habitacion;
	ImagenDAO imagenDAO = new ImagenDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		habitacion = (Habitacion) Executions.getCurrent().getArg().get("Habitacion");
		cargarImagenes();
	}
	@Command
	public void agregarImagen() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Habitacion", habitacion);
		Window ventanaCargar = (Window) Executions.createComponents("/forms/administracion/habitaciones/agregarPanoramica.zul", null, params);
		ventanaCargar.doModal();
	}
	@GlobalCommand("Imagen.buscarPorhabitacion")
	@Command
	public void cargarImagenes() {
		if(tbImagenesPanoramicas.getTabs() == null) {
			tbImagenesPanoramicas.appendChild(new Tabs());   
			tbImagenesPanoramicas.appendChild(new Tabpanels());			
		}else {
			tbImagenesPanoramicas.getTabs().getChildren().clear();
			tbImagenesPanoramicas.getTabpanels().getChildren().clear();
		}
		List<Imagen> panoramicas = new ArrayList<>();
		panoramicas = imagenDAO.obtenerImagenPorHabitacion(habitacion.getIdHabitacion(), Constantes.TIPO_IMAGEN_PANORAMICA);
		if(panoramicas.size() > 0) {
			for(Imagen img : panoramicas) {
				Tab tabImagen = new Tab(img.getNombreImagenPanoramica());
				Tabpanel panelImagen = new Tabpanel();
				Iframe frameImage = new Iframe();
				frameImage.setSrc(img.getFuentePanoramica());
				frameImage.setHflex("1");
				frameImage.setVflex("1");
				frameImage.setStyle("border:none;");
				panelImagen.getChildren().add(frameImage);
				tbImagenesPanoramicas.getTabs().appendChild(tabImagen);
				tbImagenesPanoramicas.getTabpanels().appendChild(panelImagen);
			}
		}
	}
	@Command 
	public void salir() {
		winPanoramicas.detach();
	}
	
	public Habitacion getHabitacion() {
		return habitacion;
	}
	public void setHabitacion(Habitacion habitacion) {
		this.habitacion = habitacion;
	}

}
