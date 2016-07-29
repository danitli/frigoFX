package ejecutoras.qrCode;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MedioImpresionImpresoraComun extends MedioImpresion {

	String datos;
	File codigoQRGenerado;
	
	public MedioImpresionImpresoraComun (String datos, File codigoQRGenerado) {
		this.datos = datos;
		this.codigoQRGenerado = codigoQRGenerado;
	}
	
	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex == 0) {
			// User (0,0) is typically outside the
			// imageable area, so we must translate
			// by the X and Y values in the PageFormat
			// to avoid clipping.
			Graphics2D g2d = (Graphics2D) graphics;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

			// Now we perform our rendering
			// g2d.drawString(datos, 100, 100);
			this.printMultipleLines(g2d, 400, 100, datos);

			BufferedImage bi;
			try {
				bi = ImageIO.read(codigoQRGenerado);
				g2d.drawImage(bi, 10, 70, 150, 150, this);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// tell the caller that this page is part
			// of the printed document
			return PAGE_EXISTS;
		}

		return NO_SUCH_PAGE;
	}

}
