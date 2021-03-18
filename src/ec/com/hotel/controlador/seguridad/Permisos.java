package ec.com.hotel.controlador.seguridad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

import ec.com.hotel.modelo.Menu;
import ec.com.hotel.modelo.MenuDAO;
import ec.com.hotel.modelo.Perfil;
import ec.com.hotel.modelo.PerfilDAO;
import ec.com.hotel.modelo.Permiso;
import ec.com.hotel.modelo.PermisoDAO;


public class Permisos {
	PerfilDAO perfilDAO = new PerfilDAO();
	@Wire private Combobox cboPerfil;
	MenuDAO menuDAO = new MenuDAO();
	PermisoDAO permisoDAO = new PermisoDAO();
	Perfil perfilSeleccionado;
	
	@Wire private Listbox lstPermisosTodos;
	@Wire private Listbox lstPermisoPerfil;

	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		cargarMenuDisponibles();
	}
	
	@Command
	public void agregar() {
		if(cboPerfil.getSelectedItem() == null) {
			Clients.showNotification("Debe seleccionar un perfil");
			return;
		}
		if(lstPermisosTodos.getSelectedItem().getValue() == null){
			Clients.showNotification("Debe seleccionar un menu para agreagar");
			return;
		}
		Perfil perfilSeleccionado = (Perfil)cboPerfil.getSelectedItem().getValue();
		Menu menuSeleccionado = (Menu)lstPermisosTodos.getSelectedItem().getValue();
		//grabar 

		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					perfilDAO.getEntityManager().getTransaction().begin();
					Permiso permisoGrabar = new Permiso();
					permisoGrabar.setIdPermiso(null);
					permisoGrabar.setEstado("A");
					permisoGrabar.setMenu(menuSeleccionado);
					permisoGrabar.setPerfil(perfilSeleccionado);
					perfilDAO.getEntityManager().persist(permisoGrabar);
					perfilDAO.getEntityManager().getTransaction().commit();
					Messagebox.show("Datos grabados con exito");
					cargarPermisosPerfil(perfilSeleccionado.getIdPerfil());
				}
			}
		};
		Messagebox.show("Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
	}
	
	@Command
    public void cambioPerfil() {
		if(cboPerfil.getSelectedItem() == null) {
			Clients.showNotification("Debe seleccionar un perfil");
			return;
		}
		Perfil perfilSeleccionado = (Perfil)cboPerfil.getSelectedItem().getValue();
		cargarPermisosPerfil(perfilSeleccionado.getIdPerfil());
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void cargarPermisosPerfil(Integer idPerfil) {
		try {
			List<Permiso> listaPermisos = permisoDAO.getListaPermisosHijo(idPerfil);
			for(Permiso per : listaPermisos) {
				List<Menu> listadoPadre = menuDAO.getMenuPadre(per.getMenu().getIdMenuPadre());
				if(listadoPadre.size() > 0) {						
					per.getMenu().setPresentacion(listadoPadre.get(0).getDescripcion() + " --> " + per.getMenu().getDescripcion());
				}
			}
			lstPermisoPerfil.setModel(new ListModelList(listaPermisos));
			boolean bandera = false;
			//tambien se deben cargar los menu que quedan
			List<Menu> listaMenuDisponibles = menuDAO.getListaHijos();
			List<Menu> listaSobrentes = new ArrayList<>();
			for(Menu menu : listaMenuDisponibles) {
				bandera = false;
				for(Permiso per : listaPermisos) {
					if(menu.getIdMenu() == per.getMenu().getIdMenu()) {
						bandera = true;
					}
				}
				//no eciste
				if(bandera == false) {
					listaSobrentes.add(menu);
				}
			}
			
			if(listaSobrentes.size() > 0) {
				for(Menu mnu : listaSobrentes) {
					List<Menu> listadoPadre = menuDAO.getMenuPadre(mnu.getIdMenuPadre());
					if(listadoPadre.size() > 0) {						
						mnu.setPresentacion(listadoPadre.get(0).getDescripcion() + " --> " + mnu.getDescripcion());
					}
				}
			}
			lstPermisosTodos.setModel(new ListModelList(listaSobrentes));
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	@Command
	public void quitar() {
		if(cboPerfil.getSelectedItem() == null) {
			Clients.showNotification("Debe seleccionar un perfil");
			return;
		}
		if(lstPermisoPerfil.getSelectedItem().getValue() == null){
			Clients.showNotification("Debe seleccionar un menu para quitar");
			return;
		}
		Perfil perfilSeleccionado = (Perfil)cboPerfil.getSelectedItem().getValue();
		Permiso menuSeleccionado = (Permiso)lstPermisoPerfil.getSelectedItem().getValue();
		
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					perfilDAO.getEntityManager().getTransaction().begin();
					Permiso permisoGrabar = menuSeleccionado;
					permisoGrabar.setEstado("I");
					perfilDAO.getEntityManager().merge(permisoGrabar);
					perfilDAO.getEntityManager().getTransaction().commit();
					Messagebox.show("Menu Quitado con exito");
					cargarPermisosPerfil(perfilSeleccionado.getIdPerfil());
				}
			}
		};
		Messagebox.show("Desea quitar el menu?", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void cargarMenuDisponibles() {
		try {
			List<Menu> listaMenuDisponibles = menuDAO.getListaHijos();
			if(listaMenuDisponibles.size() > 0) {
				for(Menu mnu : listaMenuDisponibles) {
					List<Menu> listadoPadre = menuDAO.getMenuPadre(mnu.getIdMenuPadre());
					if(listadoPadre.size() > 0) {						
						mnu.setPresentacion(listadoPadre.get(0).getDescripcion() + " --> " + mnu.getDescripcion());
					}
				}
			}
			lstPermisosTodos.setModel(new ListModelList(listaMenuDisponibles));
		}catch(Exception ex) {

		}
	}
	public List<Perfil> getPerfiles(){
		return perfilDAO.getPerfilesPorDescripcion("");
	}

	public Perfil getPerfilSeleccionado() {
		return perfilSeleccionado;
	}

	public void setPerfilSeleccionado(Perfil perfilSeleccionado) {
		this.perfilSeleccionado = perfilSeleccionado;
	}
	
}
