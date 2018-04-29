package leadtools.sane.server;

public abstract interface ImageProcessingListener
{
  public abstract void begin(ImageProcessingEvent paramImageProcessingEvent);

  public abstract void process(PageImageProcessingEvent paramPageImageProcessingEvent);

  public abstract void end(ImageProcessingEvent paramImageProcessingEvent);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.ImageProcessingListener
 * JD-Core Version:    0.6.2
 */