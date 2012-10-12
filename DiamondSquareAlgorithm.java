package terrainGeneration;
import java.util.Random;

/**
 * 
 * @author Balared
 * @version 0.1.0.0
 *
 * An implementation of the Diamond Square Algorithm, as 
 * described by Fournier, Fussell and Carpenter in 1982.
 *
 */
abstract class DiamondSquareAlgorithm {
	//Randomiser
	private static final Random r = new Random();
	
	/**
	 * @param width The width (and hence height) of the map
	 * @return A completed fractal terrain heightmap
	 */
	public static float[][] generateDSAMap(int size)
	{
		int width = (int)(Math.pow(2, size) + 1);
		//Create a map of the correct size
		float[][] map = new float[width][width];
		
		//Populate the corners of the map

		map[0][0] = r.nextFloat();
		map[0][width-1] = r.nextFloat();
		map[width-1][0] = r.nextFloat();
		map[width-1][width-1] =r.nextFloat();
		
		
		//Perform the DSA on it
		map = performDSA(map, size);
		
		//Normalize map between 0 and 1
		map = normalize(map);
		
		//Return the completed map
		return map;
	}
	
	/**
	 * 
	 * @param preDSAmap The map with populated corners
	 * @param size The size of the map, from which the width and height can be calculated.
	 * @return A completed fractal terrain heightmap
	 */
	private static float[][] performDSA(float[][] preDSAmap, int size)
	{
		//Copy the pre-DSA map
		float[][] postDSAMap = preDSAmap.clone();
		for(int i = 0; i < Math.pow(2,  size) + 1; i++)
			postDSAMap[i] = preDSAmap[i].clone();
		
		
		//perform the algorithm
		for(int i = 0; i < size; i++)
		{
			postDSAMap = square(postDSAMap, size, i);
			postDSAMap = diamond(postDSAMap, size, i);
		}
		
		//Return the completed map
		return postDSAMap;
	}
	
	/**
	 *
	 * @param map The pre-diamond map
	 * @param size The size of the map, from which the width and height can be calculated.
	 * @param depth The number of times the Diamond method has been called so far
	 * @return A post-diamond version of the map
	**/
	private static float[][] diamond(float[][] oldMap, int size, int depth)
	{
		//Copy the pre-diamond map
		float[][] newMap = oldMap.clone();
		for(int i = 0; i < Math.pow(2, size) + 1; i++)
			newMap[i] = oldMap[i].clone();
		
		//Calculate the offset applied to find the first set of coordinates in odd-numbered rows
		int offset = (int)Math.pow(2, size - depth - 1);
		//Calculate the physical size of the array storing the map
		int width = (int)Math.pow(2, size) + 1;
		
		//For each coordinate that conforms to the diamond pattern...
		for(int y = 0; y < width; y += offset)
			for(int x = (y % offset == 0 ? 0 : (int)offset);
					x < width;
					x += offset)
			{
				//...If the coordinate is unassigned...
				if(newMap[x][y]==0)
					//...Assign it a value based on the average of the values around it.
					newMap[x][y] = averageWithRand(
						x >= offset ? 
								newMap[x - offset][y] : 
									Float.MAX_VALUE,
						x < width - offset ? 
								newMap[x + offset][y] : 
									Float.MAX_VALUE,
						y >= offset ? 
								newMap[x][y - offset] : 
									Float.MAX_VALUE,
						y < width - offset ? 
								newMap[x][y + offset] : 
									Float.MAX_VALUE,
						depth + 1);
			}
		
		
		//Return the map post-Diamond
		return newMap;
	}

	/**
	 *
	 * @param map The pre-square map
	 * @param size The size of the map, from which the width and height can be calculated.
	 * @param depth The number of times the Square method has been called so far
	 * @return A post-square version of the map
	**/
	private static float[][] square(float[][] oldMap, int size, int depth)
	{		
		//Copy the pre-square map
		float[][] newMap = oldMap.clone();
		for(int i = 0; i < Math.pow(2, size) + 1; i++)
			newMap[i] = oldMap[i].clone();
		
		//Calculate the offset applied to find the first set of coordinates in odd-numbered rows
		int offset = (int)Math.pow(2, size - depth - 1);
		//Calculate the physical size of the array storing the map
		int width = (int)Math.pow(2, size);
		
		//For each coordinate that conforms to the square pattern...
		for(int y = offset; y < width; y += offset)
			for(int x = offset;	x < width;	x += offset)
			{	
				//...If the coordinate is unassigned...
				if(newMap[x][y]==0)
					//...Assign it a value based on the average of the values around it.
					newMap[x][y] = averageWithRand(
						(x >= offset && y >= offset) ? newMap[x - offset][y - offset] : Float.MAX_VALUE,
						(x <= width - offset && y >= offset) ? newMap[x + offset][y - offset] : Float.MAX_VALUE,
						(x >= offset && y <= width - offset) ? newMap[x - offset][y + offset] : Float.MAX_VALUE,
						(x <= width - offset && y <= width - offset) ? newMap[x + offset][y + offset] : Float.MAX_VALUE,
						depth);
			}
		
		//Return the map post-Square
		return newMap;
	}
	
	/**
	 * @param a The first value contributing to the average
	 * @param bT he second value contributing to the average
	 * @param c The third value contributing to the average
	 * @param d The fourth value contributing to the average
	 * @param randDeprMag The current depth of the algorithm, which acts as a Depriciation Magnitude for the random element of the averaging
	 * @return A value equal to the average of the four values plus a random element of appropriate size according to the depth of the algorithm
	private static float averageWithRand(float a, float b, float c, float d, int randDeprMag)
	{
		//Create a value in which to store the number of valid numbers, and the ret(urn) value. Set both to 0
		int valids = 0;
		float ret = 0;

		//If a/b/c/d is not equal to the (incredibly unlikely) default incorrect value...
		if(a != Float.MAX_VALUE)
		{
			//Increment the number of valid entries
			valids++;
			//Add it to the return value
			ret += a;
		}
		if(b != Float.MAX_VALUE)
		{
			valids++;
			ret += b;
		}
		if(c != Float.MAX_VALUE)
		{
			valids++;
			ret += c;
		}
		if(d != Float.MAX_VALUE)
		{
			valids++;
			ret += d;
		}
		
		//Divide the return value (currently the sum of the valid entries) by the number of valid entries
		ret /= valids;
		
		//THIS IS THE LINE THAT DOESN'T WORK, I THINK
		//Add a random element equal to r(-0.5 -> 0.5) divided by (2 ^ the depth of the current algorithm step)
		ret += (r.nextFloat() - 0.5f) / Math.pow(2, randDeprMag);
		
		//Return the ret value
		return ret;
	}
	
	/**
	 * Normalises the values in the map so they are between 0.0F and 1.0F, for purposes of colouring.
	 * Allows for standarised gradients when colouring map and all that good stuff.
	 * @param rawmap The pre-normalization map
	 * @return The normalised map.
	private static float[][] normalize(float[][] rawMap)
	{
		//Make a copy of the prenormal map
		float[][] normalMap = rawMap.clone();
		for(int i = 0; i < rawMap.length; i++)
			normalMap[i] = rawMap[i].clone();
		
		//Default the highest and lowest value. Make a diff
		float highest = Float.MIN_VALUE;
		float lowest = Float.MAX_VALUE;
		float diff;
		
		//Find the highest and lowest values
		for(int i = 0; i < rawMap.length; i++)
			for(int j = 0; j < rawMap[0].length; j++)
			{
				if(highest < rawMap[i][j]) highest = rawMap[i][j];
				if(lowest > rawMap[i][j]) lowest = rawMap[i][j];
			}
		
		//Calculate the difference between highest and lowest
		diff = highest - lowest;
		
		//Subtract the lowest value from each value (defaulting range to 0.0F+)
		//Divide each range by the difference between the two (defaulting range to 0.0F -> 1.0F)
		for(int i = 0; i < rawMap.length; i++)
			for(int j = 0; j < rawMap[0].length; j++)
			{
				normalMap[i][j] -= lowest;
				normalMap[i][j] /= diff;
			}
		
		//Return the normalised map
		return normalMap;
	}
}
