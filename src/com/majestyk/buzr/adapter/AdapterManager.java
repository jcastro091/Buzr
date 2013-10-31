package com.majestyk.buzr.adapter;

import java.util.List;

import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.majestyk.buzr.apis.OnReportResultListener;

public abstract class AdapterManager<V> implements OnRefreshListener2<ListView>, OnReportResultListener<V> {
	public static interface MyAdapterInterface<V> {
		void addValues(List<V> values);
		void clear();
	}

	final private PullToRefreshListView listView;
	final MyAdapterInterface<V> adapter;
	private final int pageSize = 20;
	private int currentPage = 1;

	protected int getPageSize() {
		return pageSize;
	}

	protected int getCurrentPage() {
		return currentPage;
	}
	
	protected int getStart() {
		return pageSize * currentPage;
	}

	protected int getNextPage() {
		return currentPage++;
	}

	protected void getFirstPage() {
		currentPage = 1;
	}

	public AdapterManager(PullToRefreshListView listView, MyAdapterInterface<V> adapter) {
		this.listView = listView;
		this.adapter = adapter;

		this.listView.setOnRefreshListener(this);
		
//		launchApiCall();
	}

	/*
	 * Call api which has OnReportResultListener set to this
	 */
	public abstract void launchApiCall();
	public abstract void reset();

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		adapter.clear();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		launchApiCall();
	}

	@Override
	public void onReportResult(boolean success, List<V> values) {
		if(success)
			adapter.addValues(values);
			
		listView.onRefreshComplete();
	}
}
