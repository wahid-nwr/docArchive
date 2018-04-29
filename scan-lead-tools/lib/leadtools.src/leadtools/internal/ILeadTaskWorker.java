package leadtools.internal;

public abstract interface ILeadTaskWorker
{
  public abstract void start();

  public abstract boolean isFinished();

  public static abstract interface Worker
  {
    public abstract void onWorking();

    public abstract void onCompleted();
  }
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.ILeadTaskWorker
 * JD-Core Version:    0.6.2
 */