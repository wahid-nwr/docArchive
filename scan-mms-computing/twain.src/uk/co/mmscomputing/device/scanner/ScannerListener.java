package uk.co.mmscomputing.device.scanner;

public abstract interface ScannerListener
{
  public abstract void update(ScannerIOMetadata.Type paramType, ScannerIOMetadata paramScannerIOMetadata);
}

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.scanner.ScannerListener
 * JD-Core Version:    0.6.2
 */