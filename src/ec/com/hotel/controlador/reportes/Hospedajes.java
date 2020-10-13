package ec.com.hotel.controlador.reportes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;

import ec.com.hotel.modelo.ClienteDAO;
import ec.com.hotel.modelo.Empresa;
import ec.com.hotel.modelo.EmpresaDAO;
import ec.com.hotel.utils.PrintReport;

public class Hospedajes {
	String hotel = "", representante = "", direccion = "", email = "", telefono = "";
	@Wire Datebox dtpFechaInicioConsumo;
	@Wire Datebox dtpFechaFinConsumo;
	
	ClienteDAO clienteDAO = new ClienteDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		EmpresaDAO empresaDAO = new EmpresaDAO();
		List<Empresa> empresas = empresaDAO.buscarEmpresaActiva();
		if(empresas.size() > 0) {
			hotel = empresas.get(0).getNombre();
			representante = empresas.get(0).getRepresentante();
			direccion = empresas.get(0).getDireccion();
			email = empresas.get(0).getEmail();
			telefono = empresas.get(0).getTelefono();
		}
	}
	
	@Command
	public void hospedajeDetallado() {
		if(dtpFechaInicioConsumo.getValue() == null) {
			Messagebox.show("Debe seleccionar fecha de inicio");
			return;
		}
		if(dtpFechaFinConsumo.getValue() == null) {
			Messagebox.show("Debe seleccionar fecha fin");
			return;
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("HOTEL",hotel);
		params.put("REPRESENTANTE","Representante: " + representante);
		params.put("DIRECCION","Dirección: " + direccion);
		params.put("EMAIL","Correo: " + email);
		params.put("TELEFONO","Teléfono: " + telefono);
		String fecha = "";
		fecha = "Fecha Inicio: " + new SimpleDateFormat("dd/MM/yyyy").format(dtpFechaInicioConsumo.getValue());
		fecha = fecha + " - Fecha Fin: " + new SimpleDateFormat("dd/MM/yyyy").format(dtpFechaFinConsumo.getValue());
		params.put("FECHA_BUSQUEDA",fecha);
		params.put("FECHA_INICIO",dtpFechaInicioConsumo.getValue());
		params.put("FECHA_FIN",dtpFechaFinConsumo.getValue());
		PrintReport obj = new PrintReport();
		obj.crearReporte("/reportes/rpHospedaje.jasper",clienteDAO, params);
	}
	
	@Command
	public void hospedajeResumido() {
		if(dtpFechaInicioConsumo.getValue() == null) {
			Messagebox.show("Debe seleccionar fecha de inicio");
			return;
		}
		if(dtpFechaFinConsumo.getValue() == null) {
			Messagebox.show("Debe seleccionar fecha fin");
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("HOTEL",hotel);
		params.put("REPRESENTANTE","Representante: " + representante);
		params.put("DIRECCION","Dirección: " + direccion);
		params.put("EMAIL","Correo: " + email);
		params.put("TELEFONO","Teléfono: " + telefono);
		String fecha = "";
		fecha = "Fecha Inicio: " + new SimpleDateFormat("dd/MM/yyyy").format(dtpFechaInicioConsumo.getValue());
		fecha = fecha + " - Fecha Fin: " + new SimpleDateFormat("dd/MM/yyyy").format(dtpFechaFinConsumo.getValue());
		params.put("FECHA_BUSQUEDA",fecha);
		params.put("FECHA_INICIO",dtpFechaInicioConsumo.getValue());
		params.put("FECHA_FIN",dtpFechaFinConsumo.getValue());
		PrintReport obj = new PrintReport();
		obj.crearReporte("/reportes/rpHospedajeResumido.jasper",clienteDAO, params);
	}
}
