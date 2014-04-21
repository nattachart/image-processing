package util;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Gaussian2D_RGB extends GeneralFilterRGB implements PlugInFilter {
	private double[][] filter;
	private double sigma;
	private ImagePlus imp;
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB;
	}

	public void run(ImageProcessor ip) {
		int[][] result;
		GenericDialog gd = new GenericDialog("Gaussian Radius");
		gd.addNumericField("Sigma: ", 1, 1);
		gd.showDialog();
		if(gd.wasCanceled()) return;
		sigma = gd.getNextNumber(); //Get sigma from the user
		String[] args = new String[1];
		args[GaussianFilter.SIGMA_IDX] = sigma+"";
		filter = GaussianFilter.constructFilter2D(args); //Construct the filter (2 dimensions)
		result = applyFilter2D(ip, filter);
		ip.setIntArray(result);
		imp.updateAndDraw(); //Update the displayed image after applying the filter
	}
}
