package dani.address.model.establecimiento;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import dani.address.excepciones.TropaInexistenteException;
import dani.address.model.tropa.Tropa;






public class Establecimiento implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;




	private int idEstablecimiento;
	
	
	private String codigoEstablecimiento;
	private String titular;
	private String nombre;
	private long cuit;

	private int numeroHabilitacion;
	private String telefono;
	private String direccion;
	private String localidad;
	private String provincia;
	
	
	private List<Tropa> tropas;

	public Establecimiento() {
		this.setTropas(new ArrayList<Tropa>());
	}

	public String getCodigoEstablecimiento() {
		return codigoEstablecimiento;
	}

	public void setCodigoEstablecimiento(String codigoEstablecimiento) {
		this.codigoEstablecimiento = codigoEstablecimiento;
	}

	public String getTitular() {
		return titular;
	}

	public void setTitular(String titular) {
		this.titular = titular;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public long getCuit() {
		return cuit;
	}

	public void setCuit(long cuit) {
		this.cuit = cuit;
	}

	public int getNumeroHabilitacion() {
		return numeroHabilitacion;
	}

	public void setNumeroHabilitacion(int numeroHabilitacion) {
		this.numeroHabilitacion = numeroHabilitacion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public List<Tropa> getTropas() {
		return tropas;
	}

	public void setTropas(List<Tropa> tropas) {
		this.tropas = tropas;
	}

	public int cantidadTropas() {

		return this.getTropas().size();
	}

	public void agregarTropa(Tropa tropa) {
		this.getTropas().add(tropa);
		

	}

	public Tropa obtenerTropa(int numeroTropa) throws TropaInexistenteException {
		for (Tropa tropa : this.getTropas()) {
			if (tropa.getNumeroTropa() == numeroTropa){
				
				return tropa;
			}
		}
		throw new TropaInexistenteException();
	}

	public boolean eliminarTropa(int numeroTropa) {
		for (Tropa tropa : this.getTropas()) {
			if (tropa.getNumeroTropa() == numeroTropa) {
				this.getTropas().remove(tropa);
				
				return true;
			}
		}
		
		return false;
	}

}
