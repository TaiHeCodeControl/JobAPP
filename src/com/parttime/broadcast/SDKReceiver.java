package com.parttime.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @date 2015-1-27
 * @time 15:23
 * @function 广播判断地图的key是否正确
 *
 */
public class SDKReceiver extends BroadcastReceiver {
	public void onReceive(Context mContext, Intent intent) {
		String keyString = intent.getAction();
		if (keyString.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
			Log.i("SDKReceiver", "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
		} else if (keyString.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
			Utils.ShowToast(mContext, "网络出错");
		}
	}
}