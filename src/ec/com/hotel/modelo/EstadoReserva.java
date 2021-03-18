package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the estado_reserva database table.
 * 
 */
@Entity
@Table(name="estado_reserva")
@NamedQuery(name="EstadoReserva.findAll", query="SELECT e FROM EstadoReserva e")
public class EstadoReserva implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_estado_reserva")
	private int idEstadoReserva;

	private String estado;

	@Column(name="estado_reserva")
	private String estadoReserva;

	//bi-directional many-to-one association to Reserva
	@OneToMany(mappedBy="estadoReserva")
	private List<Reserva> reservas;

	public EstadoReserva() {
	}

	public int getIdEstadoReserva() {
		return this.idEstadoReserva;
	}

	public void setIdEstadoReserva(int idEstadoReserva) {
		this.idEstadoReserva = idEstadoReserva;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getEstadoReserva() {
		return this.estadoReserva;
	}

	public void setEstadoReserva(String estadoReserva) {
		this.estadoReserva = estadoReserva;
	}

	public List<Reserva> getReservas() {
		return this.reservas;
	}

	public void setReservas(List<Reserva> reservas) {
		this.reservas = reservas;
	}

	public Reserva addReserva(Reserva reserva) {
		getReservas().add(reserva);
		reserva.setEstadoReserva(this);

		return reserva;
	}

	public Reserva removeReserva(Reserva reserva) {
		getReservas().remove(reserva);
		reserva.setEstadoReserva(null);

		return reserva;
	}

}