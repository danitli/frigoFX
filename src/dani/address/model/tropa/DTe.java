package dani.address.model.tropa;

import java.util.GregorianCalendar;

public class DTe {

	private int numeroDte;
	private String origen;
	private GregorianCalendar fechaCarga;
	private GregorianCalendar fechaVencimiento;
	private int animalesEnviados;

	public DTe(int numero, int animalesEnviados) {
		this.setNumeroDte(numero);
		this.setAnimalesEnviados(animalesEnviados);
	}

	public int getNumeroDte() {
		return numeroDte;
	}

	public void setNumeroDte(int numeroDte) {
		this.numeroDte = numeroDte;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public GregorianCalendar getFechaCarga() {
		return fechaCarga;
	}

	public void setFechaCarga(GregorianCalendar fechaCarga) {
		this.fechaCarga = fechaCarga;
	}

	public GregorianCalendar getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(GregorianCalendar fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public int getAnimalesEnviados() {
		return animalesEnviados;
	}

	public void setAnimalesEnviados(int animalesEnviados) {
		this.animalesEnviados = animalesEnviados;
	}

}
