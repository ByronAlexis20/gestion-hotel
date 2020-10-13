package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the servicio database table.
 * 
 */
@Entity
@Table(name="servicio")
@NamedQueries({
	@NamedQuery(name="Servicio.buscarActivos", query="SELECT s FROM Servicio s where s.estado = 'A'"),
	@NamedQuery(name="Servicio.buscarPorId", query="SELECT s FROM Servicio s where s.idServicio = :id"),
})
public class Servicio implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_servicio")
	private int idServicio;

	private String estado;

	private float precio;

	private String servicio;

	//bi-directional many-to-one association to Adicional
	@OneToMany(mappedBy="servicio")
	private List<Adicional> adicionals;

	//bi-directional many-to-one association to ComprobanteDetalle
	@OneToMany(mappedBy="servicio")
	private List<ComprobanteDetalle> comprobanteDetalles;

	public Servicio() {
	}

	public int getIdServicio() {
		return this.idServicio;
	}

	public void setIdServicio(int idServicio) {
		this.idServicio = idServicio;
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

	public String getServicio() {
		return this.servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public List<Adicional> getAdicionals() {
		return this.adicionals;
	}

	public void setAdicionals(List<Adicional> adicionals) {
		this.adicionals = adicionals;
	}

	public Adicional addAdicional(Adicional adicional) {
		getAdicionals().add(adicional);
		adicional.setServicio(this);

		return adicional;
	}

	public Adicional removeAdicional(Adicional adicional) {
		getAdicionals().remove(adicional);
		adicional.setServicio(null);

		return adicional;
	}

	public List<ComprobanteDetalle> getComprobanteDetalles() {
		return this.comprobanteDetalles;
	}

	public void setComprobanteDetalles(List<ComprobanteDetalle> comprobanteDetalles) {
		this.comprobanteDetalles = comprobanteDetalles;
	}

	public ComprobanteDetalle addComprobanteDetalle(ComprobanteDetalle comprobanteDetalle) {
		getComprobanteDetalles().add(comprobanteDetalle);
		comprobanteDetalle.setServicio(this);

		return comprobanteDetalle;
	}

	public ComprobanteDetalle removeComprobanteDetalle(ComprobanteDetalle comprobanteDetalle) {
		getComprobanteDetalles().remove(comprobanteDetalle);
		comprobanteDetalle.setServicio(null);

		return comprobanteDetalle;
	}

}