package com.parttime.activity;


import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.parttime.broadcast.SDKReceiver;
import com.parttime.parttimejob.R;
import com.parttime.utils.BaiduMap_GetLatLng_Utils;
/**
 * 
 * @author 灰色的寂寞
 * @date 2015-1-27
 * @time 15：19
 * @function 调用百度的接口，通过传过来的字符串（地区），在地图中小水滴标注位置
 *
 */
public class BaiduMap_Activity extends Activity{
	private Context mContext;
	private LatLng mLocalLatLng;
	
	/* 定位相关部分*/
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;

	/*基础地图部分*/
	MapView mMapView;
	BaiduMap mBaiduMap;
	
	/*添加定位模式部分的按钮*/
	Button requestLocButton;
	boolean isFirstLoc = true;// 是否首次定位
	
	
	
	/*  表示其他位置的图标         初始化全局 bitmap 信息，不用时及时 recycle*/
	BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.other_poi_mark_icon);
	/*  表示当前位置的图标         修改为自定义marker,自定义一个图标（mCurrentMarker=null为默认的）*/
	BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.current_poi_marked_icon);
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SDKInitializer.initialize(getApplicationContext());  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.baidumap_layout);
		mContext = this;
		mLocalLatLng = BaiduMap_GetLatLng_Utils.mLocal_LatLng;
		TestMapSdkOrNetwork();
		
		if(mLocalLatLng==null){//如果当前位置由于某种原因没有获取到，则默认定位到'游美情景教育公司'
			mLocalLatLng = new LatLng(40.105156,116.298433);
		}
		
		Intent intent = getIntent();
		Double lat = intent.getDoubleExtra("lat",mLocalLatLng.latitude);//默认定位到当前
		Double lng = intent.getDoubleExtra("lng",mLocalLatLng.longitude);
		LatLng ll = new LatLng(lat,lng);
		
		initLocationModeBlock();
		/*基础地图初始化*/
		mMapView = (MapView) findViewById(R.id.partmapView);
		mBaiduMap = mMapView.getMap();
		/*地图默认的尺寸设置*/
		// MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		// mBaiduMap.setMapStatus(msu);
		
		initLocationBlock();
		initOverlay(ll);
	}
	
	
	/**
	 * 显示定位模式按钮
	 */
	public void initLocationModeBlock(){
		requestLocButton = (Button) findViewById(R.id.location_mode);
		requestLocButton.setVisibility(View.VISIBLE);
		mCurrentMode = LocationMode.NORMAL;
		requestLocButton.setText("普通");
		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
				case NORMAL:
					requestLocButton.setText("跟随");
					mCurrentMode = LocationMode.FOLLOWING;
					mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
					break;
				case COMPASS:
					requestLocButton.setText("普通");
					mCurrentMode = LocationMode.NORMAL;
					mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
					break;
				case FOLLOWING:
					requestLocButton.setText("罗盘");
					mCurrentMode = LocationMode.COMPASS;
					mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
					break;
				}
			}
		};
		requestLocButton.setOnClickListener(btnClickListener);
	}
	
	/**
	 * 定位部分
	 */
	public void initLocationBlock(){
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(mContext);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);//设置范围
		mLocClient.setLocOption(option);
		mLocClient.start();
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
		
		AnimatToLocation(mLocalLatLng);
	}
	

	/**
	 * 添加坐标点显示
	 * @param llA
	 */
	public void initOverlay(LatLng llA) {

		OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA).zIndex(9).draggable(true);
		Marker mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));

		LatLngBounds bounds = new LatLngBounds.Builder().include(mLocalLatLng).include(mLocalLatLng).build();
		MapStatusUpdate mapstatus = MapStatusUpdateFactory.newLatLng(bounds.getCenter());
		mBaiduMap.setMapStatus(mapstatus);

		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			public void onMarkerDrag(Marker marker) {
			}
			public void onMarkerDragEnd(Marker marker) {
			}
			public void onMarkerDragStart(Marker marker) {
			}
		});
	}

	/**
	 * 定位到固定的经纬度
	 */
	public void AnimatToLocation(LatLng latlng){
		if(latlng!=null){
			MyLocationData locData = new MyLocationData.Builder()
			//.accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
			.direction(100).latitude(latlng.latitude)
			.longitude(latlng.longitude).build();
			mBaiduMap.setMyLocationData(locData);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latlng);
			mBaiduMap.animateMapStatus(u);
//			DistanceUtil.getDistance(,);
		}
	}
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			// MyLocationData locData = new
			// MyLocationData.Builder().accuracy(location.getRadius())
			// // 此处设置开发者获取到的方向信息，顺时针0-360
			// .direction(100).latitude(location.getLatitude())
			// .longitude(location.getLongitude()).build();
			// mBaiduMap.setMyLocationData(locData);
			// if (isFirstLoc) {
			// isFirstLoc = false;
			// LatLng ll = new
			// LatLng(location.getLatitude(),location.getLongitude());
			// MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			// mBaiduMap.animateMapStatus(u);
			// }
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	/**
	 * 添加地图上固定坐标的文字描述
	 * @param overtext
	 */
	public void AddTextOverlay(String overtext){
		//定义文字所显示的坐标点  
		LatLng llText = new LatLng(39.86923, 116.397428);  
		//构建文字Option对象，用于在地图上添加文字  
		OverlayOptions textOption = new TextOptions()  
		    .bgColor(0xAAFFFF00)  
		    .fontSize(24)  
		    .fontColor(0xFFFF00FF)  
		    .text(overtext)  
		    .rotate(-30)  
		    .position(llText);  
		//在地图上添加该文字对象并显示  
		mBaiduMap.addOverlay(textOption);
	}
	
	/**
	 * 添加几何图形
	 * 地图SDK提供多种结合图形覆盖物，利用这些图形，可帮助您构建更加丰富多彩的地图应用。目前提供的几何图形有：
	 * 点（Dot）、折线（Polyline）、弧线（Arc）、圆（Circle）、多边形（Polygon）。 
	 */
	public void AddOverlayGeometry(){
		
		//定义多边形的五个顶点  
		LatLng pt1 = new LatLng(39.93923, 116.357428);  
		LatLng pt2 = new LatLng(39.91923, 116.327428);  
		LatLng pt3 = new LatLng(39.89923, 116.347428);  
		LatLng pt4 = new LatLng(39.89923, 116.367428);  
		LatLng pt5 = new LatLng(39.91923, 116.387428);  
		List<LatLng> pts = new ArrayList<LatLng>();  
		pts.add(pt1);  
		pts.add(pt2);  
		pts.add(pt3);  
		pts.add(pt4);  
		pts.add(pt5);  
		//构建用户绘制多边形的Option对象  
		OverlayOptions polygonOption = new PolygonOptions()  
		    .points(pts)  
		    .stroke(new Stroke(5, 0xAA00FF00))  
		    .fillColor(0xAAFFFF00);  
		//在地图上添加多边形Option，用于显示  
		mBaiduMap.addOverlay(polygonOption);
		
	}
	
	/**
	 * 弹出窗覆盖物
	 */
	public void AddPopupView(){
		//创建InfoWindow展示的view  
		Button button = new Button(getApplicationContext());  
		button.setBackgroundResource(R.drawable.apply_icon);  
		//定义用于显示该InfoWindow的坐标点  
		LatLng pt = new LatLng(39.86923, 116.397428);  
		//创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量 
		InfoWindow mInfoWindow = new InfoWindow(button, pt, -47);  
		//显示InfoWindow  
		mBaiduMap.showInfoWindow(mInfoWindow);
	}
	
	
	public void startNavi(View view) {  
		LatLng pt1 = new LatLng(39.86923, 116.397428);  
		LatLng pt2 = new LatLng(39.86923, 116.397428);  
	    // 构建 导航参数  
	    NaviPara para = new NaviPara();  
	    para.startPoint = pt1;  
	    para.startName = "从这里开始";  
	    para.endPoint = pt2;  
	    para.endName = "到这里结束";  
	    try {  
	        BaiduMapNavigation.openBaiduMapNavi(para, this);  
	    } catch (BaiduMapAppNotSupportNaviException e) {  
	        e.printStackTrace();  
	    }  
	    
		// 坐标转换 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
		// CoordinateConverter converter = new CoordinateConverter();
		// converter.from(CoordType.COMMON);
		// // sourceLatLng待转换坐标
		// converter.coord(sourceLatLng);
		// LatLng desLatLng = converter.convert();
		//
		// // 将GPS设备采集的原始GPS坐标转换成百度坐标
		// CoordinateConverter converter = new CoordinateConverter();
		// converter.from(CoordType.GPS);
		// // sourceLatLng待转换坐标
		// converter.coord(sourceLatLng);
		// LatLng desLatLng = converter.convert();
	}
	
	private SDKReceiver mReceiver;
	public void TestMapSdkOrNetwork(){
		// 注册 SDK 广播监听者,使用地图之前需要判断key是否正确和网络是否可用
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		TestMapSdkOrNetwork();
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
		
		//marker.remove();   //调用Marker对象的remove方法实现指定marker的删除
		// 退出时销毁定位
		if(mLocClient!=null)
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		super.onDestroy();
		mMapView.onDestroy();
	}


}
