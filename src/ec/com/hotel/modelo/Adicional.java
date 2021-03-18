package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the adicional database table.
 * 
 */
@Entity
@NamedQuery(name="Adicional.buscarPorReserva", query="SELECT a FROM Adicional a where a.estado = 'A' and a.reserva.idReserva = :id")
public class Adicional implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_adicional")
	private Integer idAdicional;

	private int cantidad;

	private String estado;

	private float precio;

	private float total;

	//bi-directional many-to-one association to Reserva
	@ManyToOne
	@JoinColumn(name="id_reserva")
	private Reserva reserva;

	//bi-directional many-to-one association to Servicio
	@ManyToOne
	@JoinColumn(name="id_servicio")
	private Servicio servicio;

	public Adicional() {
	}

	public Integer getIdAdicional() {
		return this.idAdicional;
	}

	public void setIdAdicional(Integer idAdicional) {
		this.idAdicional = idAdicional;
	}

	public int getCantidad() {
		return this.cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public float getPrecio() {
		return this.precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public float getTotal() {
		return this.total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public Reserva getReserva() {
		return this.reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	public Servicio getServicio() {
		return this.servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

}