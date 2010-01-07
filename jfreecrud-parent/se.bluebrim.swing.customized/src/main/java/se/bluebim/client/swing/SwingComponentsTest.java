package se.bluebim.client.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.RectanglePainter;
import org.jdesktop.swingx.painter.ShapePainter;
import org.jdesktop.swingx.painter.AbstractAreaPainter.Style;
import org.jdesktop.swingx.painter.effects.InnerShadowPathEffect;

/**
 * Application for experimenting with customized Swing components.
 * Links:
 * http://today.java.net/pub/a/today/2007/02/22/how-to-write-custom-swing-component.html <br>
 * https://laf-widget.dev.java.net/ <br>
 * https://substance-samples.dev.java.net/ <br>
 * http://www.curious-creature.org/ <br>
 * http://code.google.com/p/seaglass/ <br>
 * http://www.pushing-pixels.org/ <br>
 * http://www.jroller.com/aalmiray/tags/swing <br>
 * http://weblogs.java.net/blog/zixle/archive/2006/07/beanshell_2d_in.html <br>
 * 
 * Perhaps Scene Graph is the way to go for fancy Swing GUI. Check this out:
 * https://scenegraph.dev.java.net <br>
 * https://scenegraph-effects.dev.java.net <br>
 * http://www.informit.com/articles/article.aspx?p=1323245 <br>
 * http://slreynolds.net/talks/scenegraph <br>
 * http://openjfx.java.sun.com/hudson <br>
 * http://fxexperience.com/ <br>
 * http://jfxtras.org/portal/home <br>
 * 
 * mixing/combining javafx swing:
 * http://weblogs.java.net/blog/aim/archive/2009/06/insiders_guide.html <br>
 * http://jfxtras.org/portal/core/-/wiki/JFXtras/JXScene
 * http://blogs.sun.com/clarkeman/entry/how_to_use_javafx_in
 * 
 * @author Goran Stack
 *
 */
public class SwingComponentsTest {	

	private static final int ARC_SIZE = 26;
	private static final Dimension size = new Dimension(160, 80);
	
	public static void main(String[] args) 
	{
		new SwingComponentsTest().run();

	}

	private void run() {
		JFrame window = new JXFrame("Test", true);
		window.getContentPane().add(getContent());
		window.setSize(600, 600);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
	}

	private Component getContent() {
		JXPanel panel = new JXPanel(new FlowLayout());
		JXButton button = new JXButton();
		button.setPreferredSize(size);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setBackgroundPainter(getBackgroundPainter());
		button.setForegroundPainter(getForegroundPainter());
		panel.add(button);
		return panel;
	}

	private Painter getForegroundPainter() {
		return null;
	}

	private Painter getBackgroundPainter() {
		return new CompoundPainter(getInnerShadowPainter());

	}
	
	private Painter getFillPainter()
	{
		RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, size.width, size.height, ARC_SIZE, ARC_SIZE);
		GradientPaint paint = new GradientPaint(0, 0, Color.ORANGE.brighter(), 0, 1, Color.ORANGE.darker());
		ShapePainter shapePainter = new ShapePainter(shape, paint, Style.FILLED);
		shapePainter.setAntialiasing(true);
		shapePainter.setPaintStretched(true);
		return shapePainter;
	}
	
	private Painter getInnerShadowPainter()
	{
//		Shape shape = new RoundRectangle2D.Float(0, 0, size.width, size.height, ARC_SIZE, ARC_SIZE);
		Shape shape = new Rectangle2D.Float(0, 0, size.width, size.height);
		RectanglePainter painter = new RectanglePainter(0,0,0,0, ARC_SIZE, ARC_SIZE, true,
                Color.ORANGE, 3, Color.GREEN.darker());
		painter.setAntialiasing(true);
		painter.setStyle(Style.FILLED);
		InnerShadowPathEffect innerShadow = new InnerShadowPathEffect();
		innerShadow.setBrushSteps(2);
		painter.setAreaEffects(innerShadow);
		return painter;
	}


}
