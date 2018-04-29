package ij.measure;

public abstract interface Measurements
{
  public static final int AREA = 1;
  public static final int MEAN = 2;
  public static final int STD_DEV = 4;
  public static final int MODE = 8;
  public static final int MIN_MAX = 16;
  public static final int CENTROID = 32;
  public static final int CENTER_OF_MASS = 64;
  public static final int PERIMETER = 128;
  public static final int LIMIT = 256;
  public static final int RECT = 512;
  public static final int LABELS = 1024;
  public static final int ELLIPSE = 2048;
  public static final int INVERT_Y = 4096;
  public static final int CIRCULARITY = 8192;
  public static final int SHAPE_DESCRIPTORS = 8192;
  public static final int FERET = 16384;
  public static final int INTEGRATED_DENSITY = 32768;
  public static final int MEDIAN = 65536;
  public static final int SKEWNESS = 131072;
  public static final int KURTOSIS = 262144;
  public static final int AREA_FRACTION = 524288;
  public static final int SLICE = 1048576;
  public static final int STACK_POSITION = 1048576;
  public static final int SCIENTIFIC_NOTATION = 2097152;
  public static final int ADD_TO_OVERLAY = 4194304;
  public static final int MAX_STANDARDS = 20;
}

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.measure.Measurements
 * JD-Core Version:    0.6.2
 */