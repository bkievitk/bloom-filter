package bloomfilter;

/**
 * This class allows the StatisticalFilters to use different hash functions than those
 * built into Java Objects. This allows for more efficient hash functions specific to
 * a given filter type.
 * 
 * @author bkievitk
 *
 * @param <E>
 */
public abstract class HashFunction<E> {
	public abstract int hashCode(E e, int seed);
}
