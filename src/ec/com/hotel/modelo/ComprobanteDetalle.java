package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the comprobante_detalle database table.
 * 
 */
@Entity
@Table(name="comprobante_detalle")
@NamedQuery(name="ComprobanteDetalle.findAll", query="SELECT c FROM ComprobanteDetalle c")
public class ComprobanteDetalle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_detalle_comprobante")
	private Integer idDetalleComprobante;

	private int cantidad;

	private String descripcion;

	private String estado;

	private float precio;

	private float total;

	//bi-directional many-to-one association to Comprobante
	@ManyToOne
	@JoinColumn(name="id_comprobante")
	private Comprobante comprobante;

	//bi-directional many-to-one association to Servicio
	@ManyToOne
	@JoinColumn(name="id_servicio")
	private Servicio servicio;

	public ComprobanteDetalle() {
	}

	public Integer getIdDetalleComprobante() {
		return this.idDetalleComprobante;
	}

	public void setIdDetalleComprobante(Integer idDetalleComprobante) {
		this.idDetalleComprobante = idDetalleComprobante;
	}

	public int getCantidad() {
		return this.cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	public Comprobante getComprobante() {
		return this.comprobante;
	}

	public void setComprobante(Comprobante comprobante) {
		this.comprobante = comprobante;
	}

	public Servicio getServicio() {
		return this.servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

}