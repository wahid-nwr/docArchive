package leadtools.sane.server;

public abstract interface ClientListener
{
  public abstract void clientStarted(ClientConnectionEvent paramClientConnectionEvent);

  public abstract void clientStopped(ClientConnectionEvent paramClientConnectionEvent);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.ClientListener
 * JD-Core Version:    0.6.2
 */