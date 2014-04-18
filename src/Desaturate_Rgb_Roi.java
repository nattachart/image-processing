import java.awt.Rectangle;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

public class Desaturate_Rgb_Roi implements PlugInFilter {
	private ImagePlus imp;
	static double sCol = 0.3; // color saturation factor

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB;
	}

	public void run(ImageProcessor ip) {
		//Ask the user for the sCol value (color saturation factor)
		GenericDialog gd = new GenericDialog("sCol Value");
		gd.addNumericField("sCol: ", 0.3, 3);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		sCol = gd.getNextNumber();

		// Get the mask for the ROI (selected region)
		ImageProcessor mask = ip.getMask();

		Rectangle roi = ip.getRoi();
		int x = (int) roi.getX();
		int y = (int) roi.getY();
		int w = (int) roi.getWidth();
		int h = (int) roi.getHeight();
		// IJ.write("mask w: "+mask.getWidth()+", mask h: "+mask.getHeight()+", roi w: "+w+", roi h: "+h);
		//Create a new mask based on the selected region, but the size is equivalent to the original image
		ColorProcessor temp = new ColorProcessor(ip.getWidth(), ip.getHeight());
		for (int j = 0; j < ip.getHeight(); j++) {
			for (int i = 0; i < ip.getWidth(); i++) {
				if (j >= y && j < y + h && i >= x && i < x + w) {
					// In the mask region
					if (mask == null) // When ROI is Rectangle
						temp.set(i, j, 1);
					else {
						if (mask.get(i - x, j - y) != 0)
							temp.set(i, j, 1);
						else
							temp.set(i, j, 0);
					}
				} else {
					temp.set(i, j, 0);
				}
			}
		}
		ip.setMask(temp); // not necessary
		mask = temp;

		// iterate over all pixels, and desaturate only pixels in ROI
		for (int v = 0; v < ip.getHeight(); v++) {
			for (int u = 0; u < ip.getWidth(); u++) {
				if (mask.get(u, v) != 0) {
					// get int-packed color pixel
					int c = ip.get(u, v);

					// extract RGB components from color pixel
					int r = (c & 0xff0000) >> 16;
					int g = (c & 0x00ff00) >> 8;
					int b = (c & 0x0000ff);

					// compute equivalent gray value
					double yy = 0.299 * r + 0.587 * g + 0.114 * b;

					// linearly interpolate $(yyy) --> (rgb)
					r = (int) (yy + sCol * (r - yy));
					g = (int) (yy + sCol * (g - yy));
					b = (int) (yy + sCol * (b - yy));

					// reassemble color pixel
					c = ((r & 0xff) << 16) | ((g & 0xff) << 8) | b & 0xff;
					ip.set(u, v, c);
				}
			}
		}
		imp.updateAndDraw();
	}
}
