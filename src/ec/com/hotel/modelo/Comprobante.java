package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the comprobante database table.
 * 
 */
@Entity
@NamedQuery(name="Comprobante.buscarPorFecha", query="SELECT c FROM Comprobante c where c.fecha = :fecha")
public class Comprobante implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_comprobante")
	private Integer idComprobante;

	private String estado;

	@Temporal(TemporalType.DATE)
	private Date fecha;

	@Column(name="numero_comprobante")
	private String numeroComprobante;

	private String observacion;

	private float total;

	//bi-directional many-to-one association to Reserva
	@ManyToOne
	@JoinColumn(name="id_reserva")
	private Reserva reserva;

	//bi-directional many-to-one association to ComprobanteDetalle
	@OneToMany(mappedBy="comprobante",cascade = CascadeType.ALL)
	private List<ComprobanteDetalle> comprobanteDetalles;

	public Comprobante() {
	}

	public Integer getIdComprobante() {
		return this.idComprobante;
	}

	public void setIdComprobante(Integer idComprobante) {
		this.idComprobante = idComprobante;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNumeroComprobante() {
		return this.numeroComprobante;
	}

	public void setNumeroComprobante(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
	}

	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
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

	public List<ComprobanteDetalle> getComprobanteDetalles() {
		return this.comprobanteDetalles;
	}

	public void setComprobanteDetalles(List<ComprobanteDetalle> comprobanteDetalles) {
		this.comprobanteDetalles = comprobanteDetalles;
	}

	public ComprobanteDetalle addComprobanteDetalle(ComprobanteDetalle comprobanteDetalle) {
		getComprobanteDetalles().add(comprobanteDetalle);
		comprobanteDetalle.setComprobante(this);

		return comprobanteDetalle;
	}

	public ComprobanteDetalle removeComprobanteDetalle(ComprobanteDetalle comprobanteDetalle) {
		getComprobanteDetalles().remove(comprobanteDetalle);
		comprobanteDetalle.setComprobante(null);

		return comprobanteDetalle;
	}

}