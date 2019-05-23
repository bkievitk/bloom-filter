package bloomfilter;

/**
 * A Holographic Filter works by taking a memory trace of every element that it has seen.
 * This memory trace is updated by vector addition for each new object that it observers.
 * The memory trace can be queried by comparing the cosine of the trace to a vector and
 * uses the cosine similarity to determine how likely it is that the item has been stored
 * in the memory trace. An items vector is computed by taking a positional hash for each
 * cell of the memory trace and then converting that hash into a Gaussian distribution to
 * encourage separation of different traces.
 * 
 * @author bkievitk
 *
 * @param <E>
 */
public class HolographicFilter<E> extends StatisticalFilter<E> {
	
	private int[] memoryTrace;
	private int n;
	private HashFunction<E> hashFunction;

	public HolographicFilter(int sizeEstimate, int bytes, HashFunction<E> hashFunction) {
		super(sizeEstimate, bytes);
		this.hashFunction = hashFunction;
		this.memoryTrace = new int[bytes / 4];
	}
	
	@Override
	public boolean add(E e) {
		for(int i=0;i<memoryTrace.length;i++) {
			// Add the Gaussian version to our memory trace.
			memoryTrace[i] += evenToGaussian(hashFunction.hashCode(e, i));
		}
		return true;
	}

	@Override
	public void clear() {
		for(int i=0;i<memoryTrace.length;i++) {
			memoryTrace[i] = 0;
		}
		n = 0;
	}

	@Override
	public double probabilityContains(E e) {
		// Calculate the cosine between our vector and the memory trace.
		int ab = 0;
		int aa = 0;
		int bb = 0;
		for(int i=0;i<memoryTrace.length;i++) {
			int value = evenToGaussian(hashFunction.hashCode(e, i));
			ab += value * memoryTrace[i];
			aa += value * value;
			bb += memoryTrace[i] * memoryTrace[i];
		}

		// Return 1 minus the cosine angle.
		return 1.4 - Math.acos(ab / (Math.sqrt(aa) * Math.sqrt(bb)));
	}
	
	private int evenToGaussian(int even) {
		// We want to convert our hash into a Gaussian distribution.
		// To do so, we look though bits and treat them as coin flips.
		// 1, are +1 and 0s are -1. We simply add them together to get our new value.
		int value = 0;
		for(int j=0;j<16;j++) {
			if(((even >> j) | 1) > 0) {
				value++;
			} else {
				value--;
			}
		}
		return value;
	}
	
	@Override
	public int size() {
		return n;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int v : memoryTrace) {
			sb.append(v + ",");
		}
		return sb.toString();
	}
}
