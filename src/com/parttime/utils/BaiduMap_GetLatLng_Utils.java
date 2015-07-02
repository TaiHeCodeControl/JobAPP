package com.parttime.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
/**
 * 
 * @author 灰色的寂寞
 * @date 2015-1-30
 * @time 11:25
 * @fucntion 通过传来的city和address得到LatLng
 *
 */
public class BaiduMap_GetLatLng_Utils implements OnGetGeoCoderResultListener{
	private static final String TAG =BaiduMap_GetLatLng_Utils.class.getSimpleName(); 
	
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	public static LatLng mLatLng=null;
	public static LatLng  mLocal_LatLng=null;
	
	public BaiduMap_GetLatLng_Utils(String city, String address) {
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		mSearch.geocode(new GeoCodeOption().city(city).address(address));
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Utils.ShowLog(TAG, "抱歉，未能找到结果");
			return;
		}
		String strInfo = String.format("纬度：%f 经度：%f",result.getLocation().latitude, result.getLocation().longitude);
		Utils.ShowLog(TAG, strInfo);
		mLatLng =  result.getLocation();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Utils.ShowLog(TAG, "抱歉，未能找到结果");
			return;
		}
		Utils.ShowLog(TAG, result.getAddress());
		mLatLng =  result.getLocation();
	}
	
	
}
