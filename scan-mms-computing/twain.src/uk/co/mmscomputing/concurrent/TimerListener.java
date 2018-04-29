package uk.co.mmscomputing.concurrent;

public abstract interface TimerListener
{
  public abstract void begin(int paramInt);

  public abstract void tick(int paramInt);

  public abstract void end(int paramInt);
}

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.concurrent.TimerListener
 * JD-Core Version:    0.6.2
 */