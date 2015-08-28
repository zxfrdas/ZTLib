package com.example.test;

public class NumberComparableWrapper<T, E extends Number> implements Comparable<NumberComparableWrapper<T, E>> {
	private static final int BIGGER = 1;
	private static final int EQUAL = 0;
	
	private T origin;
	private E order;
	
	public NumberComparableWrapper() {
	}
	
	public NumberComparableWrapper(T origin, E order) {
		this();
		this.origin = origin;
		this.order = order;
	}
	
	public T getOrigin() {
		return origin;
	}
	
	public E getOrder() {
		return order;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(NumberComparableWrapper<T, E> another) {
		if (null == another) {
			return BIGGER;
		}
		if (this.equals(another)) {
			return EQUAL;
		}
		return ((Comparable<E>)this.order).compareTo(another.order);
		
	}

}
