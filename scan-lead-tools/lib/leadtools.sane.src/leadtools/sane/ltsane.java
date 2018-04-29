package leadtools.sane;

class ltsane
{
  public static native int SaneInit(int[] paramArrayOfInt);

  public static native int SaneGetOptionDescriptor(long paramLong, int paramInt, Object[] paramArrayOfObject);

  public static native int SaneOpen(String paramString, long[] paramArrayOfLong);

  public static native int SaneStart(long paramLong);

  public static native int SaneStatusToLeadError(int paramInt);

  public static native int SaneRead(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int[] paramArrayOfInt);

  public static native int SaneCancel(long paramLong);

  public static native int SaneGetParameters(long paramLong, SaneParameters paramSaneParameters);

  public static native String SaneGetOptionValue(long paramLong, String paramString, int[] paramArrayOfInt);

  public static native String SaneStrStatus(int paramInt);

  public static native NativeSaneDevice[] SaneGetDevices(boolean paramBoolean, int[] paramArrayOfInt);

  public static native int SaneClose(long paramLong);

  public static native int SaneExit();

  public static native int SaneSetOptionValue(long paramLong, String paramString1, String paramString2);

  public static native int SaneAcquire(long paramLong1, long paramLong2);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.ltsane
 * JD-Core Version:    0.6.2
 */