package hybridimage;

import java.awt.AWTEvent;
import java.awt.Label;
import java.util.Vector;

import util.Util;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.util.StringSorter;

public class Hybrid_Image implements PlugInFilter{
	private ImagePlus hpImp, lpImp;
	/**
	 * High-pass filter's sigma value
	 */
	private double hpSigma;
	/**
	 * Low-pass filter's sigma value
	 */
	private double lpSigma;
	/**
	 * Low-pass filtered image weight
	 */
	private double lpw;
	/**
	 * High-pass filtered image weight
	 */
	private double hpw;
	private String hpImgName;
	private String lpImgName;
	@Override
	public void run(ImageProcessor arg0) {
		ImageProcessor hp, lp, blend;
		ImagePlus hpImp, lpImp, blendImp;
		boolean ok = this.getParameters();
		if(!ok)
			return;
		Filters filter = new Filters(Filters.GAUSSIAN, Filters.LAPLACIAN, lpSigma, hpSigma);
		lp = filter.getLowPassFilteredImage(this.lpImp.getProcessor());
		hp = filter.getHighPassFilteredImage(this.hpImp.getProcessor());
		blend = Util.blendImages(hp, lp, hpw, lpw);
		
		hpImp = new ImagePlus("High-Pass Filtered Image", hp);
		lpImp = new ImagePlus("Low-Pass Filtered Image", lp);
		
		blendImp = new ImagePlus("Blended Image", blend);
		
		hpImp.show();
		lpImp.show();
		blendImp.show();
	}

	@Override
	public int setup(String arg0, ImagePlus arg1) {
		return DOES_8G | DOES_RGB | DOES_16;
	}
	
	private boolean getParameters(){
		double hpw = 0.5;
		final String lpwMsg = "Low-pass filtered image weight: ";
		String[] suitableImages = getOpenedImages();
		final double[] lpw = new double[1];
		GenericDialog gd = new GenericDialog("Hybridge Image Parameters");
		gd.addChoice("High-pass filtered image: ", suitableImages, hpImgName);
		gd.addNumericField("High-pass filter's sigma value: ", 1, 1);
		gd.addNumericField("High-pass filtered image weight: ", hpw, 1);
		gd.addChoice("Low-pass filtered image: ", suitableImages, lpImgName);
		gd.addNumericField("Low-pass filter's sigma value: ", 1, 1);
		lpw[0] = 1-hpw;
		gd.addMessage(lpwMsg + lpw[0]);
		gd.addDialogListener(new DialogListener(){
			@Override
			public boolean dialogItemChanged(GenericDialog gd, AWTEvent arg1) {
				double hpw;
				gd.getNextNumber();
				hpw = gd.getNextNumber();
				Label lastMsg = (Label)gd.getMessage();
				lpw[0] = 1-hpw;
				lastMsg.setText(lpwMsg + lpw[0]);
				return !gd.invalidNumber();
			}
		});
		gd.showDialog();
		
		if(gd.wasCanceled())
			return false;
		
		hpImp = WindowManager.getImage(gd.getNextChoice());
		lpImp = WindowManager.getImage(gd.getNextChoice());
		hpSigma = gd.getNextNumber();
		this.hpw = gd.getNextNumber();
		lpSigma = gd.getNextNumber();
		this.lpw = lpw[0];
		
		return true;
	}
	
	/**
     * Get a list of open images with the same size and number of channels as the current
     * ImagePlus (number of channels is 1 for grayscale, 3 for RGB).
     * @return A sorted list of the names of the images. Duplicate names are listed only once.
     */
    String[] getOpenedImages (/*ImagePlus imp*/) {
//        int width = imp.getWidth();          // determine properties of the current image
//        int height = imp.getHeight();
//        int channels = imp.getProcessor().getNChannels();
//        int thisID = imp.getID();
        int[] fullList = WindowManager.getIDList();//IDs of all open image windows
        Vector suitables = new Vector(fullList.length); //will hold names of suitable images
        for (int i=0; i<fullList.length; i++) { // check images for suitability, make condensed list
            ImagePlus imp2 = WindowManager.getImage(fullList[i]);
//            if (imp2.getWidth()==width && imp2.getHeight()==height &&
//                    imp2.getProcessor().getNChannels()==channels && fullList[i]!=thisID) {
                String name = imp2.getTitle();  // found suitable image
                if (!suitables.contains(name))  // enter only if a new name
                    suitables.addElement(name);
//            }
        }
        if (suitables.size() == 0)
            return null;                        // nothing found
        String[] suitableImages = new String[suitables.size()];
        for (int i=0; i<suitables.size(); i++)  // vector to array conversion
            suitableImages[i] = (String)suitables.elementAt(i);
        StringSorter.sort(suitableImages);
        return suitableImages;
    }
}
