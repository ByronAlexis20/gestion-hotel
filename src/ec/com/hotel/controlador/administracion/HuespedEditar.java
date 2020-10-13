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
import ec.com.hotel.utils.ControllerHelper;

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
	TipoDocumento documentoSeleccionado;
	ControllerHelper helper = new ControllerHelper();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		huesped = (Cliente) Executions.getCurrent().getArg().get("Huesped");
		if (huesped == null) {
			huesped = new Cliente();
			huesped.setEstado("A");
			txtNoDocumento.setDisabled(true);
			txtNombres.setDisabled(true);
			txtApellidos.setDisabled(true);
			txtCelular.setDisabled(true);
			txtCorreo.setDisabled(true);
			txtDirecion.setDisabled(true);
			documentoSeleccionado = null;
		}else {
			documentoSeleccionado = huesped.getTipoDocumento();
			cboTipoDocumento.setText(huesped.getTipoDocumento().getTipoDocumento());
		}
	}
	
	@Command
	public void cambiarTipoDocumento() {
		if(documentoSeleccionado != null) {
			txtNoDocumento.setDisabled(false);
			txtNoDocumento.setText("");
			txtNoDocumento.setMaxlength(documentoSeleccionado.getDigitos());
			txtNombres.setDisabled(false);
			txtApellidos.setDisabled(false);
			txtCelular.setDisabled(false);
			txtCorreo.setDisabled(false);
			txtDirecion.setDisabled(false);
		}else {
			txtNoDocumento.setDisabled(true);
			txtNoDocumento.setText("");
			txtNombres.setDisabled(true);
			txtApellidos.setDisabled(true);
			txtCelular.setDisabled(true);
			txtCorreo.setDisabled(true);
			txtDirecion.setDisabled(true);
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
		if(cboTipoDocumento.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar tipo de documento","info",cboTipoDocumento,"end_center",2000);
			return false;
		}
		if(txtNoDocumento.getText().isEmpty()) {
			Clients.showNotification("Debe registrar el número de documento","info",txtNoDocumento,"end_center",2000);
			txtNoDocumento.focus();
			return false;
		}
		//validar el tipo de documento
		if(documentoSeleccionado.getDigitos() == 10) {// es una cedula
			if(txtNoDocumento.getText().length() < documentoSeleccionado.getDigitos()) {
				Clients.showNotification("El número de cédula no tiene la cantidad de digitos obligatorios [10]","info",txtNoDocumento,"end_center",2000);
				txtNoDocumento.focus();
				return false;
			}
			if(!helper.validarDeCedula(txtNoDocumento.getText())) {
				Clients.showNotification("Número de CÉDULA NO VÁLIDA!","info",txtNoDocumento,"end_center",2000);
				txtNoDocumento.focus();
				return false;
			}
		}else if(documentoSeleccionado.getDigitos() == 13) {// es un ruc
			if(txtNoDocumento.getText().length() < documentoSeleccionado.getDigitos()) {
				Clients.showNotification("El número de ruc no tiene la cantidad de digitos obligatorios [13]","info",txtNoDocumento,"end_center",2000);
				txtNoDocumento.focus();
				return false;
			}
			if(!helper.validarRuc(txtNoDocumento.getText())) {
				Clients.showNotification("Número de RUC NO VÁLIDO!","info",txtNoDocumento,"end_center",2000);
				txtNoDocumento.focus();
				return false;
			}
		}
		//luego preguntar si el numero de documento ya se encuentra sobre los registros
		if(validarClienteExistente() == true) {
			Clients.showNotification("Ya hay un Cliente con el número de documento " + txtNoDocumento.getText() + "!","info",txtNoDocumento,"end_center",2000);
			txtNoDocumento.focus();
			return false;
		}
		
		if(txtNombres.getText().isEmpty()) {
			Clients.showNotification("Debe registrar nombre del huesped","info",txtNombres,"end_center",2000);
			txtNombres.setFocus(true);
			return false;
		}
		if(txtApellidos.getText().isEmpty()) {
			Clients.showNotification("Debe registrar apellidos del huesped","info",txtApellidos,"end_center",2000);
			txtApellidos.setFocus(true);
			return false;
		}
		if(txtCelular.getText().isEmpty()) {
			Clients.showNotification("Debe registrar celular del huesped","info",txtCelular,"end_center",2000);
			txtCelular.setFocus(true);
			return false;
		}
		
		//validar el email, el email es opcional, solo se valida si esta lleno
		if(!txtCorreo.getText().isEmpty()) {
			if(!ControllerHelper.validarEmail(txtCorreo.getText())) {
				Clients.showNotification("El correo ingresado no es valido","info",txtCorreo,"end_center",2000);
				txtCorreo.setFocus(true);
				return false;
			}
		}
		return true;
	}
	/** Validar si el cliente existe a traves de la cedula */
	private boolean validarClienteExistente() {
		try {
			boolean bandera = false;
			List<Cliente> listaUsuario;
			if (huesped.getIdCliente() == null) {
				listaUsuario = huespedDAO.getValidarClienteExistente(txtNoDocumento.getText().toString());
			}else {
				listaUsuario = huespedDAO.getValidarClienteExistenteDiferente(txtNoDocumento.getText().toString(),huesped.getIdCliente());
			}
			
			if(listaUsuario.size() != 0)
				bandera = true;
			else
				bandera = false;
			return bandera;
		}catch(Exception ex) {
			return false;
		}
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

	public TipoDocumento getDocumentoSeleccionado() {
		return documentoSeleccionado;
	}

	public void setDocumentoSeleccionado(TipoDocumento documentoSeleccionado) {
		this.documentoSeleccionado = documentoSeleccionado;
	}
	
}
