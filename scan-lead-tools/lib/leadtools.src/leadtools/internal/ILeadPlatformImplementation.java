package leadtools.internal;

abstract interface ILeadPlatformImplementation
{
  public abstract byte[] fromBase64String(String paramString);

  public abstract String toBase64String(byte[] paramArrayOfByte);

  public abstract int parseColor(String paramString);

  public abstract ILeadTaskWorker createTaskWorker(ILeadTaskWorker.Worker paramWorker);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.ILeadPlatformImplementation
 * JD-Core Version:    0.6.2
 */