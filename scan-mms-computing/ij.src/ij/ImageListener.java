package ij;

public abstract interface ImageListener
{
  public abstract void imageOpened(ImagePlus paramImagePlus);

  public abstract void imageClosed(ImagePlus paramImagePlus);

  public abstract void imageUpdated(ImagePlus paramImagePlus);
}

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.ImageListener
 * JD-Core Version:    0.6.2
 */