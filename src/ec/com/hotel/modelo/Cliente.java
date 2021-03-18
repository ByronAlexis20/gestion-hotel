package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="cliente")
@NamedQueries({
	@NamedQuery(name="Cliente.buscarPorPatron", query="SELECT c FROM Cliente c where (lower(c.nombres) "
			+ "like(:patron) or lower(c.apellidos) like(:patron)) and c.estado = 'A'"),
	@NamedQuery(name="Cliente.buscarPorCedula", query="SELECT c FROM Cliente c where c.numeroDocumento = :cedula and c.estado = 'A'"),
	@NamedQuery(name="Cliente.buscarPorCedulaDiferenteAlUsuarioActual", query="SELECT c FROM Cliente c where c.numeroDocumento = :cedula and c.estado = 'A' "
			+ "and c.idCliente <> :idCliente"),
})

public class Cliente implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_cliente")
	private Integer idCliente;

	private String apellidos;

	private String celular;

	private String correo;

	private String direccion;

	private String estado;

	private String nombres;

	@Column(name="numero_documento")
	private String numeroDocumento;

	@Column(name="id_usuario")
	private Integer idUsuario;
	
	//bi-directional many-to-one association to TipoDocumento
	@ManyToOne
	@JoinColumn(name="id_tipo_documento")
	private TipoDocumento tipoDocumento;

	//bi-directional many-to-one association to Reserva
	@OneToMany(mappedBy="cliente",cascade = CascadeType.ALL)
	private List<Reserva> reservas;

	public Cliente() {
	}

	public Integer getIdCliente() {
		return this.idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public String getApellidos() {
		return this.apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCelular() {
		return this.celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getCorreo() {
		return this.correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNombres() {
		return this.nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getNumeroDocumento() {
		return this.numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public TipoDocumento getTipoDocumento() {
		return this.tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public List<Reserva> getReservas() {
		return this.reservas;
	}

	public void setReservas(List<Reserva> reservas) {
		this.reservas = reservas;
	}

	public Reserva addReserva(Reserva reserva) {
		getReservas().add(reserva);
		reserva.setCliente(this);

		return reserva;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Reserva removeReserva(Reserva reserva) {
		getReservas().remove(reserva);
		reserva.setCliente(null);

		return reserva;
	}

}