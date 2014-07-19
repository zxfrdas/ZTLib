package com.zt.lib.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.util.SparseArray;


/**
 * 最大个数固定的列表。已经饱和后，会对原有元素进行删除。调用方法不同删除方法不同。
 * <p>如果试图加入的新数据集合个数大于设定的最大个数，则抛出异常。
 * @author zhaotong
 * @param <E>
 */
public class FixedMaxArrayList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 2711132855882712357L;
	private SparseArray<IListIncreased<E>> mIncreasedCallbacks;
	private int mMax;
	
	/**
	 * 列表增加元素时回调接口
	 * @author zhaotong
	 * @param <E>
	 */
	public interface IListIncreased<E> {
		/**
		 * 列表增加元素时回调
		 * @param removed 为了保持最大个数不变而被删除的元素
		 * @param added 添加的元素
		 */
		public void onNewItemAdded(List<E> removed, List<E> added);
	}
	
	public FixedMaxArrayList(int max) {
		if (0 >= max) throw new IllegalArgumentException("最大个数必须大于0");
		mMax = max;
		mIncreasedCallbacks = new SparseArray<IListIncreased<E>>();
	}
	
	public int getMax()
	{
		return mMax;
	}
	
	/**
	 * 注册列表添加元素时的回调
	 * @param callback
	 */
	public void addListIncreasedCallback(IListIncreased<E> callback)
	{
		mIncreasedCallbacks.put(callback.hashCode(), callback);
	}
	
	/**
	 * 注销列表添加元素时的回调
	 * @param callback
	 */
	public void removeListIncreasedCallback(IListIncreased<E> callback)
	{
		mIncreasedCallbacks.delete(callback.hashCode());
	}
	
	/**
	 * 清除所有已经注册的列表添加元素时的回调
	 */
	public void clearListincreasedCallbacks()
	{
		mIncreasedCallbacks.clear();
	}
	
	@Override
	public boolean add(E object)
	{
		List<E> removes = new ArrayList<E>(1);
		List<E> adds = new ArrayList<E>(1);
		if (isFull()) {
			removes.add(remove(0));
		}
		super.add(object);
		adds.add(object);
		notifyCallbacks(removes, adds);
		//ArrayList中add(E object)方法返回值一直为true
		return true;
	}
	
	/**
	 * 添加至列表底部，从列表顶部开始删除。
	 * @param 数据
	 */
	public void addBottom(E object)
	{
		List<E> removes = new ArrayList<E>(1);
		List<E> adds = new ArrayList<E>(1);
		if (isFull()) {
			removes.add(remove(0));
		}
		super.add(object);
		adds.add(object);
		notifyCallbacks(removes, adds);
	}
	
	private boolean isFull()
	{
		return size() == mMax;
	}
	
	private void notifyCallbacks(List<E> removed, List<E> added)
	{
		int total = mIncreasedCallbacks.size();
		for (int i = 0; i < total; i ++) {
			mIncreasedCallbacks.valueAt(i).onNewItemAdded(removed, added);
		}
	}
	
	/**
	 * 添加至列表顶部，从列表底部开始删除。
	 * @param object 数据
	 */
	public void addTop(E object)
	{
		List<E> removed = new ArrayList<E>(1);
		List<E> added = new ArrayList<E>(1);
		if (isFull()) {
			removed.add(remove(size() - 1));
		}
		super.add(0, object);
		added.add(object);
		notifyCallbacks(removed, added);
	}

	@Deprecated
	@Override
	public void add(int index, E object)
	{
		List<E> removed = new ArrayList<E>(1);
		List<E> added = new ArrayList<E>(1);
		if (isFull()) {
			removed.add(remove(0));
		}
		super.add(index, object);
		added.add(object);
		notifyCallbacks(removed, added);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> collection)
	{
		int size = collection.size();
		if (size > mMax)
			throw new IllegalArgumentException("试图添加的集合个数：" + size
					+ "，超过上限：" + mMax);
		int over = size() + size - mMax;
		List<E> removed = new ArrayList<E>(over);
		List<E> added = new ArrayList<E>(size);
		if (over > 0) {
			for (int i = 0; i < over; i ++) {
				removed.add(remove(0));
			}
		}
		boolean modified = super.addAll(collection);
		if (modified) {
			added.addAll(collection);
		}
		notifyCallbacks(removed, added);
		return modified;
	}
	
	@Deprecated
	@Override
	public boolean addAll(int index, Collection<? extends E> collection)
	{
		int size = collection.size();
		if ((index + size) > mMax)
			throw new IllegalArgumentException("试图在index = " + index
					+ "处插入长度为" + size + "的数组, " + "超过上限：" + mMax);
		//TODO-算法没想好
		boolean modified = super.addAll(index, collection);
		return modified;
	}
	
}
