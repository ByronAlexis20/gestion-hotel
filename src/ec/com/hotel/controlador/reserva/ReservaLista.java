package ec.com.hotel.controlador.reserva;

import java.io.IOException;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Window;

public class ReservaLista {
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
	}
	@Command
	public void nuevo(){
		//Map<String, Object> params = new HashMap<String, Object>();
		//params.put("Categoria", new Categoria());
		Window ventanaCargar = (Window) Executions.createComponents("/forms/reservas_recepcion/reservas/reservaEditar.zul", null, null);
		ventanaCargar.doModal();
	}
}
