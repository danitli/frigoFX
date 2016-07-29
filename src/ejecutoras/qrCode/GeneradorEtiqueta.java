package ejecutoras.qrCode;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;

public class GeneradorEtiqueta {

	public void imprimir(String datos, File codigoQRGenerado, MedioImpresion medioImpresion) {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setCopies(1);
		job.setPrintable(medioImpresion);
		try {
			job.print();
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public static void main(String[] args) {

		// TODO
		// http://stackoverflow.com/questions/9308412/sending-a-barcode-to-a-zebra-printer-from-a-java-application
		GeneradorEtiqueta ge = new GeneradorEtiqueta();
		String datos = "Tropa: 4 \n Fecha Faena: 12/10/2014 \n Numero de Garron: 4"
				+ "\n Peso del animal: 180 \n Categoria del animal: CHA";
		GeneradorCodigoQR generadorCodigoQR = new GeneradorCodigoQR();		
		File codigoQRGenerado = generadorCodigoQR.crearCodigoQrFile(datos);
		
		MedioImpresionImpresoraComun miic = 
				new MedioImpresionImpresoraComun(datos, codigoQRGenerado);
		ge.imprimir(datos,codigoQRGenerado, miic);

	}

}
