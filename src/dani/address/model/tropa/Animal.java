package dani.address.model.tropa;

import java.io.Serializable;


public class Animal implements Serializable{


	private int idAnimal;
	private int garron;
	private double peso;
	

	private Categoria categoria;
	

	private Tropa tropa;
	

	private boolean cabezaFaenadaEntera;
	
	public int getGarron() {
		return garron;
	}

	public void setGarron(int garron) {
		this.garron = garron;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	public int getIdAnimal() {
		return idAnimal;
	}

	public void setIdAnimal(int idAnimal) {
		this.idAnimal = idAnimal;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Tropa getTropa() {
		return tropa;
	}

	public void setTropa(Tropa tropa) {
		this.tropa = tropa;
	}

	public boolean isCabezaFaenadaEntera() {
		return cabezaFaenadaEntera;
	}

	public void setCabezaFaenadaEntera(boolean cabezaFaenadaEntera) {
		this.cabezaFaenadaEntera = cabezaFaenadaEntera;
	}
	
	
}
