package se.bluebrim.crud;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

/**
 * A class suitable for representing an image in a client server
 * environment where the image is stored in the database and transferred
 * between client server as byte array, but represented as a BufferedImage
 * in the user interface. The text field is used at the client side to
 * inform the user about on going image operations. For example when
 * a lazy initialized image is loaded from the server the text "is loading"
 * is stored in the text property. 
 * 
 * @author GStack
 *
 */
public abstract class ClientServerImage extends AbstractDto implements Serializable
{
	protected byte[] imageData;
	protected transient BufferedImage image;
	private boolean changed = false;

	public ClientServerImage()
	{
	}
	
	public ClientServerImage(BufferedImage image)
	{
		this.image = image;
	}
	
	public ClientServerImage( byte[] imageData)
	{
		this.imageData = imageData;
	}
			
	public byte[] getImageData()
	{
		if (imageData == null)
			loadImageData();
		return imageData;
	}
		
	public BufferedImage getImage()
	{
		if (image == null)
		{
			image = fromBytesToImage(getImageData());
			imageData = null;
		}
		return image;
	}
			
	public void setImage(BufferedImage image)
	{
		if (!equals(this.image, image))
		{
			this.image = image;
			changed = true;
			fireValueChanged("image", this.image);
		}
	}
	
	public boolean isChanged()
	{
		return changed;
	}
	
	
	protected static byte[] fromImageToBytes(BufferedImage image)
	{
		if (image == null)
			return null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			ImageIO.write(image, "png", out);
		} catch (IOException e)
		{
			throw new RuntimeException("Unable to convert image to byte array", e);
		}
		return out.toByteArray();
	}

	protected static BufferedImage fromBytesToImage(byte[] bytes)
	{
		if (bytes == null)
			return null;
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try
		{
			return ImageIO.read(in);
		} catch (IOException e)
		{
			throw new RuntimeException("Unable to convert byte array to image", e);
		}
	}
	
  private void writeObject( ObjectOutputStream stream ) throws IOException
  {
  	prepareServerTransfer();
  	stream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException
	{
		stream.defaultReadObject();
	}
  

	/**
	 * It's unnecessary to transfer unchanged image. Changed image is transferred
	 * as a byte array.
	 */
	private void prepareServerTransfer()
	{
		if (changed)
		{
			imageData = fromImageToBytes(image);
		}
	}
	
	protected abstract void loadImageData();
	
	public BufferedImage createThumbnail(float maxWidth, float maxHeight)
	{
		try
		{
			return createScaledImage(getImage(), maxWidth, maxHeight);
		} catch (OutOfMemoryError e)
		{
			return null;
		}
	}
	
	/**
	 * Return a scaled image with the same aspect ratio as the original.<br>
	 * Can may be improved see:
	 * http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
	 */
	public static BufferedImage createScaledImage(BufferedImage original, float maxWidth, float maxHeight)
	{
		double vScale = maxHeight / original.getHeight();
		double hScale = maxWidth / original.getWidth();
		double scale = Math.min(vScale, hScale);
		int scaledImageHeight = (int) (original.getHeight() * scale);
		int scaledImageWidth = (int) (original.getWidth() * scale);
		BufferedImage scaledImage = new BufferedImage(scaledImageWidth, scaledImageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = scaledImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		graphics2D.drawRenderedImage(original, AffineTransform.getScaleInstance(scale, scale));
		graphics2D.dispose();
		return scaledImage;		
	}

}
