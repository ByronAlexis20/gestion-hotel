package ec.com.hotel.controlador.recepcion;

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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Habitacion;
import ec.com.hotel.modelo.HabitacionDAO;
import ec.com.hotel.modelo.Imagen;
import ec.com.hotel.modelo.ImagenDAO;
import ec.com.hotel.modelo.ReservaDAO;
import ec.com.hotel.utils.Constantes;

public class ReservarPorImagen {
	@Wire Tabbox tbHabitaciones;
	ImagenDAO imagenDAO = new ImagenDAO();
	HabitacionDAO habitacionDAO = new HabitacionDAO();	
	ReservaDAO reservaDAO = new ReservaDAO();
	
	@Wire Datebox dtpFecha;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		//cargarImagenes();
	}
	@GlobalCommand("Habitacion.buscarPorPatron")
	@Command
	public void cargarImagenes() {
		if(dtpFecha.getValue() == null) {
			Clients.showNotification("Debe seleccionar la fecha");
			return;
		}
		if(tbHabitaciones.getTabs() == null) {
			tbHabitaciones.appendChild(new Tabs());   
			tbHabitaciones.appendChild(new Tabpanels());			
		}else {
			tbHabitaciones.getTabs().getChildren().clear();
			tbHabitaciones.getTabpanels().getChildren().clear();
		}
		//primero se crea los tabs de las habitaciones
		List<Habitacion> habitaciones = new ArrayList<>();
		
		//primero busco las habitaciones activas
		List<Habitacion> habitacionesActivas = new ArrayList<>();
		habitacionesActivas = habitacionDAO.getListaHabitaciones("");
		List<Habitacion> habitacionesDisponibles = new ArrayList<>();
		for(Habitacion hab : habitacionesActivas) {
			//si es mayor a cero.. quiere decir que si se encuentra reservada
			//si es menor o igual a cero. la habitacion se encuentra disponible
			if(reservaDAO.getConsultarDisponibilidad(hab.getIdHabitacion(), dtpFecha.getValue()).size() <= 0) {
				habitacionesDisponibles.add(hab);
			}
		}
		habitaciones = habitacionesDisponibles;
		
		for(Habitacion hab : habitaciones) {
			Tab tabHabitacion = new Tab(hab.getNumero());
			tabHabitacion.setHflex("1");
			tabHabitacion.setVflex("1");
			Tabpanel panel = new Tabpanel();
			panel.setHflex("1");
			panel.setVflex("1");
			
			
			Label labelHabitacion = new Label("Imagenes Panorámicas Habitacion No: " + hab.getNumero() + " Estado: " + hab.getEstadoReserva());
			labelHabitacion.setHflex("1");
			labelHabitacion.setStyle("font-size:17px;font-weight: bold;");
			
			Button btnReservar = new Button();
			btnReservar.setLabel("Reservar");
			
			btnReservar.addEventListener("onClick", new EventListener<Event>() {
				@Override
				public void onEvent(Event arg0) throws Exception {
					if(!hab.getEstadoReserva().equals(Constantes.HABITACION_DISPONIBLE)) {
						Clients.showNotification("habitacion NO DISPONIBLE!");
						return;
					}
					//si esta disponible
					
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("Habitacion", hab);
					params.put("Fecha", dtpFecha.getValue());
					Window ventanaCargar = (Window) Executions.createComponents("/forms/reservas_recepcion/reservas/cliente/reservarHabitacion.zul", null, params);
					ventanaCargar.doModal();
				}
			});
			
			panel.getChildren().add(labelHabitacion);
			panel.getChildren().add(btnReservar);

			
			//aqui dentro se crea el tab para las imagenes
			Tabbox tabboxImagen = new Tabbox();
			if(tabboxImagen.getTabs() == null) {
				tabboxImagen.appendChild(new Tabs());   
				tabboxImagen.appendChild(new Tabpanels());			
			}else {
				tabboxImagen.getTabs().getChildren().clear();
				tabboxImagen.getTabpanels().getChildren().clear();
			}
			tabboxImagen.setHeight("100%");
			tabboxImagen.setVflex("1");
			List<Imagen> imagenes = new ArrayList<>();
			imagenes = imagenDAO.obtenerImagenPorHabitacion(hab.getIdHabitacion(), Constantes.TIPO_IMAGEN_PANORAMICA);
			if(imagenes.size() > 0) {
				for(Imagen img : imagenes) {
					Tab tabImagen = new Tab(img.getNombreImagenPanoramica());
					tabImagen.setHflex("1");
					tabImagen.setVflex("1");
					Tabpanel panelImagen = new Tabpanel();
					panelImagen.setHflex("1");
					panelImagen.setVflex("1");
					Iframe frameImage = new Iframe();
					frameImage.setSrc(img.getFuentePanoramica());
					frameImage.setHflex("1");
					frameImage.setVflex("1");
					frameImage.setStyle("border:none;");
					panelImagen.getChildren().add(frameImage);
					//panelImagen.getChildren().add(btn);
					tabboxImagen.getTabs().appendChild(tabImagen);
					tabboxImagen.getTabpanels().appendChild(panelImagen);
				}
			}
			
			panel.getChildren().add(tabboxImagen);
			
			//panelImagen.getChildren().add(btn);
			tbHabitaciones.getTabs().appendChild(tabHabitacion);
			tbHabitaciones.getTabpanels().appendChild(panel);
		}
		
	}
}
