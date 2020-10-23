package ec.com.hotel.modelo;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the imagen database table.
 * 
 */
@Entity
@Table(name="Imagen")
@NamedQueries({
	@NamedQuery(name="Imagen.buscarPorhabitacion", query="SELECT i FROM Imagen i where i.habitacion.idHabitacion = :idHabitacion and i.estado = 'A'"
			+ " and i.tipoImagen = :tipoImagen")
})
public class Imagen implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_imagen")
	private Integer idImagen;

	private String estado;

	@Column(name="ruta_imagen")
	private String rutaImagen;
	
	@Column(name="ruta_relativa")
	private String rutaRelativa;
	
	@Column(name="nombre_imagen")
	private String nombreImagen;
	
	@Column(name="nombre_imagen_panoramica")
	private String nombreImagenPanoramica;
	
	@Column(name="fuente_panoramica")
	private String fuentePanoramica;
	
	@Column(name="tipo_imagen")
	private String tipoImagen;
	
	//bi-directional many-to-one association to Habitacion
	@ManyToOne
	@JoinColumn(name="id_habitacion")
	private Habitacion habitacion;

	public Imagen() {
	}

	public Integer getIdImagen() {
		return this.idImagen;
	}

	public void setIdImagen(Integer idImagen) {
		this.idImagen = idImagen;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getRutaImagen() {
		return this.rutaImagen;
	}

	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}

	public Habitacion getHabitacion() {
		return this.habitacion;
	}

	public void setHabitacion(Habitacion habitacion) {
		this.habitacion = habitacion;
	}

	public String getRutaRelativa() {
		return rutaRelativa;
	}

	public void setRutaRelativa(String rutaRelativa) {
		this.rutaRelativa = rutaRelativa;
	}

	public String getNombreImagen() {
		return nombreImagen;
	}

	public void setNombreImagen(String nombreImagen) {
		this.nombreImagen = nombreImagen;
	}

	public String getNombreImagenPanoramica() {
		return nombreImagenPanoramica;
	}

	public void setNombreImagenPanoramica(String nombreImagenPanoramica) {
		this.nombreImagenPanoramica = nombreImagenPanoramica;
	}

	public String getFuentePanoramica() {
		return fuentePanoramica;
	}

	public void setFuentePanoramica(String fuentePanoramica) {
		this.fuentePanoramica = fuentePanoramica;
	}

	public String getTipoImagen() {
		return tipoImagen;
	}

	public void setTipoImagen(String tipoImagen) {
		this.tipoImagen = tipoImagen;
	}

}