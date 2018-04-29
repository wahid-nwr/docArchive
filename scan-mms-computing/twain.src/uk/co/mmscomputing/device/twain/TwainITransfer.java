package uk.co.mmscomputing.device.twain;

public abstract interface TwainITransfer
{
  public abstract void initiate()
    throws TwainIOException;

  public abstract void finish()
    throws TwainIOException;

  public abstract void setCancel(boolean paramBoolean);

  public abstract void cancel()
    throws TwainIOException;

  public abstract void cleanup()
    throws TwainIOException;
}

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainITransfer
 * JD-Core Version:    0.6.2
 */