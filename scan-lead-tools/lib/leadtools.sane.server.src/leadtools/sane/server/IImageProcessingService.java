package leadtools.sane.server;

import java.io.ByteArrayOutputStream;
import leadtools.RasterImageFormat;

public abstract interface IImageProcessingService
{
  public abstract void run(String paramString1, int paramInt1, int paramInt2, String paramString2, String paramString3, Object paramObject);

  public abstract ByteArrayOutputStream preview(String paramString1, int paramInt1, String paramString2, String paramString3, int paramInt2, int paramInt3, RasterImageFormat paramRasterImageFormat, int paramInt4, Object paramObject);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.IImageProcessingService
 * JD-Core Version:    0.6.2
 */