package com.majestyk.buzr.adapter;

import java.util.List;

import android.widget.GridView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.majestyk.buzr.apis.OnReportResultListener;

public abstract class ImageGridAdapterManager<V> implements OnRefreshListener2<GridView>, OnReportResultListener<V> {
	public static interface MyGridAdapterInterface<V> {
		void addValues(List<V> values);
		void clear();
	}

	final private PullToRefreshGridView gridView;
	final MyGridAdapterInterface<V> adapter;
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

	public ImageGridAdapterManager(PullToRefreshGridView gridView, MyGridAdapterInterface<V> adapter) {
		this.gridView = gridView;
		this.adapter = adapter;

		this.gridView.setOnRefreshListener(this);
	}

	/*
	 * Call api which has OnReportResultListener set to this
	 */
	public abstract void launchApiCall();

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
		adapter.clear();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
		launchApiCall();
	}

	@Override
	public void onReportResult(boolean success, List<V> values) {
		if(success)
			adapter.addValues(values);

		gridView.onRefreshComplete();
	}
}
