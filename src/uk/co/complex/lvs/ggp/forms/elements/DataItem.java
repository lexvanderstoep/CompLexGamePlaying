package uk.co.complex.lvs.ggp.forms.elements;

import uk.co.complex.lvs.ggp.StateMachine;

public class DataItem {
	private String mTitle;
	private Object mData;
	
	public DataItem(String title, Object data) {
		mTitle = title;
		mData = data;
	}

	public String getTitle() {
		return mTitle;
	}
	
	public Object getData() {
		return mData;
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
}
