package dani.address.model.bean.tropa;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;

public class TropaBean {

	private int idTropa;
	private int numeroTropa;
	// private Date fechaIngreso;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", locale = "es-AR", timezone = "America/Argentina/Buenos_Aires")
	private Date fechaFaena;
	// TODO: se toma del sistema asi que borrar eestos comentarios

	private int procendeciaId;
	private int animalesRecibidos;
	private int establecimientoId;
	private int especieId;
	// private List<Integer> animales;
	// private Set<Integer> corralesIds;
	// private int dteId;

	public int getIdTropa() {
		return idTropa;
	}

	public void setIdTropa(int idTropa) {
		this.idTropa = idTropa;
	}

	public int getNumeroTropa() {
		return numeroTropa;
	}

	public void setNumeroTropa(int numeroTropa) {
		this.numeroTropa = numeroTropa;
	}

	/*
	 * public Date getFechaIngreso() { return fechaIngreso; } public void
	 * setFechaIngreso(Date fechaIngreso) { this.fechaIngreso = fechaIngreso; }
	 */
	
	public int getProcendeciaId() {
		return procendeciaId;
	}

	public void setProcendeciaId(int procendeciaId) {
		this.procendeciaId = procendeciaId;
	}

	public int getAnimalesRecibidos() {
		return animalesRecibidos;
	}

	public void setAnimalesRecibidos(int animalesRecibidos) {
		this.animalesRecibidos = animalesRecibidos;
	}

	public int getEstablecimientoId() {
		return establecimientoId;
	}

	public void setEstablecimientoId(int establecimientoId) {
		this.establecimientoId = establecimientoId;
	}

	public int getEspecieId() {
		return especieId;
	}

	public void setEspecieId(int especieId) {
		this.especieId = especieId;
	}

	public Date getFechaFaena() {
		return fechaFaena;
	}

	public void setFechaFaena(Date fechaFaena) {
		this.fechaFaena = fechaFaena;
	}

	/*
	 * public List<Integer> getAnimales() { return animales; } public void
	 * setAnimales(List<Integer> animales) { this.animales = animales; } public
	 * Set<Integer> getCorralesIds() { return corralesIds; } public void
	 * setCorralesIds(Set<Integer> corralesIds) { this.corralesIds =
	 * corralesIds; } public int getDteId() { return dteId; } public void
	 * setDteId(int dteId) { this.dteId = dteId; }
	 */

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
