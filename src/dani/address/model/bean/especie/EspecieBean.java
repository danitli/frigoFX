package dani.address.model.bean.especie;

public class EspecieBean {
	
	private int idEspecie;
	private String descripcion;
	private String codigo;
	
	public EspecieBean () {
		
	}
	
	public int getIdEspecie() {
		return idEspecie;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public String getCodigo() {
		return codigo;
	}

	public void setIdEspecie(int idEspecie) {
		this.idEspecie = idEspecie;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Override
	public String toString(){
		return this.getDescripcion();
	}
	
	
}
