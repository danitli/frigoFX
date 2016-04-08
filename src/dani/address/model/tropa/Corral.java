package dani.address.model.tropa;

import java.util.GregorianCalendar;

public class Corral {
	
	private int numero;
	private int capacidad;
	private int ocupacion;
	private GregorianCalendar fechaEgreso;
	
	public Corral(int numero, int capacidad) {
		this.setNumero(numero);
		this.setCapacidad(capacidad);
	}
	
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public int getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public int getOcupacion() {
		return ocupacion;
	}

	public void setOcupacion(int ocupacion) {
		this.ocupacion = ocupacion;
	}

	public GregorianCalendar getFechaEgreso() {
		return fechaEgreso;
	}

	public void setFechaEgreso(GregorianCalendar fechaEgreso) {
		this.fechaEgreso = fechaEgreso;
	}
	
	

}
