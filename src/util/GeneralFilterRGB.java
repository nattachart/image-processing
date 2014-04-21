package util;

import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ChannelSplitter;
import ij.plugin.RGBStackMerge;


public class GeneralFilterRGB extends GeneralFilter{
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final int ALPHA = 3;
	private int[] destRange;
	
	public GeneralFilterRGB()
	{
		destRange = new int[2];
		destRange[MIN] = 0;
		destRange[MAX] = 1;
	}
	
	public int[][] applyFilter2D(ImageProcessor ip, double[][] filter)
	{
		int imgW = ip.getWidth();
		int imgH = ip.getHeight();
		int img[][] = ip.getIntArray();
		int result[][] = new int[imgW][imgH];
		
		//Separate color channels
		ImageProcessor redP = ip.duplicate();
		ImageProcessor greenP = ip.duplicate();
		ImageProcessor blueP = ip.duplicate();
		ImageProcessor alphaP = ip.duplicate();
		redP.setIntArray(this.getColorChannel(RED, img));
		greenP.setIntArray(this.getColorChannel(GREEN, img));
		blueP.setIntArray(this.getColorChannel(BLUE, img));
		alphaP.setIntArray(this.getColorChannel(ALPHA, img));
		int[][] alpha = alphaP.getIntArray();
		
		//Apply filter
		int[][] red = super.applyFilter2D(redP, filter);
		int[][] green = super.applyFilter2D(greenP, filter);
		int[][] blue = super.applyFilter2D(blueP, filter);
		
		//Scale the results
		int[] redSrcRange = this.getMinMax(red);
		int[] greenSrcRange = this.getMinMax(green);
		int[] blueSrcRange = this.getMinMax(blue);
		red = this.scaleImage(redSrcRange, destRange, red);
		green = this.scaleImage(greenSrcRange, destRange, green);
		blue = this.scaleImage(blueSrcRange, destRange, blue);
		redP.setIntArray(red);
		greenP.setIntArray(green);
		blueP.setIntArray(blue);
		
		//Combine the channels together
		result = this.combineRGBChannels(red, green, blue, alpha);
//		ImagePlus mg = RGBStackMerge.mergeChannels(sips, true);
//		result = mg.getChannelProcessor().getIntArray();
//		IJ.write("mg: "+mg.getProcessor());
		
		return result;
	}
	
	public int[][] applyFilterXY(ImageProcessor ip, double[] x, double[] y)
	{
		int imgW = ip.getWidth();
		int imgH = ip.getHeight();
		int img[][] = ip.getIntArray();
		int result[][] = new int[imgW][imgH];
		
		//Separate color channels
		ImageProcessor redP = ip.duplicate();
		ImageProcessor greenP = ip.duplicate();
		ImageProcessor blueP = ip.duplicate();
		ImageProcessor alphaP = ip.duplicate();
		redP.setIntArray(this.getColorChannel(RED, img));
		greenP.setIntArray(this.getColorChannel(GREEN, img));
		blueP.setIntArray(this.getColorChannel(BLUE, img));
		alphaP.setIntArray(this.getColorChannel(ALPHA, img));
		int[][] alpha = alphaP.getIntArray();
		
		//Apply filters
		//Red channel
		//Apply the filter in rows
		int[][] red = this.applyFilter1D(redP, x, HORIZONTAL);
		//Scale pixel values
		int [] srcRange = this.getMinMax(red);
		red = this.scaleImage(srcRange, destRange, red);
		redP.setIntArray(red);
		//Apply the filter in columns
		red = this.applyFilter1D(redP, y, VERTICAL);
		//Scale pixel values
		srcRange = this.getMinMax(red);
		red = this.scaleImage(srcRange, destRange, red);
		
		//Green channel
		//Apply the filter in rows
		int[][] green = this.applyFilter1D(greenP, x, HORIZONTAL);
		//Scale pixel values
		srcRange = this.getMinMax(green);
		green = this.scaleImage(srcRange, destRange, green);
		greenP.setIntArray(green);
		//Apply the filter in columns
		green = this.applyFilter1D(greenP, y, VERTICAL);
		//Scale pixel values
		srcRange = this.getMinMax(green);
		green = this.scaleImage(srcRange, destRange, green);
		
		//Blue channel
		//Apply the filter in rows
		int[][] blue = this.applyFilter1D(blueP, x, HORIZONTAL);
		//Scale pixel values
		srcRange = this.getMinMax(blue);
		blue = this.scaleImage(srcRange, destRange, blue);
		blueP.setIntArray(blue);
		//Apply the filter in columns
		blue = this.applyFilter1D(blueP, y, VERTICAL);
		//Scale pixel values
		srcRange = this.getMinMax(blue);
		blue = this.scaleImage(srcRange, destRange, blue);
		
		//Combine the channels together
		result = this.combineRGBChannels(red, green, blue, alpha);
		 
		return result;
	}
	
	private int[][] getColorChannel(int color, int[][] img)
	{
		int[][] channel = new int[img.length][img[0].length];
		
		for(int i=0; i<img.length; i++)
		{
			for(int j=0; j<img[0].length; j++)
			{
				channel[i][j] = this.getColorValue(color, img[i][j]);
			}
		}
		
		return channel;
	}
	
	private int[][] combineRGBChannels(int[][] red, int[][] green, int[][] blue, int[][] alpha)
	{
		int[][] img = new int[red.length][red[0].length]; 
		
		for(int i=0; i<img.length; i++)
		{
			for(int j=0; j<img[0].length; j++)
			{
				img[i][j] = this.combineRGBValues(red[i][j], green[i][j], blue[i][j], alpha[i][j]);
			}
		}
		
		return img;
	}
	
	private int getColorValue(int color, int pixel)
	{
		int value;
		switch(color)
		{
			case RED:
				value = (pixel & 0xff0000);
				value >>= 16;
				break;
			case GREEN:
				value = (pixel & 0x00ff00);
				value >>= 8;
				break;
			case BLUE:
				value = (pixel & 0x0000ff);
				break;
			default: //Alpha
				value = (pixel & 0xff000000);
				value >>= 32;
				break;
		}
		return value;
	}
	
	private int combineRGBValues(int red, int green, int blue, int alpha)
	{
		int pixel = 0;
		//Assume that red, green, blue are not negative.
		pixel = alpha & 0xff;
		pixel <<= 32;
		pixel |= red & 0xff;
		pixel <<= 16;
		pixel |= green & 0xff;
		pixel <<= 8;
		pixel |= blue & 0xff;
		
		return pixel;
	}
}
