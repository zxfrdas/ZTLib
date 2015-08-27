package com.example.test;

public class TestItem {

	private String videoName;
	
	public TestItem() {
		
	}
	
	public TestItem(String name) {
		this();
		videoName = name;
	}
	
	public String getVideoName() {
		return videoName;
	}
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	@Override
	public boolean equals(Object o) {
		if (null != o && o instanceof TestItem) {
			TestItem item = (TestItem) o;
			videoName.equals(item.videoName);
			return videoName.equals(item.videoName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (videoName).hashCode();
	}
	
	
	
}
