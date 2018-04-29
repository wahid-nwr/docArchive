package uk.co.mmscomputing.device.scanner;

public abstract interface ScannerDevice
{
  public abstract void setShowUserInterface(boolean paramBoolean)
    throws ScannerIOException;

  public abstract void setShowProgressBar(boolean paramBoolean)
    throws ScannerIOException;

  public abstract void setResolution(double paramDouble)
    throws ScannerIOException;

  public abstract void setRegionOfInterest(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws ScannerIOException;

  public abstract void setRegionOfInterest(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    throws ScannerIOException;

  public abstract void select(String paramString)
    throws ScannerIOException;

  public abstract void setCancel(boolean paramBoolean);

  public abstract boolean getCancel();

  public abstract boolean isBusy();
}

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.scanner.ScannerDevice
 * JD-Core Version:    0.6.2
 */