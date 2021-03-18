package ec.com.hotel.controlador.reportes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import ec.com.hotel.modelo.Cliente;
import ec.com.hotel.modelo.ClienteDAO;
import ec.com.hotel.modelo.Empresa;
import ec.com.hotel.modelo.EmpresaDAO;
import ec.com.hotel.utils.PrintReport;

public class Huesped {
	
	ClienteDAO clienteDAO = new ClienteDAO();
	List<Cliente> clienteLista;
	@Wire private Listbox lstHuespedes;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		buscar();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Cliente.buscarPorPatron")
	@NotifyChange({"clienteLista"})
	public void buscar(){
		if (clienteLista != null) {
			clienteLista = null; 
		}
		clienteLista = clienteDAO.getListaClientesBuscar("");
		lstHuespedes.setModel(new ListModelList(clienteLista));
		if(clienteLista.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	
	@Command
	public void descargarReporte() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			String hotel = "", representante = "", direccion = "", email = "", telefono = "";
			EmpresaDAO empresaDAO = new EmpresaDAO();
			List<Empresa> empresas = empresaDAO.buscarEmpresaActiva();
			if(empresas.size() > 0) {
				hotel = empresas.get(0).getNombre();
				representante = empresas.get(0).getRepresentante();
				direccion = empresas.get(0).getDireccion();
				email = empresas.get(0).getEmail();
				telefono = empresas.get(0).getTelefono();
			}
			
			params.put("HOTEL",hotel);
			params.put("REPRESENTANTE","Representante: " + representante);
			params.put("DIRECCION","Dirección: " + direccion);
			params.put("EMAIL","Correo: " + email);
			params.put("TELEFONO","Teléfono: " + telefono);
			
			PrintReport obj = new PrintReport();
			obj.crearReporte("/reportes/rpHuesped.jasper",clienteDAO, params);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public List<Cliente> getClienteLista() {
		return clienteLista;
	}

	public void setClienteLista(List<Cliente> clienteLista) {
		this.clienteLista = clienteLista;
	}
	
}