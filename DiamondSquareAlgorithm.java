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
	 * @param map The map with populated corners
	 * @param width The width (and hence height) of the map
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
	
	private static float[][] diamond(float[][] oldMap, int size, int depth)
	{
		//Copy the pre-diamond map
		float[][] newMap = oldMap.clone();
		for(int i = 0; i < Math.pow(2, size) + 1; i++)
			newMap[i] = oldMap[i].clone();
		
		int offset = (int)Math.pow(2, size - depth - 1);
		int width = (int)Math.pow(2, size) + 1;
		
		for(int y = 0; y < width; y += offset)
			for(int x = (y % offset == 0 ? 0 : (int)offset);
					x < width;
					x += offset)
			{
				if(newMap[x][y]==0)
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
		
		
		return newMap;
	}

	private static float[][] square(float[][] oldMap, int size, int depth)
	{		
		//Copy the pre-square map
		float[][] newMap = oldMap.clone();
		for(int i = 0; i < Math.pow(2, size) + 1; i++)
			newMap[i] = oldMap[i].clone();
		
		int offset = (int)Math.pow(2, size - depth - 1);
		int width = (int)Math.pow(2, size);
		
		for(int y = offset; y < width; y += offset)
			for(int x = offset;	x < width;	x += offset)
			{	
				if(newMap[x][y]==0)
					newMap[x][y] = averageWithRand(
						(x >= offset && y >= offset) ? newMap[x - offset][y - offset] : Float.MAX_VALUE,
						(x <= width - offset && y >= offset) ? newMap[x + offset][y - offset] : Float.MAX_VALUE,
						(x >= offset && y <= width - offset) ? newMap[x - offset][y + offset] : Float.MAX_VALUE,
						(x <= width - offset && y <= width - offset) ? newMap[x + offset][y + offset] : Float.MAX_VALUE,
						depth);
			}
		
		return newMap;
	}
	
	private static float averageWithRand(float a, float b, float c, float d, int randDeprMag)
	{
		int valids = 0;
		float ret = 0;

		if(a != Float.MAX_VALUE)
		{
			valids++;
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
		
		ret /= valids;
		
		ret += (r.nextFloat() - 0.5f) / Math.pow(2, randDeprMag);
		
		return ret;
	}
	
	private static float[][] normalize(float[][] rawMap)
	{
		float[][] normalMap = rawMap.clone();
		for(int i = 0; i < rawMap.length; i++)
			normalMap[i] = rawMap[i].clone();
		
		float highest = Float.MIN_VALUE;
		float lowest = Float.MAX_VALUE;
		float diff;
		
		for(int i = 0; i < rawMap.length; i++)
			for(int j = 0; j < rawMap[0].length; j++)
			{
				if(highest < rawMap[i][j]) highest = rawMap[i][j];
				if(lowest > rawMap[i][j]) lowest = rawMap[i][j];
			}
		
		diff = highest - lowest;
		
		for(int i = 0; i < rawMap.length; i++)
			for(int j = 0; j < rawMap[0].length; j++)
			{
				normalMap[i][j] -= lowest;
				normalMap[i][j] /= diff;
			}
		
		return normalMap;
	}
}
