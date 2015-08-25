package com.example.test;

import java.util.Collection;

public interface IPublisher<T> {

	public interface INotifier<T> {
		void onItemPublish(Collection<T> items);
	}
	
	void request(String name, INotifier<T> notifier);
	
	 
	
}
