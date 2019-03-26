package se.bluebim.client.swing;

//HelloPSG.java

import java.awt.*;
import java.awt.geom.*;

import java.util.*;

import javax.swing.*;

import com.sun.scenario.animation.*;
import com.sun.scenario.effect.*;
import com.sun.scenario.scenegraph.*;

public class HelloPSG
{
  final static int WIDTH = 550;
  final static int HEIGHT = 300;
  final static int BORDERWIDTH = 10;

  public static void main (String [] args)
  {
   Runnable r = new Runnable ()
            {
             public void run ()
             {
               createAndShowGUI ();
             }
            };

   EventQueue.invokeLater (r);
  }

  public static void createAndShowGUI ()
  {
   JFrame f = new JFrame ("Hello, PSG!");
   f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

   // JSGPanel is a JComponent that renders a scene graph. Instead of
   // instantiating this class, this method instantiates a subclass that
   // allows a scene graph to be properly centered.

   CenteringSGPanel panel = new CenteringSGPanel ();
   panel.setBackground (Color.BLACK);
   panel.setPreferredSize (new Dimension (WIDTH, HEIGHT));

   // A scene graph is implemented as a tree of nodes. The scene node is an
   // instance of SGGroup, which serves as a container for adding child
   // nodes.

   SGGroup sceneNode = new SGGroup ();
   sceneNode.setID ("sceneNode: SGGroup");

   // SGText represents a child node that renders a single line of text.

   SGText textNode = new SGText ();
   textNode.setID ("textNode: SGText");
   textNode.setText ("Hello, PSG!");
   textNode.setFont (new Font ("SansSerif", Font.PLAIN, 96));
   textNode.setAntialiasingHint (RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
   textNode.setFillPaint (Color.WHITE);

   // Create a node consisting of the text node surrounded by a border
   // node, and add it to the scene graph via the scene node.

   sceneNode.add (createBorderedNode (textNode));

   // An SGEffect node is required to introduce an effect, such as a drop
   // shadow.

   SGEffect effectNode = new SGEffect ();
   effectNode.setID ("effectNode: SGEffect");
   DropShadow shadow = new DropShadow ();
   shadow.setOffsetX (5);
   shadow.setOffsetY (5);
   effectNode.setEffect (shadow);
   effectNode.setChild (textNode);

   // Add the SGEffect node to the scene graph.

   sceneNode.add (effectNode);

   // An SGComposite node is required to specify opacity. The default
   // opacity is 1.0f (fully opaque).

   SGComposite compNode = new SGComposite ();
   compNode.setID ("compNode: SGComposite");
   compNode.setOpacity (1.0f);
   compNode.setChild (effectNode);

   // Add the SGComposite node to the scene graph.

   sceneNode.add (compNode);

   // Establish the scene.

   panel.setScene (sceneNode);

   // Dump the scene graph hierarchy to the standard output.

   dump (panel.getScene (), 0);

   // Install the panel into the Swing hierarchy, pack it to its preferred
   // size, and don't let it be resized. Furthermore, center and display
   // the main window on the screen.

   f.setContentPane (panel);
   f.pack ();
   f.setResizable (false);
   f.setLocationRelativeTo (null);
   f.setVisible (true);

   // Create an animation clip for animating the scene's text opacity. Each
   // cycle will last for 1.5 seconds, the animation will continue
   // indefinitely, the animation will repeatedly invoke the SGComposite
   // timing target, the property being animated is Opacity, and this
   // property is animated from completely opaque (1.0) to completely
   // transparent (0.0). Also, each cycle reverses the animation direction.

   Clip fader = Clip.create (1500,
                Clip.INDEFINITE,
                compNode,
                "Opacity",
                1.0f,
                0.0f);

   // Start the animation.

   fader.start ();
  }

  static SGNode createBorderedNode (SGNode node)
  {
   Rectangle2D nodeR = node.getBounds ();
   double x = nodeR.getX ()-BORDERWIDTH;
   double y = nodeR.getY ()-BORDERWIDTH;
   double w = nodeR.getWidth ()+(2*BORDERWIDTH);
   double h = nodeR.getHeight ()+(2*BORDERWIDTH);
   double a = 1.5*BORDERWIDTH;
   SGShape borderNode = new SGShape ();
   borderNode.setID ("borderNode: SGShape");
   borderNode.setShape (new RoundRectangle2D.Double (x, y, w, h, a, a));
   borderNode.setFillPaint (new Color (0x660000));
   borderNode.setDrawPaint (new Color (0xFFFF33));
   borderNode.setDrawStroke (new BasicStroke ((float) (BORDERWIDTH/2.0)));
   borderNode.setMode (SGAbstractShape.Mode.STROKE_FILL);
   borderNode.setAntialiasingHint (RenderingHints.VALUE_ANTIALIAS_ON);
   SGGroup borderedNode = new SGGroup ();
   borderedNode.setID ("borderedNode: SGGroup");
   borderedNode.add (borderNode);
   borderedNode.add (node);
   return borderedNode;
  }

  public static void dump (SGNode node, int level)
  {
   for (int i = 0; i < level; i++)
      System.out.print (" ");

   System.out.println (node.getID ());

   if (node instanceof SGParent)
   {
     java.util.List<SGNode> children = ((SGParent) node).getChildren ();
     Iterator<SGNode> it = children.iterator ();
     while (it.hasNext ())
       dump (it.next (), level+1);
   }
  }
}

// JSGPanel has been subclassed in order to properly center the scene within 
// this Swing component.

class CenteringSGPanel extends JSGPanel
{
  private SGNode sceneNode;
  private SGTransform.Translate centeringTNode;

  @Override public void doLayout ()
  {
   if (sceneNode != null)
   {
     Rectangle2D bounds = sceneNode.getBounds ();
     centeringTNode.setTranslateX (-bounds.getX ()+
                    (getWidth ()-bounds.getWidth ())/2.0);
     centeringTNode.setTranslateY (-bounds.getY ()+
                    (getHeight ()-bounds.getHeight ())/2.0);
   }
  }

  @Override public void setScene (SGNode sceneNode)
  {
   this.sceneNode = sceneNode;
   centeringTNode = SGTransform.createTranslation (0f, 0f, sceneNode);
   centeringTNode.setID ("centeringTNode: SGTransform.Translate");
   super.setScene (centeringTNode);
  }
}
