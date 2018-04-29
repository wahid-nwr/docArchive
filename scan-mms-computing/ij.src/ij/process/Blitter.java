package ij.process;

import java.awt.Color;

public abstract interface Blitter
{
  public static final int COPY = 0;
  public static final int COPY_INVERTED = 1;
  public static final int COPY_TRANSPARENT = 2;
  public static final int ADD = 3;
  public static final int SUBTRACT = 4;
  public static final int MULTIPLY = 5;
  public static final int DIVIDE = 6;
  public static final int AVERAGE = 7;
  public static final int DIFFERENCE = 8;
  public static final int AND = 9;
  public static final int OR = 10;
  public static final int XOR = 11;
  public static final int MIN = 12;
  public static final int MAX = 13;
  public static final int COPY_ZERO_TRANSPARENT = 14;

  public abstract void setTransparentColor(Color paramColor);

  public abstract void copyBits(ImageProcessor paramImageProcessor, int paramInt1, int paramInt2, int paramInt3);
}

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.Blitter
 * JD-Core Version:    0.6.2
 */