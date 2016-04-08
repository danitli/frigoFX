package dani.address.model.tropa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import dani.address.model.especie.Especie;
import dani.address.model.establecimiento.Establecimiento;




public class Tropa implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private int idTropa;
	

	private int numeroTropa;
	
	
	private Date fechaIngreso;

	
	private Date fechaFaena;
	
	
	private int animalesRecibidos;
	
	
	private List<Animal> animales;
	
	
	private Establecimiento establecimiento;
	
	
	private Especie especie;
	
	
	private Set<Corral> corrales;
	
	private DTe dte;
	
	public Tropa(){
		
	}
	
	public Tropa(int numeroTropa, int animalesRecibidos, DTe dte, Set<Corral> corrales) {
		this.setNumeroTropa(numeroTropa);
		this.setAnimalesRecibidos(animalesRecibidos);
		this.setDte(dte);
		this.setFechaIngreso(new Date());
		this.setCorrales(corrales);
		this.setAnimales(new ArrayList<Animal>());

	}

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

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public Date getFechaFaena() {
		return fechaFaena;
	}

	public void setFechaFaena(Date fechaFaena) {
		this.fechaFaena = fechaFaena;
	}

	public int getAnimalesRecibidos() {
		return animalesRecibidos;
	}

	public void setAnimalesRecibidos(int animalesRecibidos) {
		this.animalesRecibidos = animalesRecibidos;
	}

	public Establecimiento getEstablecimiento() {
		return establecimiento;
	}
	
	public void setEstablecimiento(Establecimiento establecimiento) {
		this.establecimiento = establecimiento;
	}
	
	public List<Animal> getAnimales() {
		return animales;
	}
	
	public void setAnimales(List<Animal> animales) {
		this.animales = animales;
	}

	public Especie getEspecie() {
		return especie;
	}

	public void setEspecie(Especie especie) {
		this.especie = especie;
	}

	public Set<Corral> getCorrales() {
		return corrales;
	}

	public void setCorrales(Set<Corral> corrales) {
		this.corrales = corrales;
	}

	public DTe getDte() {
		return dte;
	}

	public void setDte(DTe dte) {
		this.dte = dte;
	}
	
	public void agregarAnimal(Animal animal){
		this.getAnimales().add(animal);
	}

}
