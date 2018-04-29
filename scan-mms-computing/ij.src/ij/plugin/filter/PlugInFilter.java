package ij.plugin.filter;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public abstract interface PlugInFilter
{
  public static final int DOES_8G = 1;
  public static final int DOES_8C = 2;
  public static final int DOES_16 = 4;
  public static final int DOES_32 = 8;
  public static final int DOES_RGB = 16;
  public static final int DOES_ALL = 31;
  public static final int DOES_STACKS = 32;
  public static final int SUPPORTS_MASKING = 64;
  public static final int NO_CHANGES = 128;
  public static final int NO_UNDO = 256;
  public static final int NO_IMAGE_REQUIRED = 512;
  public static final int ROI_REQUIRED = 1024;
  public static final int STACK_REQUIRED = 2048;
  public static final int DONE = 4096;
  public static final int CONVERT_TO_FLOAT = 8192;
  public static final int SNAPSHOT = 16384;
  public static final int PARALLELIZE_STACKS = 32768;
  public static final int FINAL_PROCESSING = 65536;
  public static final int KEEP_THRESHOLD = 131072;
  public static final int PARALLELIZE_IMAGES = 262144;

  public abstract int setup(String paramString, ImagePlus paramImagePlus);

  public abstract void run(ImageProcessor paramImageProcessor);
}

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.PlugInFilter
 * JD-Core Version:    0.6.2
 */