package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="habitacion")
@NamedQueries({
	@NamedQuery(name="Habitacion.findAll", query="SELECT h FROM Habitacion h"),
	@NamedQuery(name="Habitacion.buscarPorPatron", query="SELECT h FROM Habitacion h where h.numero like :patron and h.estado = 'A'"),
	@NamedQuery(name="Habitacion.buscarPorEstadoReserva", query="SELECT h FROM Habitacion h where h.numero like :patron and h.estado = 'A'"
			+ " and h.estadoReserva = :estadoReserva")
})
public class Habitacion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_habitacion")
	private Integer idHabitacion;

	private String detalles;

	private String estado;

	private String numero;

	@Column(name="numero_camas")
	private int numeroCamas;
	
	@Column(name="estado_reserva")
	private String estadoReserva;

	private float precio;

	//bi-directional many-to-one association to Categoria
	@ManyToOne
	@JoinColumn(name="id_categoria")
	private Categoria categoria;

	//bi-directional many-to-one association to Nivel
	@ManyToOne
	@JoinColumn(name="id_nivel")
	private Nivel nivel;

	//bi-directional many-to-one association to Imagen
	@OneToMany(mappedBy="habitacion")
	private List<Imagen> imagens;

	//bi-directional many-to-one association to Reserva
	@OneToMany(mappedBy="habitacion",cascade = CascadeType.ALL)
	private List<Reserva> reservas;

	public Habitacion() {
	}

	public Integer getIdHabitacion() {
		return this.idHabitacion;
	}

	public void setIdHabitacion(Integer idHabitacion) {
		this.idHabitacion = idHabitacion;
	}

	public String getDetalles() {
		return this.detalles;
	}

	public void setDetalles(String detalles) {
		this.detalles = detalles;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNumero() {
		return this.numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public int getNumeroCamas() {
		return this.numeroCamas;
	}

	public void setNumeroCamas(int numeroCamas) {
		this.numeroCamas = numeroCamas;
	}

	public float getPrecio() {
		return this.precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public Categoria getCategoria() {
		return this.categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Nivel getNivel() {
		return this.nivel;
	}

	public void setNivel(Nivel nivel) {
		this.nivel = nivel;
	}

	public List<Imagen> getImagens() {
		return this.imagens;
	}

	public void setImagens(List<Imagen> imagens) {
		this.imagens = imagens;
	}

	public Imagen addImagen(Imagen imagen) {
		getImagens().add(imagen);
		imagen.setHabitacion(this);

		return imagen;
	}

	public Imagen removeImagen(Imagen imagen) {
		getImagens().remove(imagen);
		imagen.setHabitacion(null);

		return imagen;
	}

	public List<Reserva> getReservas() {
		return this.reservas;
	}

	public void setReservas(List<Reserva> reservas) {
		this.reservas = reservas;
	}

	public Reserva addReserva(Reserva reserva) {
		getReservas().add(reserva);
		reserva.setHabitacion(this);

		return reserva;
	}

	public Reserva removeReserva(Reserva reserva) {
		getReservas().remove(reserva);
		reserva.setHabitacion(null);

		return reserva;
	}

	public String getEstadoReserva() {
		return estadoReserva;
	}

	public void setEstadoReserva(String estadoReserva) {
		this.estadoReserva = estadoReserva;
	}

	@Override
	public String toString() {
		return "Habitacion [idHabitacion=" + idHabitacion + ", detalles=" + detalles + ", estado=" + estado
				+ ", numero=" + numero + ", numeroCamas=" + numeroCamas + ", estadoReserva=" + estadoReserva
				+ ", precio=" + precio + ", categoria=" + categoria + ", nivel=" + nivel + ", imagens=" + imagens
				+ ", reservas=" + reservas + "]";
	}

}