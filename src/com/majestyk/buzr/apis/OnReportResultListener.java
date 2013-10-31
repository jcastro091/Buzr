package com.majestyk.buzr.apis;

import java.util.List;

public interface OnReportResultListener<V> {
	void onReportResult(boolean success, List<V> values);
}
