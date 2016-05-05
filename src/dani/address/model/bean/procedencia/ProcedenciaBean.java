package dani.address.model.bean.procedencia;

public class ProcedenciaBean {

	
	private int idProcedencia;
	private String descripcion;
	
	
	public ProcedenciaBean () {
		
	}


	public int getIdProcedencia() {
		return idProcedencia;
	}


	public void setIdProcedencia(int idProcedencia) {
		this.idProcedencia = idProcedencia;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	@Override
	public String toString(){
		return this.getDescripcion();
	}
	
	
}
