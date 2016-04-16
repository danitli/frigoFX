package dani.address.model.bean.tropa;


public class AnimalBean {

	private int idAnimal;
	private double peso;
	private int idCategoria;
	private int idTropa;
	private boolean cabezaFaenadaEntera;
	
	public int getIdAnimal() {
		return idAnimal;
	}
	public void setIdAnimal(int idAnimal) {
		this.idAnimal = idAnimal;
	}
	public double getPeso() {
		return peso;
	}
	public void setPeso(double peso) {
		this.peso = peso;
	}
	public int getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}
	public int getIdTropa() {
		return idTropa;
	}
	public void setIdTropa(int idTropa) {
		this.idTropa = idTropa;
	}
	public boolean isCabezaFaenadaEntera() {
		return cabezaFaenadaEntera;
	}
	public void setCabezaFaenadaEntera(boolean cabezaEntera) {
		this.cabezaFaenadaEntera = cabezaEntera;
	}
}
