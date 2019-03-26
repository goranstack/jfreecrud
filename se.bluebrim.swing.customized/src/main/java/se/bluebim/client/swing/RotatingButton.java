package se.bluebim.client.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import java.util.*;

import javax.swing.*;

import com.sun.scenario.animation.*;
import com.sun.scenario.scenegraph.*;
import com.sun.scenario.scenegraph.event.*;

public class RotatingButton
{
  final static int WIDTH = 200;
  final static int HEIGHT = 150;

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
   JFrame f = new JFrame ("Rotating Button");
   f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

   // JSGPanel is a JComponent that renders a scene graph. Instead of
   // instantiating this class, this method instantiates a subclass that
   // allows a scene graph to be properly centered.

   CenteringSGPanel panel = new CenteringSGPanel ();
   panel.setBackground (Color.BLACK);
   panel.setPreferredSize (new Dimension (WIDTH, HEIGHT));

   // Create a Swing button component that outputs a message to the
   // standard output in response to being clicked.

   JButton btn = new JButton ("Click Me!");
   btn.addActionListener (new ActionListener ()
               {
                 public void actionPerformed (ActionEvent ae)
                 {
                  System.out.println ("button clicked");
                 }
               });

   // Insert the Swing button component into the scene graph via an
   // SGComponent node.

   SGComponent compNode = new SGComponent ();
   compNode.setID ("compNode: SGComponent");
   compNode.setComponent (btn);

   // Register a mouse listener with this node that, in response to a mouse
   // click on the node, rotates the node and its button 360 degrees in a
   // clockwise direction.

   SGMouseListener listener;
   listener = new SGMouseListener ()
         {
           public void mouseClicked (MouseEvent me, SGNode node)
           {
            System.out.println ("Receiving mouse event from "+
                      node.getID ());

            // Create an animation clip for animating the button.
            // Each cycle will last for .5 seconds, the animation
            // will consist of 1 cycle, the animation will
            // repeatedly invoke the SGTransform.Rotate timing
            // target (which happens to be the grandparent of the
            // node argument passed to mouseClicked()), the
            // property being animated is Rotation, and this
            // property is animated from 0.0 radians to 2*Math.PI
            // radians (a full circle). The positive value
            // specifies a clockwise rotation.

            Clip spinner = Clip.create (500,
                          1,
                          node.getParent ()
                            .getParent (),
                          "Rotation",
                          0.0,
                          2*Math.PI);

            // Start the animation.

            spinner.start ();
           }

           public void mouseDragged (MouseEvent me, SGNode node)
           {
           }

           public void mouseEntered (MouseEvent me, SGNode node)
           {
           }

           public void mouseExited (MouseEvent me, SGNode node)
           {
           }

           public void mouseMoved (MouseEvent me, SGNode node)
           {
           }

           public void mousePressed (MouseEvent me, SGNode node)
           {
           }

           public void mouseReleased (MouseEvent me, SGNode node)
           {
           }

           public void mouseWheelMoved (MouseWheelEvent me, SGNode node)
           {
           }

         };
   compNode.addMouseListener (listener);

   // The scene consists of a single SGComponent node that houses a Swing
   // JButton component. To rotate the node around a point, we need to
   // create a chain of three transforms. The first transform translates
   // the node's rotation point (which is the node's center point in this
   // application) to the node's origin, the second transform performs the
   // rotation, and the third transform translates the node's rotation
   // point back to its original location. This transform chain is created
   // within the createRotationNode() method.

   SGTransform scene = createRotationNode (compNode);

   // Establish the scene.

   panel.setScene (scene);

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

  // The following createRotationNode() method is based on a nearly identical
  // createRotation() method in Hans Muller's "Introducing the SceneGraph
  // Project" blog entry:
  // http://weblogs.java.net/blog/hansmuller/archive/2008/01/introducing_the.html

  static SGTransform createRotationNode (SGNode node)
  {
   Rectangle2D nodeBounds = node.getBounds ();
   double cx = nodeBounds.getCenterX ();
   double cy = nodeBounds.getCenterY ();
   SGTransform toOriginT = SGTransform.createTranslation (-cx, -cy, node);
   toOriginT.setID ("toOriginT: SGTransform.Translate");
   SGTransform.Rotate rotateT = SGTransform.createRotation (0.0, toOriginT);
   rotateT.setID ("rotateT: SGTransform.Rotate");
   SGTransform toNodeT = SGTransform.createTranslation (cx, cy, rotateT);
   toNodeT.setID ("toNodeT: SGTransform.Translate");
   return toNodeT;
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
