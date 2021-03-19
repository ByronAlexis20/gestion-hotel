package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "nivel")
@NamedQueries({
	@NamedQuery(name="Nivel.bucarActivos", query="SELECT n FROM Nivel n where n.estado = 'A'"),
	@NamedQuery(name="Nivel.buscarPorPatron", 
	query="SELECT n FROM Nivel n WHERE LOWER(n.nivel) LIKE LOWER(:patron) and n.estado = 'A'")
})

public class Nivel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_nivel")
	private Integer idNivel;

	private String estado;

	private String nivel;

	//bi-directional many-to-one association to Habitacion
	@OneToMany(mappedBy="nivel")
	private List<Habitacion> habitacions;

	public Nivel() {
	}

	public Integer getIdNivel() {
		return this.idNivel;
	}

	public void setIdNivel(Integer idNivel) {
		this.idNivel = idNivel;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNivel() {
		return this.nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public List<Habitacion> getHabitacions() {
		return this.habitacions;
	}

	public void setHabitacions(List<Habitacion> habitacions) {
		this.habitacions = habitacions;
	}

	public Habitacion addHabitacion(Habitacion habitacion) {
		getHabitacions().add(habitacion);
		habitacion.setNivel(this);

		return habitacion;
	}

	public Habitacion removeHabitacion(Habitacion habitacion) {
		getHabitacions().remove(habitacion);
		habitacion.setNivel(null);

		return habitacion;
	}


}