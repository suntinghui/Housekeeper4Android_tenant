package com.ares.house.dto.app;

import java.util.List;

public class UserLandLordAppDto {

	private List<ImageAppDto> topImgs;

	private String downLoadUrl;

	public List<ImageAppDto> getTopImgs() {
		return topImgs;
	}

	public void setTopImgs(List<ImageAppDto> topImgs) {
		this.topImgs = topImgs;
	}

	public String getDownLoadUrl() {
		return downLoadUrl;
	}

	public void setDownLoadUrl(String downLoadUrl) {
		this.downLoadUrl = downLoadUrl;
	}

}
