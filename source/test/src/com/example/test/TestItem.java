package com.example.test;

public class TestItem {

	private String videoName;
	private String videoSource;
	
	public TestItem() {
		
	}
	
	public TestItem(String name, String source) {
		this();
		videoName = name;
		videoSource = source;
	}
	
	public String getVideoName() {
		return videoName;
	}
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	public String getVideoSource() {
		return videoSource;
	}
	public void setVideoSource(String videoSource) {
		this.videoSource = videoSource;
	}

	@Override
	public boolean equals(Object o) {
		if (null != o && o instanceof TestItem) {
			TestItem item = (TestItem) o;
			videoName.equals(item.videoName);
			return videoName.equals(item.videoName) && videoSource.equals(videoSource);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (videoName + videoSource).hashCode();
	}
	
	
	
}
