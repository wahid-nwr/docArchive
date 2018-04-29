package leadtools;

public abstract interface ILeadStream
{
  public abstract boolean canSeek();

  public abstract boolean canRead();

  public abstract boolean canWrite();

  public abstract String getFileName();

  public abstract boolean start();

  public abstract void stop(boolean paramBoolean);

  public abstract boolean isStarted();

  public abstract boolean openFile(String paramString, LeadStreamMode paramLeadStreamMode, LeadStreamAccess paramLeadStreamAccess, LeadStreamShare paramLeadStreamShare);

  public abstract int read(byte[] paramArrayOfByte, int paramInt);

  public abstract int write(byte[] paramArrayOfByte, int paramInt);

  public abstract long seek(LeadSeekOrigin paramLeadSeekOrigin, long paramLong);

  public abstract void closeFile();
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.ILeadStream
 * JD-Core Version:    0.6.2
 */