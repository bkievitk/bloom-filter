package bloomfilter;

import java.util.HashSet;

/**
 * This class is a simple testing framework for StatisticalFilters.
 * 
 * @author bkievitk
 *
 */
public class StatisticalFilterTester {

	public static void main(String[] args) {
		// Create filter, using the Murmur3 hash to test.
		StatisticalFilter<String> filter = new BloomFilter<String>(100, 10, new HashFunction<String>() {
			@Override
			public int hashCode(String e, int seed) {
				return Murmur3.hash32(e.getBytes(), e.length(), seed);				
			}});
		System.out.println(testFilter(filter, .1, false));
	}
	
	/**
	 * Test a filters hit rate based on a given threshold.
	 * @param filter Filter to test.
	 * @param threshold Probability to use to consider true.
	 * @param verbose Show each of the retrieval trials or not.
	 * @return
	 */
	public static double testFilter(StatisticalFilter<String> filter, double threshold, boolean verbose) {
		// Generate strings to store.
		HashSet<String> groundTruth = new HashSet<String>();
		for (int i=0;i<10;i++) {
			String word = i + "";
			filter.add(word);
			groundTruth.add(word);
		}
		
		// Generate strings to test. The first 10 should match.
		// The following 10 should not.
		int hit = 0;
		for (int i=0;i<20;i++) {
			String word = i + "";
			double testValue = filter.probabilityContains(word);
			boolean controlValue = groundTruth.contains(word);
			if (verbose) {
				System.out.println(testValue + "," + controlValue);
			}
			if ((testValue > threshold) == controlValue) {
				hit ++;
			}
		}
		return hit / 20.0;
	}
}
