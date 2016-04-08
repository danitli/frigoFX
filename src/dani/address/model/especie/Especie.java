package dani.address.model.especie;

import java.io.Serializable;




public abstract class Especie implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;


	private int idEspecie;


	private String descripcion;
	private String codigo;

	public int getIdEspecie() {
		return idEspecie;
	}

	public void setIdEspecie(int idEspecie) {
		this.idEspecie = idEspecie;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}
