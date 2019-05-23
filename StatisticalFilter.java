package bloomfilter;

import java.util.Collection;

/**
 * A statistical filter is a tool to which elements can be added and then later checked
 * for their presence. They are statistical in nature so can not guarentee correct
 * identification or recall.
 * 
 * @author bkievitk
 *
 * @param <E>
 */
public abstract class StatisticalFilter<E> {
	public abstract boolean add(E e);
	public abstract void clear();
	public abstract int size();
	
	/**
	 * Apply the add function to a collection.
	 * @param cs Collection of items to add.
	 * @return False if any add failed, otherwise True.
	 */
	public boolean addAll(Collection<? extends E> cs) {
		boolean addFailed = false;
		for(E c : cs) {
			if(!add(c)) {
				addFailed = true;
			}
		}
		return !addFailed;
	}
	
	/**
	 * Tests if the filter likely contains an element.
	 * @param e Element to test.
	 * @return A measure of the likelihood of the element to have been stored in the set.
	 */
	public abstract double probabilityContains(E e);
	
	/**
	 * Create a new statistical filter.
	 * @param sizeEstimate An estimate of the number of objects to be stored here.
	 * @param bytes The number of bytes allowed to represent the storage.
	 */
	public StatisticalFilter(int sizeEstimate, int bytes) {
		if(sizeEstimate <= 0) {
			throw new IllegalArgumentException("Size estimate must be 1 or more.");
		}
		if(bytes <= 0) {
			throw new IllegalArgumentException("Bytes to use must be 1 or more.");
		}
	}
}
