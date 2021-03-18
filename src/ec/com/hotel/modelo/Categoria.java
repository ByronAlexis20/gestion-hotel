package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the categoria database table.
 * 
 */

@Entity
@Table(name = "categoria")
@NamedQueries({
	@NamedQuery(name="Categoria.bucarActivos", query="SELECT c FROM Categoria c where c.estado = 'A'"),
	@NamedQuery(name="Categoria.findAll", query="SELECT c FROM Categoria c WHERE c.estado IS NULL"),
	@NamedQuery(name="Categoria.buscarPorPatron", 
	query="SELECT c FROM Categoria c WHERE LOWER(c.categoria) LIKE LOWER(:patron) and c.estado = 'A'")
})
public class Categoria implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_categoria")
	private Integer idCategoria;

	private String categoria;

	private String estado;

	//bi-directional many-to-one association to Habitacion
	@OneToMany(mappedBy="categoria")
	private List<Habitacion> habitacions;

	public Categoria() {
	}

	public Integer getIdCategoria() {
		return this.idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	public String getCategoria() {
		return this.categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<Habitacion> getHabitacions() {
		return this.habitacions;
	}

	public void setHabitacions(List<Habitacion> habitacions) {
		this.habitacions = habitacions;
	}

	public Habitacion addHabitacion(Habitacion habitacion) {
		getHabitacions().add(habitacion);
		habitacion.setCategoria(this);

		return habitacion;
	}

	public Habitacion removeHabitacion(Habitacion habitacion) {
		getHabitacions().remove(habitacion);
		habitacion.setCategoria(null);

		return habitacion;
	}

}