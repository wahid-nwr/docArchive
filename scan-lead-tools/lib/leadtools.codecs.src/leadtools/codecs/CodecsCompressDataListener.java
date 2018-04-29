package leadtools.codecs;

import leadtools.RasterByteOrder;
import leadtools.RasterNativeBuffer;
import leadtools.RasterViewPerspective;

public abstract interface CodecsCompressDataListener
{
  public abstract boolean OnCodecsCompressDataCallback(int paramInt1, int paramInt2, int paramInt3, RasterByteOrder paramRasterByteOrder, RasterViewPerspective paramRasterViewPerspective, RasterNativeBuffer paramRasterNativeBuffer);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsCompressDataListener
 * JD-Core Version:    0.6.2
 */