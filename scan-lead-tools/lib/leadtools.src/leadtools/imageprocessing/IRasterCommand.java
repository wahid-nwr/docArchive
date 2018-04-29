package leadtools.imageprocessing;

import leadtools.RasterImage;

public abstract interface IRasterCommand
{
  public abstract int run(RasterImage paramRasterImage);

  public abstract void onProgress(RasterCommandProgressEvent paramRasterCommandProgressEvent);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.IRasterCommand
 * JD-Core Version:    0.6.2
 */