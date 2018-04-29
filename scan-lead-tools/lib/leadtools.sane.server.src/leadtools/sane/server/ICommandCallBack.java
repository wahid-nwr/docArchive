package leadtools.sane.server;

import java.io.ByteArrayOutputStream;

public abstract interface ICommandCallBack
{
  public abstract ByteArrayOutputStream invoke(CommandEvent paramCommandEvent);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.ICommandCallBack
 * JD-Core Version:    0.6.2
 */