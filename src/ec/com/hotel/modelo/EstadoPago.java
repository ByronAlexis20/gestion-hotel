package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the estado_pago database table.
 * 
 */
@Entity
@Table(name="estado_pago")
@NamedQuery(name="EstadoPago.buscarPorId", query="SELECT e FROM EstadoPago e where e.idEstadoPago = :id")
public class EstadoPago implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_estado_pago")
	private int idEstadoPago;

	private String estado;

	@Column(name="estado_pago")
	private String estadoPago;

	//bi-directional many-to-one association to Reserva
	@OneToMany(mappedBy="estadoPago")
	private List<Reserva> reservas;

	public EstadoPago() {
	}

	public int getIdEstadoPago() {
		return this.idEstadoPago;
	}

	public void setIdEstadoPago(int idEstadoPago) {
		this.idEstadoPago = idEstadoPago;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getEstadoPago() {
		return this.estadoPago;
	}

	public void setEstadoPago(String estadoPago) {
		this.estadoPago = estadoPago;
	}

	public List<Reserva> getReservas() {
		return this.reservas;
	}

	public void setReservas(List<Reserva> reservas) {
		this.reservas = reservas;
	}

	public Reserva addReserva(Reserva reserva) {
		getReservas().add(reserva);
		reserva.setEstadoPago(this);

		return reserva;
	}

	public Reserva removeReserva(Reserva reserva) {
		getReservas().remove(reserva);
		reserva.setEstadoPago(null);

		return reserva;
	}

}