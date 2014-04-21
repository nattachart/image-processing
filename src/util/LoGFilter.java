package util;

public class LoGFilter {
	public static final int SIGMA_IDX = 0;
	
	public static double[][] constructFilter2D(String args[])
	{
		double sigma = Double.parseDouble(args[SIGMA_IDX]);
		int center = (int)(5 * sigma);	//Set the radius to be 5*sigma
		int kernelWidth = 2 * center + 1;
		double r2, exp, coeff;
		double[][] kernel2D = new double[kernelWidth][kernelWidth];
		
		//Construct the kernel (2 dimension)
		for(int i=center; i<kernelWidth; i++)
		{
			for(int j=center; j<kernelWidth; j++)
			{
				r2 = Math.pow(i-center, 2) + Math.pow(j-center, 2);
				coeff = -((r2-Math.pow(sigma, 2))/(2*Math.pow(sigma, 4)));
				exp = -(r2/(2*Math.pow(sigma, 2)));
				kernel2D[i][j] = coeff * Math.exp(exp);
				kernel2D[center-(i-center)][center-(j-center)] = kernel2D[i][j];
				kernel2D[center-(i-center)][center+(j-center)] = kernel2D[i][j];
				kernel2D[center+(i-center)][center-(j-center)] = kernel2D[i][j];
			}
		}
		
		return kernel2D;
	}
}
