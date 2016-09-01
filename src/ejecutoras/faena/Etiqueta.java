package ejecutoras.faena;

import java.io.File;

import bean.tropa.AnimalBean;
import bean.tropa.TropaBean;
import ejecutoras.qrCode.GeneradorCodigoQR;
import ejecutoras.qrCode.GeneradorEtiqueta;
import ejecutoras.qrCode.MedioImpresionImpresoraComun;
import tropa.Animal;
import tropa.Tropa;

public class Etiqueta {
	private static int PESO_CABEZA_ENTERA = 5;

	public void imprimirEtiquetas(TropaBean tropaBean, AnimalBean animalBean) {

		int peso1;
		int peso2;
		if (animalBean.isCabezaFaenadaEntera()) {
			peso1 = (int) ((Math.round(animalBean.getPeso()) / 2) + PESO_CABEZA_ENTERA);
			peso2 = (int) ((Math.round(animalBean.getPeso()) / 2) - PESO_CABEZA_ENTERA);
		} else {
			peso1 = (int) (Math.round(animalBean.getPeso())/2);
			peso2 = peso1;
		}

		String datos = "Tropa: " + tropaBean.getNumeroTropa() + "\n Fecha Faena: " + tropaBean.getFechaFaena()
				+ "\n Numero de Garron: " + animalBean.getGarron() + "\n Peso: " + peso1  + 
				"\n Categoría: " + animalBean.getIdCategoria();
		
		System.out.println("Los datos a imprimir en la etiqueta son" + datos);

		GeneradorCodigoQR generadorCodigoQR = new GeneradorCodigoQR();
		File codigoQRGenerado = generadorCodigoQR.crearCodigoQrFile(datos);
		MedioImpresionImpresoraComun miic = new MedioImpresionImpresoraComun(datos, codigoQRGenerado);
		
		GeneradorEtiqueta generadorEtiqueta = new GeneradorEtiqueta();
		generadorEtiqueta.imprimir(datos, codigoQRGenerado, miic);
		
		datos = "Tropa: " + tropaBean.getNumeroTropa() + "\n Fecha Faena: " + tropaBean.getFechaFaena() +
				"\n Numero de Garron: " + animalBean.getGarron() + "\n Peso: " + peso2  + 
				"\n Categoría: " + animalBean.getIdCategoria();

		System.out.println("Los datos a imprimir en la etiqueta son" + datos);
		generadorCodigoQR = new GeneradorCodigoQR();
		codigoQRGenerado = generadorCodigoQR.crearCodigoQrFile(datos);
		miic = new MedioImpresionImpresoraComun(datos, codigoQRGenerado);
		generadorEtiqueta = new GeneradorEtiqueta();
		generadorEtiqueta.imprimir(datos, codigoQRGenerado, miic);
		
	}
}
