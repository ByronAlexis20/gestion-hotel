package ec.com.hotel.controlador.seguridad;

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
import ec.com.hotel.modelo.Perfil;
import ec.com.hotel.modelo.PerfilDAO;
import ec.com.hotel.modelo.TipoDocumento;
import ec.com.hotel.modelo.TipoDocumentoDAO;
import ec.com.hotel.modelo.Usuario;
import ec.com.hotel.modelo.UsuarioDAO;
import ec.com.hotel.utils.Constantes;
import ec.com.hotel.utils.ControllerHelper;

public class UsuarioEditar {
	@Wire private Window winUsuarioEditar;
	@Wire private Combobox cboTipoDocumento;
	@Wire private Combobox cboPerfil;
	@Wire private Textbox txtNombres;
	@Wire private Textbox txtApellidos;
	@Wire private Textbox txtNoDocumento;
	@Wire private Textbox txtTelefono;
	@Wire private Textbox txtUsuario;
	@Wire private Textbox txtDireccion;
	@Wire private Textbox txtClave;
	@Wire private Textbox txtCorreo;
	
	TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
	PerfilDAO perfilDAO = new PerfilDAO();
	ClienteDAO clienteDAO = new ClienteDAO();
	
	UsuarioDAO usuarioDAO = new UsuarioDAO();
	Usuario usuario;
	TipoDocumento documentoSeleccionado;
	Perfil perfilSeleccionado;
	ControllerHelper helper = new ControllerHelper();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		usuario = (Usuario) Executions.getCurrent().getArg().get("Usuario");
		if (usuario == null) {
			usuario = new Usuario();
			usuario.setEstado("A");
			documentoSeleccionado = null;
			perfilSeleccionado = null;
		}else {
			documentoSeleccionado = usuario.getTipoDocumento();
			perfilSeleccionado = usuario.getPerfil();
			cboTipoDocumento.setText(usuario.getTipoDocumento().getTipoDocumento());
			cboPerfil.setText(usuario.getPerfil().getPerfil());
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
						usuario.setTipoDocumento((TipoDocumento)cboTipoDocumento.getSelectedItem().getValue());
						usuario.setPerfil((Perfil)cboPerfil.getSelectedItem().getValue());
						usuario.setClave(helper.encriptar(txtClave.getText()));
						usuarioDAO.getEntityManager().getTransaction().begin();			
						if (usuario.getIdUsuario() == null) {
							usuarioDAO.getEntityManager().persist(usuario);
						}else{
							usuario = (Usuario) usuarioDAO.getEntityManager().merge(usuario);
						}
						grabarCliente();
						usuarioDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Usuario.buscarPorPatron", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						usuarioDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}	
	private void grabarCliente() {
		//cuando es cliente grabar en la tabla cliente
		if(this.perfilSeleccionado.getIdPerfil() == Constantes.CODIGO_USUARIO_HUESPED) {
			Usuario usuarioRecuperado = usuarioDAO.getBuscarUsuarioPorCedula(usuario.getDocumento());
			//primero preguntar si existe el cliente
			List<Cliente> cliLista = clienteDAO.getListaClientesBuscarCedula(usuarioRecuperado.getDocumento());
			Cliente clienteGrabar;
			if(cliLista.size() > 0) //si tiene datos
				clienteGrabar = cliLista.get(0);
			 else 
				clienteGrabar = new Cliente();
			
			clienteGrabar.setApellidos(usuarioRecuperado.getApellido());
			clienteGrabar.setCelular(usuarioRecuperado.getTelefono());
			clienteGrabar.setDireccion(usuarioRecuperado.getDireccion());
			clienteGrabar.setEstado("A");
			clienteGrabar.setIdUsuario(usuarioRecuperado.getIdUsuario());
			clienteGrabar.setNombres(usuarioRecuperado.getNombre());
			clienteGrabar.setNumeroDocumento(usuarioRecuperado.getDocumento());
			clienteGrabar.setTipoDocumento(usuarioRecuperado.getTipoDocumento());
			clienteGrabar.setCorreo(usuarioRecuperado.getCorreo());
			if(clienteGrabar.getIdCliente() == null)
				usuarioDAO.getEntityManager().persist(clienteGrabar);
			else
				usuarioDAO.getEntityManager().merge(clienteGrabar);
		}
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
		if(cboPerfil.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar el Perfil de usuario","info",cboPerfil,"end_center",2000);
			return false;
		}
		//luego preguntar si el numero de documento ya se encuentra sobre los registros
		if(validarUsuarioExistente() == true) {
			Clients.showNotification("Ya hay un Usuario con el número de documento " + txtNoDocumento.getText() + "!","info",txtNoDocumento,"end_center",2000);
			txtNoDocumento.focus();
			return false;
		}
		
		if(txtNombres.getText().isEmpty()) {
			Clients.showNotification("Debe registrar nombre del usuario","info",txtNombres,"end_center",2000);
			txtNombres.setFocus(true);
			return false;
		}
		if(txtApellidos.getText().isEmpty()) {
			Clients.showNotification("Debe registrar apellidos del usuario","info",txtApellidos,"end_center",2000);
			txtApellidos.setFocus(true);
			return false;
		}
		if(txtTelefono.getText().isEmpty()) {
			Clients.showNotification("Debe registrar Telefono del usuario","info",txtTelefono,"end_center",2000);
			txtTelefono.setFocus(true);
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
		if(txtUsuario.getText().isEmpty()) {
			Clients.showNotification("Debe registrar Usuario del usuario","info",txtUsuario,"end_center",2000);
			txtUsuario.setFocus(true);
			return false;
		}
		int idUsuario;
		if(usuario.getIdUsuario() != null)
			idUsuario = usuario.getIdUsuario();
		else
			idUsuario = 0;
		if(usuarioDAO.getBuscarUsuario(txtUsuario.getText(),idUsuario).size() > 0) {
			Clients.showNotification("Usuario ya existe","info",txtUsuario,"end_center",2000);
			return false;
		}
		if(txtClave.getText().isEmpty()) {
			Clients.showNotification("Debe registrar Clave del usuario","info",txtClave,"end_center",2000);
			txtClave.setFocus(true);
			return false;
		}
		return true;
	}
	/** Validar si el usuario existe a traves de la cedula */
	private boolean validarUsuarioExistente() {
		try {
			boolean bandera = false;
			List<Usuario> listaUsuario;
			if (usuario.getIdUsuario() == null) {
				listaUsuario = usuarioDAO.getValidarUsuarioExistente(txtNoDocumento.getText().toString());
			}else {
				listaUsuario = usuarioDAO.getValidarUsuarioExistenteDiferente(txtNoDocumento.getText().toString(),usuario.getIdUsuario());
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

	public List<Perfil> getPerfiles(){
		return perfilDAO.getPerfilesPorDescripcion("");
	}
	
	@Command
	public void salir(){
		BindUtils.postGlobalCommand(null, null, "Usuario.buscarPorPatron", null);
		winUsuarioEditar.detach();
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Perfil getPerfilSeleccionado() {
		return perfilSeleccionado;
	}

	public void setPerfilSeleccionado(Perfil perfilSeleccionado) {
		this.perfilSeleccionado = perfilSeleccionado;
	}

	public TipoDocumento getDocumentoSeleccionado() {
		return documentoSeleccionado;
	}

	public void setDocumentoSeleccionado(TipoDocumento documentoSeleccionado) {
		this.documentoSeleccionado = documentoSeleccionado;
	}
}
