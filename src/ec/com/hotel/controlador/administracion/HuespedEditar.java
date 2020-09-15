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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ec.com.hotel.modelo.Cliente;
import ec.com.hotel.modelo.ClienteDAO;
import ec.com.hotel.modelo.TipoDocumento;
import ec.com.hotel.modelo.TipoDocumentoDAO;

public class HuespedEditar {
	@Wire private Window winHuespedEditar;
	@Wire private Textbox txtNombres;
	@Wire private Textbox txtApellidos;
	@Wire private Combobox cboTipoDocumento;	
	@Wire private Textbox txtNoDocumento;
	@Wire private Textbox txtCelular;
	@Wire private Textbox txtCorreo;
	@Wire private Textbox txtDirecion;
	
	TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
	
	ClienteDAO huespedDAO = new ClienteDAO();
	Cliente huesped;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		huesped = (Cliente) Executions.getCurrent().getArg().get("Huesped");
		if (huesped == null) {
			huesped = new Cliente();
			huesped.setEstado("A");
		}else {
			cboTipoDocumento.setText(huesped.getTipoDocumento().getTipoDocumento());
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
						huesped.setTipoDocumento((TipoDocumento)cboTipoDocumento.getSelectedItem().getValue());
						huespedDAO.getEntityManager().getTransaction().begin();			
						if (huesped.getIdCliente() == null) {
							huespedDAO.getEntityManager().persist(huesped);
						}else{
							huesped = (Cliente) huespedDAO.getEntityManager().merge(huesped);
						}			
						huespedDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Cliente.buscarPorPatron", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						huespedDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}	
	
	public boolean validarDatos() {
		if(txtNombres.getText() == "") {
			Clients.showNotification("Debe registrar nombre del huesped","info",txtNombres,"end_center",2000);
			txtNombres.setFocus(true);
			return false;
		}
		if(txtApellidos.getText() == "") {
			Clients.showNotification("Debe registrar apellidos del huesped","info",txtApellidos,"end_center",2000);
			txtApellidos.setFocus(true);
			return false;
		}
		if(cboTipoDocumento.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar tipo de documento","info",cboTipoDocumento,"end_center",2000);
			return false;
		}
		if(txtNoDocumento.getText() == "") {
			Clients.showNotification("Debe registrar el número de documento","info",txtNoDocumento,"end_center",2000);
			return false;
		}
		return true;
	}
	public List<TipoDocumento> getTiposDocumentos(){
		return tipoDocumentoDAO.getListaTiposDocumentosActivos();
	}

	@Command
	public void salir(){
		BindUtils.postGlobalCommand(null, null, "Cliente.buscarPorPatron", null);
		winHuespedEditar.detach();
	}
	public Cliente getHuesped() {
		return huesped;
	}
	public void setHuesped(Cliente huesped) {
		this.huesped = huesped;
	}
	
}
