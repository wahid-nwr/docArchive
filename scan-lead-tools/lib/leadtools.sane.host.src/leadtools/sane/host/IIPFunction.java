package leadtools.sane.host;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import leadtools.RasterImage;

abstract interface IIPFunction
{
  public abstract void invoke(RasterImage paramRasterImage, String paramString)
    throws JsonProcessingException, IOException;
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.host.jar
 * Qualified Name:     leadtools.sane.host.IIPFunction
 * JD-Core Version:    0.6.2
 */