package leadtools.sane.server;

import java.io.ByteArrayOutputStream;

public abstract interface ICommandService
{
  public abstract ByteArrayOutputStream run(String paramString1, String paramString2, String paramString3, Object paramObject);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.ICommandService
 * JD-Core Version:    0.6.2
 */