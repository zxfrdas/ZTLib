package com.example.test;

import java.util.Collection;

/**
 * Publisher provides a type of data(we call publication). 
 * <p> User book data with specified name, publisher publish the related data to user and notify when data changed.
 * @param <T> The specified type of data
 */
public interface IPublisher<T> {

	/**
	 * Notify users who has booked the data that data has changed
	 * @param <T> The specified type of data
	 */
	public interface INotifier<T> {
		/**
		 * Notify publications has changed
		 * @param publications Data that user booked
		 */
		void onPublish(Collection<T> publications);
	}
	
	/**
	 * Book publications which are related to the specified name
	 * @param name The specified name
	 * @param notifier Use to notify user
	 */
	void book(String name, INotifier<T> notifier);
	
	/**
	 * Show details of the specified publication
	 * @param publication Publication to display details
	 */
	void displayDetails(T publication);
}
