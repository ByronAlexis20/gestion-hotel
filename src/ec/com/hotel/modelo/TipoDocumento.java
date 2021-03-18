package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the tipo_documento database table.
 * 
 */

@Entity
@Table(name = "tipo_documento")
@NamedQueries({
	@NamedQuery(name="TipoDocumento.bucarActivos", query="SELECT t FROM TipoDocumento t where t.estado = 'A'"),
	@NamedQuery(name="TipoDocumento.buscarPorPatron", 
	query="SELECT t FROM TipoDocumento t WHERE LOWER(t.tipoDocumento) LIKE LOWER(:patron) and t.estado = 'A'")
})
public class TipoDocumento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_tipo_documento")
	private Integer idTipoDocumento;

	private String estado;
	
	private Integer digitos;

	@Column(name="tipo_documento")
	private String tipoDocumento;

	//bi-directional many-to-one association to Cliente
	@OneToMany(mappedBy="tipoDocumento")
	private List<Cliente> clientes;

	//bi-directional many-to-one association to Usuario
	@OneToMany(mappedBy="tipoDocumento")
	private List<Usuario> usuarios;

	public TipoDocumento() {
	}

	public Integer getIdTipoDocumento() {
		return this.idTipoDocumento;
	}

	public void setIdTipoDocumento(Integer idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getTipoDocumento() {
		return this.tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public List<Cliente> getClientes() {
		return this.clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Cliente addCliente(Cliente cliente) {
		getClientes().add(cliente);
		cliente.setTipoDocumento(this);

		return cliente;
	}

	public Cliente removeCliente(Cliente cliente) {
		getClientes().remove(cliente);
		cliente.setTipoDocumento(null);

		return cliente;
	}

	public List<Usuario> getUsuarios() {
		return this.usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public Usuario addUsuario(Usuario usuario) {
		getUsuarios().add(usuario);
		usuario.setTipoDocumento(this);

		return usuario;
	}

	public Usuario removeUsuario(Usuario usuario) {
		getUsuarios().remove(usuario);
		usuario.setTipoDocumento(null);

		return usuario;
	}

	public Integer getDigitos() {
		return digitos;
	}

	public void setDigitos(Integer digitos) {
		this.digitos = digitos;
	}

}