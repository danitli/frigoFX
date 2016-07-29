package ejecutoras.qrCode;

import java.io.ByteArrayOutputStream;
import java.io.File;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;


public class GeneradorCodigoQR {
	
	public ByteArrayOutputStream crearCodigoQRStream(String datos){
		return QRCode.from(datos).to(ImageType.PNG).stream();
	}
	
	public File crearCodigoQrFile(String datos){
		return QRCode.from(datos).to(ImageType.PNG).file();
	}
	
}
