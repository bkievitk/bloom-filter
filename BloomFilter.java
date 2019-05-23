package bloomfilter;

/**
 * A BloomFilter is a probabilistic filter that encodes an object as a list of hash
 * values. These are then used to mark points on a filter to indicate presence.
 * The filter can be queries as to the existence of an object by checking if the same
 * marks are present. If they are not, the object was never added to the filter. If
 * they are, the object may have been added to the filter.
 * 
 * @author bkievitk
 *
 * @param <E>
 */
public class BloomFilter<E> extends StatisticalFilter<E> {
	private byte[] filter;
	private int k;
	private HashFunction<E> hashFunction;
	private int n = 0;
	
	public BloomFilter(int sizeEstimate, int bytes, HashFunction<E> hashFunction) {
		super(sizeEstimate, bytes);
		this.hashFunction = hashFunction;
		this.filter = new byte[bytes];
		this.k = (int)Math.ceil(getM() / (double)sizeEstimate * Math.log(2));
	}
	
	@Override
	public boolean add(E e) {
		for(int i=0;i<k;i++) {
			setByte(Math.abs(hashFunction.hashCode(e, i)) % getM());
		}
		n++;
		return true;
	}

	@Override
	public void clear() {
		for(int i=0;i<filter.length;i++) {
			filter[i] = 0;
		}
		n = 0;
	}

	@Override
	public double probabilityContains(E e) {
		for(int i=0;i<k;i++) {
			if(!getByte(Math.abs(hashFunction.hashCode(e, i) % getM()))) {
				return 0;
			}
		}
		// Calculate probability of collision.
		return 1 - Math.pow((1 - Math.pow(Math.E, -k * n / getM())), k);
	}
	
	@Override
	public int size() {
		return n;
	}
	
	/**
	 * Set the bit in the filter that is at a given offset.
	 * @param offset
	 */
	private void setByte(int offset) {
		int byteOffset = offset / 8;
		int bitOffset = offset % 8;
		filter[byteOffset] |= (1 << bitOffset);
	}

	/**
	 * Gets the value of a bit in the filter that is at a given offset.
	 * @param offset
	 * @return
	 */
	private boolean getByte(int offset) {
		int byteOffset = offset / 8;
		int bitOffset = offset % 8;
		return ((filter[byteOffset] >> bitOffset) & 1) > 0;
	}
	
	public int getM() {
		return filter.length * 8;
	}
	
	public int getK() {
		return k;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(byte b : filter) {
			for(int i=0;i<8;i++) {
				sb.append(((b >> i) & 1) + ",");
			}
		}
		return sb.toString();
	}
}
