package leadtools.sane.server;

import java.io.ByteArrayOutputStream;
import leadtools.RasterImageFormat;
import leadtools.sane.DeviceInfo;

public abstract interface ISaneService
{
  public abstract String init(Object paramObject);

  public abstract void start(String paramString, Object paramObject);

  public abstract SaneStatus getStatus(String paramString, Object paramObject);

  public abstract DeviceInfo[] getSources(String paramString, Object paramObject);

  public abstract void selectSource(String paramString1, String paramString2, Object paramObject);

  public abstract void acquire(String paramString, Object paramObject);

  public abstract void setOptionValue(String paramString1, String paramString2, String paramString3, Object paramObject);

  public abstract String getOptionValue(String paramString1, String paramString2, Object paramObject);

  public abstract ByteArrayOutputStream getPage(String paramString, int paramInt1, RasterImageFormat paramRasterImageFormat, int paramInt2, int paramInt3, int paramInt4, Object paramObject);

  public abstract void deletePage(String paramString, int paramInt, Object paramObject);

  public abstract void stop(String paramString, Object paramObject);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.ISaneService
 * JD-Core Version:    0.6.2
 */