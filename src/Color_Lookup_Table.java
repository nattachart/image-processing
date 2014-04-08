import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.LUT;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;

//Exercise 12.2
public class Color_Lookup_Table implements PlugInFilter{
	private ImagePlus imp;
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8C;
	}
	
	public void run(ImageProcessor ip) {
		int[] hist = ip.getHistogram();
		IndexColorModel colorModel = (IndexColorModel) ip.getColorModel();
		int[] unusedValues = this.getUnusedValues(hist);
//		IJ.write("hist size: " + hist.length);
//		IJ.write("unused values: " + unusedValues.length);
//		for(int i=0; i<unusedValues.length; i++){
//			IJ.write("unused "+i+": "+unusedValues[i]);
//		}
		ImageProcessor lut = this.drawLUT(colorModel, unusedValues);
		
		Util.showProcessor(lut, "Lookup Table"); //Show the lookup table.
	}
	
	private int[] getUnusedValues(int[] histogram){
		ArrayList<Integer> unusedValues = new ArrayList<Integer>();
		for(int i=0; i<histogram.length; i++){
			if(histogram[i] == 0){
				unusedValues.add(i);
			}
		}
		int[] results = new int[unusedValues.size()];
		for(int i=0; i<unusedValues.size(); i++)
		{
			results[i] = unusedValues.get(i);
		}
		return results;
	}
	
	private ImageProcessor drawLUT(IndexColorModel colorModel, int[] unusedValues)
	//This method draws the look-up table on an ImageProcessor.
	{
		final int numColors = 256; //Number of all colors, fixed to be 256 colors (because for some images, the lookup table has fewer than 256 colors)
		ImageProcessor ip; //The new ImageProcessor to display the lookup table.
		int cellWidth = 20; //The width of each cell for each color displayed.
		int padWidth = 1; //The distance between each cell
		int lineWidth = 1; //The width of the line that draws rectangles (actually squares)
		int width = (cellWidth + padWidth) * 16; //The total width of the table
		width = width + padWidth; //Plus the last distance to the rim
		int height = width; //The height is the same, because it's a square
		int x = padWidth; //x and y starts at (padWidth, padWidth)
		int y = padWidth;
		Color c;
		Color crossColor = new Color(190, 190, 190); //The color for the cross for unused colors on the table
		
		ip = new ColorProcessor(width, height);
		ip.setLineWidth(lineWidth);
		
//		int size = colorModel.getMapSize();
//		IJ.write("size: "+size);
		for(int i=0; i<numColors; i++)
		{
			c = new Color(colorModel.getRed(i), colorModel.getGreen(i), colorModel.getBlue(i));
			ip.setColor(c);
			ip.drawRect(x, y, cellWidth, cellWidth);
			ip.fill(new Roi(x, y, cellWidth, cellWidth));
			if(this.contains(i, unusedValues))
			{
				this.drawCrossInRegtangle(ip, x, y, cellWidth, cellWidth, crossColor);
			}
			x += cellWidth+padWidth;
			if(x >= width) //Finish the row
			{
				x = padWidth;
				y += cellWidth+padWidth;
			}
		}
		return ip;
	}
	
	private boolean contains(int value, int[] list)
	{
		for(int i=0; i<list.length; i++){
			if(list[i] == value)
				return true;
		}
		return false;
	}
	
	private void drawCrossInRegtangle(ImageProcessor ip, int x, int y, int width, int height, Color color){
		ip.setColor(color);
		final int coord = 2;
		final int xIdx = 0;
		final int yIdx = 1;
		int[] p1 = new int[coord];
		int[] p2 = new int[coord];
		int[] p3 = new int[coord];
		int[] p4 = new int[coord];
		p1[xIdx] = x;
		p1[yIdx] = y;
		
		p4[xIdx] = x + width;
		p4[yIdx] = y;
		
		p2[xIdx] = x + width;
		p2[yIdx] = y + height;
		
		p3[xIdx] = x;
		p3[yIdx] = y + height;
		
		ip.drawLine(p1[xIdx], p1[yIdx], p2[xIdx], p2[yIdx]);
		ip.drawLine(p3[xIdx], p3[yIdx], p4[xIdx], p4[yIdx]);
	}
}
