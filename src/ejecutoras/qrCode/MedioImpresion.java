package ejecutoras.qrCode;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public abstract class MedioImpresion implements Printable, ImageObserver {

	public abstract boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height);

	public abstract int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException;

	public void printMultipleLines(Graphics2D g2d, int width, int height, String text) {

		int m_width = width;

		AttributedString styledText = new AttributedString(text);
		AttributedCharacterIterator m_iterator = styledText.getIterator();
		int m_start = m_iterator.getBeginIndex();
		int m_end = m_iterator.getEndIndex();

		FontRenderContext frc = g2d.getFontRenderContext();

		LineBreakMeasurer measurer = new LineBreakMeasurer(m_iterator, frc);
		measurer.setPosition(m_start);

		float x = 0, y = 0;
		while (measurer.getPosition() < m_end) {
			TextLayout layout = measurer.nextLayout(m_width);

			y += layout.getAscent();
			float dx = layout.isLeftToRight() ? 0 : m_width - layout.getAdvance();

			layout.draw(g2d, x + dx, y);
			y += layout.getDescent() + layout.getLeading();
		}
	}
}
