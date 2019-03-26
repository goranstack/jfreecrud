package se.bluebim.client.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.io.*;

import java.util.*;

import javax.imageio.*;

import javax.swing.*;

import com.sun.scenario.animation.*;
import com.sun.scenario.effect.*;
import com.sun.scenario.scenegraph.*;
import com.sun.scenario.scenegraph.event.*;
import com.sun.scenario.scenegraph.fx.*;

public class GlowingImages
{
  final static int WIDTH = 650;
  final static int HEIGHT = 250;

  final static int GLOW_TIME = 250;
  final static int PADDING = 50;

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
   JFrame f = new JFrame ("Glowing Images");
   f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

   // JSGPanel is a JComponent that renders a scene graph. Instead of
   // instantiating this class, this method instantiates a subclass that
   // allows a scene graph to be properly centered.

   CenteringSGPanel panel = new CenteringSGPanel ();
   panel.setBackground (Color.BLACK);
   panel.setPreferredSize (new Dimension (WIDTH, HEIGHT));

   // Create a group to hold three FXImage nodes that each present a single
   // image.

   FXGroup imageNodes = new FXGroup ();
   imageNodes.setID ("imageNodes: FXGroup");

   // I've chosen to present images of three planets: Venus, Mars, and
   // Jupiter.

   String [] imageNames =
   {
     "venus.gif", "mars.gif", "jupiter.gif"
   };

   // Populate the group.

   int xOffset = 0;
   for (String name: imageNames)
   {
      FXImage imageNode = new FXImage ();
      imageNode.setID ("imageNode: FXImage");

      // Create and register a mouse listener with each image node. This
      // listener is responsible for animating an image node's glow or
      // fade.

      SGMouseListener listener;
      listener = new SGMouseAdapter ()
             {
               public void mouseEntered (MouseEvent me, SGNode n)
               {
                 // Obtain the node's animation clip for
                 // performing a glow.

                 Clip clip;
                 clip = (Clip) n.getAttribute ("doGlow");

                 // An IllegalStateException is thrown if
                 // clip.start() is invoked when the clip is
                 // already running. The following test avoids
                 // this exception.

                 if (clip.isRunning ())
                   return;     

                 // Start the animation.

                 clip.start ();
               }

               public void mouseExited (MouseEvent me, SGNode n)
               {
                 // Obtain the node's animation clip for
                 // performing a fade.

                 Clip clip;
                 clip = (Clip) n.getAttribute ("doFade");

                 // An IllegalStateException is thrown if
                 // clip.start() is invoked when the clip is
                 // already running. The following test avoids
                 // this exception.

                 if (clip.isRunning ())
                   return;     

                 // Start the animation.

                 clip.start ();
               }
             };
      imageNode.addMouseListener (listener);

      try
      {
        // Load next image.

        BufferedImage origImage = ImageIO.read (new File (name));
        imageNode.setImage (origImage);

        // Give image node a glowing effect.

        Glow glow = new Glow ();
        glow.setLevel (0.0f);
        imageNode.setEffect (glow);

        // Make sure that image node is offset from previous image node
        // so that it isn't positioned over the previous node.

        imageNode.setTranslateX (xOffset);

        // Space image nodes appropriately.

        xOffset += origImage.getWidth ()+PADDING;

        // Create animation clips for glowing and fading.

        Clip clipGlow = Clip.create (GLOW_TIME,
                      new BeanProperty<Float>
                      (imageNode.getEffect (), "Level"),
                      0.0f, 1.0f);

        Clip clipFade = Clip.create (GLOW_TIME,
                      new BeanProperty<Float>
                      (imageNode.getEffect (), "Level"),
                      1.0f, 0.0f);

        // Store the animation clips for later access.

        imageNode.putAttribute ("doGlow", clipGlow);
        imageNode.putAttribute ("doFade", clipFade);

        // Add image node to the group.

        imageNodes.add (imageNode);
      }
      catch (Exception e)
      {
        System.err.println ("Image loading error: "+e);
      }
   }

   // Establish the scene.

   panel.setScene (imageNodes);

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
