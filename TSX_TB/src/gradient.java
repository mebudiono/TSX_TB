//package GammScript;


import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JDesktopPane;

/**
 *
 * @author Luwax
 */
public class gradient extends JDesktopPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Color BACKGROUND = Color.decode("#2980B9");
	private static final Color BACKGROUND_2 = Color.WHITE;

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D graphics = (Graphics2D) g.create();

		int midY = 100;

		Paint topPaint = new GradientPaint(0, 0, BACKGROUND,
				0, midY, BACKGROUND_2);
		graphics.setPaint(topPaint);
		graphics.fillRect(0, 0, getWidth(), midY);

		Paint bottomPaint = new GradientPaint(0, midY + 1, BACKGROUND_2,
				0, getHeight(), BACKGROUND);
		graphics.setPaint(bottomPaint);
		graphics.fillRect(0, midY, getWidth(), getHeight());

		graphics.dispose();
	}
//	public static void main(String[] b){
//		gradient a=new gradient();
//		
//	}
}
