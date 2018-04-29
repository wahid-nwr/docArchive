package ij.plugin.filter;

import ij.ImagePlus;

public abstract interface ExtendedPlugInFilter extends PlugInFilter
{
  public static final int KEEP_PREVIEW = 16777216;

  public abstract int showDialog(ImagePlus paramImagePlus, String paramString, PlugInFilterRunner paramPlugInFilterRunner);

  public abstract void setNPasses(int paramInt);
}

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.ExtendedPlugInFilter
 * JD-Core Version:    0.6.2
 */