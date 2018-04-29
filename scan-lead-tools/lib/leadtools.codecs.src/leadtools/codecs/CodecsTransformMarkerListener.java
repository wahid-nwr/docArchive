package leadtools.codecs;

import leadtools.RasterNativeBuffer;

public abstract interface CodecsTransformMarkerListener
{
  public abstract CodecsTransformMarkerAction OnCodecsTransformMarkerCallback(int paramInt, RasterNativeBuffer paramRasterNativeBuffer, CodecsTransformFlags paramCodecsTransformFlags);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsTransformMarkerListener
 * JD-Core Version:    0.6.2
 */