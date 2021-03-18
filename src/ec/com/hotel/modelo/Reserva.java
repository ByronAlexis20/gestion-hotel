package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="reserva")
@NamedQueries({
	@NamedQuery(name="Reserva.buscarDisponibilidad", query="SELECT r FROM Reserva r "
			+ "where r.habitacion.idHabitacion = :idHabitacion and (:fecha between r.fechaEntrada and r.fechaSalida) and r.estado = 'A' "
			+ "and r.estadoReservaS = 'PENDIENTE'"),
	@NamedQuery(name="Reserva.buscarPendientes", query="SELECT r FROM Reserva r "
			+ "where r.estadoReservaS = 'PENDIENTE' and r.estado = 'A' order by r.idReserva desc"),
	@NamedQuery(name="Reserva.buscarPendientesPorCliente", query="SELECT r FROM Reserva r "
			+ "where r.estadoReservaS = 'PENDIENTE' and r.estado = 'A' and r.cliente.idCliente = :idCliente order by r.idReserva desc"),
	@NamedQuery(name="Reserva.buscarPendientesDiaActual", query="SELECT r FROM Reserva r "
			+ "where r.estadoReservaS = 'PENDIENTE' and r.estado = 'A' and r.fechaEntrada = :fecha "
			+ "and (lower(r.cliente.nombres) like(:patron) or lower(r.cliente.apellidos) like(:patron)) order by r.idReserva asc"),
	@NamedQuery(name="Reserva.buscarPorHabitacion", query="SELECT r FROM Reserva r where r.estadoReservaS = 'CONFIRMADO' and "
			+ "r.estado = 'A' and r.habitacion.numero = :numeroHabitacion and (lower(r.cliente.nombres) like(:patron) or "
			+ "lower(r.cliente.apellidos) like(:patron)) order by r.idReserva desc")
	
})

public class Reserva implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_reserva")
	private int idReserva;

	@Column(name="cantidad_dias")
	private int cantidadDias;

	private String estado;

	@Column(name="estado_reserva")
	private String estadoReservaS;
	
	@Temporal(TemporalType.DATE)
	@Column(name="fecha_entrada")
	private Date fechaEntrada;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_salida")
	private Date fechaSalida;
	
	@Temporal(TemporalType.DATE)
	@Column(name="fecha_registro")
	private Date fechaRegistro;

	@Column(name="hora_entrada")
	private Time horaEntrada;

	@Column(name="hora_salida")
	private Time horaSalida;

	private String incidencias;

	@Column(name="numero_adultos")
	private int numeroAdultos;

	@Column(name="numero_ninos")
	private int numeroNinos;

	@Column(name="precio_adelanto")
	private float precioAdelanto;

	@Column(name="precio_adicinal")
	private float precioAdicinal;

	@Column(name="precio_descuento")
	private float precioDescuento;

	@Column(name="precio_total")
	private float precioTotal;

	//bi-directional many-to-one association to Adicional
	@OneToMany(mappedBy="reserva")
	private List<Adicional> adicionals;

	//bi-directional many-to-one association to Comprobante
	@OneToMany(mappedBy="reserva")
	private List<Comprobante> comprobantes;

	//bi-directional many-to-one association to Cliente
	@ManyToOne
	@JoinColumn(name="id_cliente")
	private Cliente cliente;

	//bi-directional many-to-one association to EstadoPago
	@ManyToOne
	@JoinColumn(name="id_estado_pago")
	private EstadoPago estadoPago;

	//bi-directional many-to-one association to EstadoReserva
	@ManyToOne
	@JoinColumn(name="id_estado_reserva")
	private EstadoReserva estadoReserva;

	//bi-directional many-to-one association to Habitacion
	@ManyToOne
	@JoinColumn(name="id_habitacion")
	private Habitacion habitacion;

	public Reserva() {
	}

	public int getIdReserva() {
		return this.idReserva;
	}

	public void setIdReserva(int idReserva) {
		this.idReserva = idReserva;
	}

	public int getCantidadDias() {
		return this.cantidadDias;
	}

	public void setCantidadDias(int cantidadDias) {
		this.cantidadDias = cantidadDias;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaEntrada() {
		return this.fechaEntrada;
	}

	public void setFechaEntrada(Date fechaEntrada) {
		this.fechaEntrada = fechaEntrada;
	}

	public Date getFechaSalida() {
		return this.fechaSalida;
	}

	public void setFechaSalida(Date fechaSalida) {
		this.fechaSalida = fechaSalida;
	}

	public Time getHoraEntrada() {
		return this.horaEntrada;
	}

	public void setHoraEntrada(Time horaEntrada) {
		this.horaEntrada = horaEntrada;
	}

	public Time getHoraSalida() {
		return this.horaSalida;
	}

	public void setHoraSalida(Time horaSalida) {
		this.horaSalida = horaSalida;
	}

	public String getIncidencias() {
		return this.incidencias;
	}

	public void setIncidencias(String incidencias) {
		this.incidencias = incidencias;
	}

	public int getNumeroAdultos() {
		return this.numeroAdultos;
	}

	public void setNumeroAdultos(int numeroAdultos) {
		this.numeroAdultos = numeroAdultos;
	}

	public int getNumeroNinos() {
		return this.numeroNinos;
	}

	public void setNumeroNinos(int numeroNinos) {
		this.numeroNinos = numeroNinos;
	}

	public float getPrecioAdelanto() {
		return this.precioAdelanto;
	}

	public void setPrecioAdelanto(float precioAdelanto) {
		this.precioAdelanto = precioAdelanto;
	}

	public float getPrecioAdicinal() {
		return this.precioAdicinal;
	}

	public void setPrecioAdicinal(float precioAdicinal) {
		this.precioAdicinal = precioAdicinal;
	}

	public float getPrecioDescuento() {
		return this.precioDescuento;
	}

	public void setPrecioDescuento(float precioDescuento) {
		this.precioDescuento = precioDescuento;
	}

	public float getPrecioTotal() {
		return this.precioTotal;
	}

	public void setPrecioTotal(float precioTotal) {
		this.precioTotal = precioTotal;
	}

	public List<Adicional> getAdicionals() {
		return this.adicionals;
	}

	public void setAdicionals(List<Adicional> adicionals) {
		this.adicionals = adicionals;
	}

	public Adicional addAdicional(Adicional adicional) {
		getAdicionals().add(adicional);
		adicional.setReserva(this);

		return adicional;
	}

	public Adicional removeAdicional(Adicional adicional) {
		getAdicionals().remove(adicional);
		adicional.setReserva(null);

		return adicional;
	}

	public List<Comprobante> getComprobantes() {
		return this.comprobantes;
	}

	public void setComprobantes(List<Comprobante> comprobantes) {
		this.comprobantes = comprobantes;
	}

	public Comprobante addComprobante(Comprobante comprobante) {
		getComprobantes().add(comprobante);
		comprobante.setReserva(this);

		return comprobante;
	}

	public Comprobante removeComprobante(Comprobante comprobante) {
		getComprobantes().remove(comprobante);
		comprobante.setReserva(null);

		return comprobante;
	}

	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public EstadoPago getEstadoPago() {
		return this.estadoPago;
	}

	public void setEstadoPago(EstadoPago estadoPago) {
		this.estadoPago = estadoPago;
	}

	public EstadoReserva getEstadoReserva() {
		return this.estadoReserva;
	}

	public void setEstadoReserva(EstadoReserva estadoReserva) {
		this.estadoReserva = estadoReserva;
	}

	public Habitacion getHabitacion() {
		return this.habitacion;
	}

	public void setHabitacion(Habitacion habitacion) {
		this.habitacion = habitacion;
	}

	public String getEstadoReservaS() {
		return estadoReservaS;
	}

	public void setEstadoReservaS(String estadoReservaS) {
		this.estadoReservaS = estadoReservaS;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

}