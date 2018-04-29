package uk.co.mmscomputing.device.twain;

public abstract interface TwainITransferFactory
{
  public abstract TwainITransfer createNativeTransfer(TwainSource paramTwainSource);

  public abstract TwainITransfer createFileTransfer(TwainSource paramTwainSource);

  public abstract TwainITransfer createMemoryTransfer(TwainSource paramTwainSource);
}

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainITransferFactory
 * JD-Core Version:    0.6.2
 */