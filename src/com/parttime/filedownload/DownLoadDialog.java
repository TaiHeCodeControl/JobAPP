package com.parttime.filedownload;

import java.util.List;

import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.VersionInfoStruct;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


public class DownLoadDialog {
	// 检查机子是否安装的有Adobe Flash相关APK
	public static boolean checkinstallornotadobeflashapk(Context checkcontext,String packageType) {
		PackageManager pm = checkcontext.getPackageManager();
		List<PackageInfo> infoList = pm
				.getInstalledPackages(PackageManager.GET_SERVICES);
		for (PackageInfo info : infoList) {
			if (packageType.equals(info.packageName)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 下载apk的对话框
	 * @param context
	 * @param uri
	 * @param name
	 */
	public static void DownLoadApkTips(final Context context,final String uri,final VersionInfoStruct vis) {
		
		final AlertDialog tips_dlg = new AlertDialog.Builder(context).create();
		tips_dlg.show();
		Window window = tips_dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.new_app_download_tips_dialog);
		// 为确认按钮添加事件,执行退出应用操作
		TextView tips = (TextView) window.findViewById(R.id.umeng_update_tips);
		tips.setText("版本："+vis.mNewVersionName+"\n"+"时间："+vis.mVersionNewTime);
		TextView content = (TextView) window.findViewById(R.id.umeng_update_content);
		content.setText("更新内容："+vis.mNewContent);
		Button ok = (Button) window.findViewById(R.id.umeng_update_id_ok);
		final CheckBox checkBox = (CheckBox) window.findViewById(R.id.umeng_update_id_check);
		checkBox.setVisibility(View.GONE);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					if (Submit.isNetworkAvailable(context)) {
						Toast.makeText(context, "正在下载，请稍后……", Toast.LENGTH_LONG).show();
						// 下载的文件路径，随机的一个端口号，apk名称
						DownloadService.downNewFile(uri, 1078,"蛋壳儿"+Utils.getVersionName(context)+".apk ");
					
					} else {
						Toast.makeText(context, "对不起！没有可用网络",Toast.LENGTH_SHORT).show();
					}
				tips_dlg.cancel();
			}
		});
		// close alert dialog
		Button cancel = (Button) window.findViewById(R.id.umeng_update_id_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tips_dlg.cancel();
			}
		});
		
	}
}
