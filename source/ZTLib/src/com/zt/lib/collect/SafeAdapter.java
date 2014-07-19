package com.zt.lib.collect;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;

import com.zt.lib.exceptions.NullArgException;

/**
 * 基于{@code BaseAdapter}所创建的{@code Adapter}类。
 * <p>
 * 重新实现了设置数据，获取数据的对外接口，规避了各种空指针、非法参数错误，并且是线程安全的。
 * 
 * @author zhaotong
 * @param <T>
 *            {@code Adapter}中持有的数据类型
 */
public abstract class SafeAdapter<T> extends BaseAdapter implements OnItemClickListener,
		OnItemSelectedListener, OnFocusChangeListener {
	private static final String TAG = SafeAdapter.class.getSimpleName();
	/**
	 * 持有列表每个Item所需要的View元素的类。
	 * 
	 * @author zhaotong
	 */
	protected static abstract class ViewHolder {
		/**
		 * 恢复View的初始状态
		 */
		public abstract void reset();
	}

	private final Object mLock = new Object();
	private List<T> mDatas;
	private LayoutInflater mInflater;
	private WeakReference<Context> mContextRef;
	private WeakReference<Handler> mHandlerRef;
	private int mContainerResId;
	private boolean mIsAutoNotify;

	/**
	 * 构造此{@code Adapter}类。
	 * 
	 * @param context
	 *            应用当前界面上下文
	 * @param containerResId
	 *            {@code Adapter}中每个Item的容器视图的{@code Layout}ID
	 */
	public SafeAdapter(Context context, int containerResId)
	{
		mContextRef = new WeakReference<Context>(context);
		mHandlerRef = new WeakReference<Handler>(new Handler(Looper.getMainLooper()));
		mInflater = (LayoutInflater) mContextRef.get().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContainerResId = containerResId;
		mDatas = new ArrayList<T>();
		mIsAutoNotify = false;
	}

	public SafeAdapter(Context context, int containerResId, List<T> datas)
			throws NullArgException
	{
		this(context, containerResId);
		setDatas(mDatas);
	}

	public SafeAdapter(Context context, int containerResId, T[] datas) throws NullArgException
	{
		this(context, containerResId);
		setDatas(mDatas);
	}
	
	/**
	 * 设置是否在数据更新后自动调用notify刷新。
	 * @param isOn true则自动调用，反之false
	 */
	public void setAutoNotifyOnOff(boolean isOn)
	{
		mIsAutoNotify = isOn;
	}

	/**
	 * 设置Adapter中填充的数据
	 * 
	 * @param datas
	 *            试图填充的数据的集合
	 * @throws NullArgException
	 *             集合为空值或内部无元素时抛出
	 */
	public void setDatas(List<T> datas) throws NullArgException
	{
		if (null == datas || datas.isEmpty())
			throw new NullArgException();
		synchronized (mLock) {
			if (null == mDatas) {
				mDatas = new ArrayList<T>();
			}
			mDatas.clear();
			for (T data : datas) {
				mDatas.add(data);
			}
		}
		if (mIsAutoNotify) {
			notifyChanged();
		}
	}

	/**
	 * 设置Adapter中填充的数据
	 * 
	 * @param datas
	 *            试图填充的数据的集合
	 * @throws NullArgException
	 *             集合为空值或内部无元素时抛出
	 */
	public void setDatas(T[] datas) throws NullArgException
	{
		if (null == datas || 0 == datas.length)
			throw new NullArgException();
		synchronized (mDatas) {
			mDatas.clear();
			for (T data : datas) {
				mDatas.add(data);
			}
		}
		if (mIsAutoNotify) {
			notifyChanged();
		}
	}

	/**
	 * 想Adapter中加入填充的数据
	 * 
	 * @param data
	 *            试图加入的数据
	 * @throws NullArgException
	 *             数据为空值时抛出
	 */
	public void addData(T data) throws NullArgException
	{
		if (null == data)
			throw new NullArgException();
		synchronized (mDatas) {
			mDatas.add(data);
		}
		if (mIsAutoNotify) {
			notifyChanged();
		}
	}

	/**
	 * 从数据集合中移除指定索引值的数据
	 * 
	 * @param index
	 *            试图移除的数据在集合中的索引
	 */
	public void remove(int index)
	{
		synchronized (mDatas) {
			if (null != mDatas && index >= 0 && index < mDatas.size()) {
				mDatas.remove(index);
			}
		}
		if (mIsAutoNotify) {
			notifyChanged();
		}
	}

	/**
	 * 清空Adapter中的填充数据
	 */
	public void clearData()
	{
		synchronized (mDatas) {
			mDatas.clear();
		}
		if (mIsAutoNotify) {
			notifyChanged();
		}
	}

	@Override
	public int getCount()
	{
		synchronized (mDatas) {
			return mDatas.size();
		}
	}

	@Override
	public T getItem(int position)
	{
		synchronized (mDatas) {
			if (!mDatas.isEmpty() && position >= 0
					&& position < mDatas.size()) {
				return mDatas.get(position);
			}
		}
		return null;
	}

	@Override
	public abstract long getItemId(int position);

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (null == convertView) {
			convertView = mInflater.inflate(mContainerResId, null);
			viewHolder = createViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.reset();
		bindView(viewHolder, position);
		return convertView;
	}

	/**
	 * 创建包含当前Item所需视图元素的类，并赋初值
	 * 
	 * <pre class="prettyprint">
	 * 示例：<p>ViewHolder mHolder = new ViewHolder()</p>
	 * <p>mHolder.xxx = (XXX)convertView.findViewById(R.id.xxxxxx);</P>
	 * <p>return mHolder;
	 * </pre>
	 * 
	 * @param convertView
	 *            Item容器视图，{@code ViewHolder}内部各个视图由此解析
	 * @return 赋值完毕后的{@code ViewHolder}类
	 * @see ViewHolder
	 */
	public abstract ViewHolder createViewHolder(View convertView);

	/**
	 * 将数据与当前Item中视图元素进行绑定。
	 * 
	 * <pre class="prettyprint">
	 * 示例：<p>Data data = getItem(position);
	 * <p>if (null != data) {
	 * <p>	ViewHolder mHolder = (XXX)viewHolder;
	 * <p>	mHolder.xxx.setXXX(data.xxx);
	 * <p>}
	 * </pre>
	 * 
	 * @param viewHolder
	 *            包含当前Item中各个视图元素的类
	 * @param position
	 *            试图进行数据操作的Item在Adapter中的位置
	 * 
	 */
	public abstract void bindView(ViewHolder viewHolder, int position);
	
	public void notifyChanged()
	{
		if (null != mHandlerRef && null != mHandlerRef.get()) {
			mHandlerRef.get().post(new Runnable() {
				
				@Override
				public void run()
				{
					SafeAdapter.this.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public void notifyDataSetChanged()
	{
		if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
			super.notifyDataSetChanged();
		} else {
			Log.e(TAG, "线程:id = " + Thread.currentThread().getId() + ", name = "
					+ Thread.currentThread().getName() + " 试图刷新UI，被阻止");
		}
	}

}
