package com.majestyk.buzr.adapter;

import java.util.List;

import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.majestyk.buzr.apis.OnReportResultListener;

public abstract class ImageListAdapterManager<V> implements OnRefreshListener2<ListView>, OnReportResultListener<V> {
	public static interface MyListAdapterInterface<V> {
		void addValues(List<V> values);
		void clear();
	}

	final private PullToRefreshListView listView;
	final MyListAdapterInterface<V> adapter;
	private final int pageSize = 18;
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

	public ImageListAdapterManager(PullToRefreshListView listView, MyListAdapterInterface<V> adapter) {
		this.listView = listView;
		this.adapter = adapter;

		this.listView.setOnRefreshListener(this);
	}

	/*
	 * Call api which has OnReportResultListener set to this
	 */
	public abstract void launchApiCall();

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
