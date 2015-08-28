package com.example.test;

public class ComparableTestItem extends NumberComparableWrapper<TestItem, Integer> {

	public ComparableTestItem(TestItem item, Integer order) {
		super(item, order);
	}
	
	@Override
	public boolean equals(Object o) {
		return getOrigin().equals(o);
	}

	@Override
	public int hashCode() {
		return getOrigin().hashCode();
	}

}
