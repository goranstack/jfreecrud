package se.bluebim.client.swing;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;

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
		return new JXButton("Hello");
	}

}
