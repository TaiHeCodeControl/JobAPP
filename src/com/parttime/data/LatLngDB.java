package com.parttime.data;

import com.parttime.data.PartTimeColumns.CompanyLatLngColumns;
import com.parttime.data.PartTimeColumns.PersonalLatLngColumns;
import com.parttime.struct.PartTimeStruct.CompanyLatLngStruct;
import com.parttime.struct.PartTimeStruct.PersonalLatLngStruct;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
/**
 * 
 * @author 灰色的寂寞
 * @date 2015-1-31
 * @time 16:29
 * @function 经纬度类存储数据到数据库
 *
 */
public class LatLngDB {
	private  Context context;
	private static final String TAG = LatLngDB.class.getSimpleName();
	private static LatLngDB mPartTimeDB = null; 
	
	public LatLngDB(Context mContext) {
		this.context = mContext;
	}

	
	public static LatLngDB getInstance(Context context) {
		if(mPartTimeDB == null){
			mPartTimeDB = new LatLngDB(context);
		}
		return mPartTimeDB;
	}
	
	
	/**
	 * 添加企业的所有经纬度
	 * @param context
	 * @return
	 */
	public long AddCompanyLatLng(Context context,CompanyLatLngStruct struct){
		if(!JudgeCompanyLatLng(struct)){
			ContentValues values = new ContentValues();
			
			values.put(CompanyLatLngColumns.company_id, struct.company_id);
			values.put(CompanyLatLngColumns.company_lat, struct.company_lat);
			values.put(CompanyLatLngColumns.company_lng, struct.company_lng);
			
			Uri Evturi = context.getContentResolver().insert(CompanyLatLngColumns.CONTENT_URI, values);
			if(Evturi!=null){
				long etnid = ContentUris.parseId(Evturi);
				if (etnid < 0) {
					return -1;
				}
				return etnid;
			}else{
				return -1;
			}
		}else{
			return UpdateCompanyLatLng(struct);
		}
	}
	/**
	 * 判断企业对应的信息是否存在
	 * @param struct
	 * @return
	 */
	public boolean JudgeCompanyLatLng(CompanyLatLngStruct struct){
		Cursor curtmp = null;
		try{
			String selection = CompanyLatLngColumns.company_id + "= '"+ struct.company_id+ "'";
			curtmp = context.getContentResolver().query(CompanyLatLngColumns.CONTENT_URI, null, selection,null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		}finally{
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}
	/**
	 * 更新企业的经纬度
	 * @param struct
	 * @return
	 */
	public long UpdateCompanyLatLng(CompanyLatLngStruct struct){
		ContentValues values = new ContentValues();
		
		values.put(CompanyLatLngColumns.company_id, struct.company_id);
		values.put(CompanyLatLngColumns.company_lat, struct.company_lat);
		values.put(CompanyLatLngColumns.company_lng, struct.company_lng);
		int etnid = context.getContentResolver().update(CompanyLatLngColumns.CONTENT_URI,values, CompanyLatLngColumns.company_id + "= '" +  struct.company_id + "'", null);
		return etnid;
	}
	/**
	 * 获取企业对应的坐标
	 * @param context
	 * @param company_id
	 * @return
	 */
	public CompanyLatLngStruct GetCompanyLatLng(Context context,long company_id)
	{
		Cursor curtmp = null;
		CompanyLatLngStruct struct = new CompanyLatLngStruct();
		try {
			String selection = CompanyLatLngColumns.company_id + "= '"+ company_id+ "'";
			curtmp = context.getContentResolver().query(CompanyLatLngColumns.CONTENT_URI, null, selection,null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				curtmp.moveToNext();
				struct.company_id =curtmp.getLong(curtmp.getColumnIndexOrThrow(CompanyLatLngColumns.company_id));
				struct.company_lat =curtmp.getDouble(curtmp.getColumnIndexOrThrow(CompanyLatLngColumns.company_lat));
				struct.company_lng =curtmp.getDouble(curtmp.getColumnIndexOrThrow(CompanyLatLngColumns.company_lng));
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return struct;
	}
	
	/**
	 * 更新个人的经纬度
	 * @param struct
	 * @return
	 */
	public long UpdatePersonalLatLng(PersonalLatLngStruct struct){
		ContentValues values = new ContentValues();
		
		values.put(PersonalLatLngColumns.personal_id, struct.personal_id);
		values.put(PersonalLatLngColumns.personal_lat, struct.personal_lat);
		values.put(PersonalLatLngColumns.personal_lng, struct.personal_lng);
		int etnid = context.getContentResolver().update(PersonalLatLngColumns.CONTENT_URI,values, PersonalLatLngColumns.personal_id + "= '" +  struct.personal_id + "'", null);
		return etnid;
	}
	/**
	 * 添加个人的所有经纬度
	 * @param context
	 * @return
	 */
	public long AddPersonalLatLng(Context context,PersonalLatLngStruct struct){
		if(!JudgePersonalLatLng(struct)){
			ContentValues values = new ContentValues();
			
			values.put(PersonalLatLngColumns.personal_id, struct.personal_id);
			values.put(PersonalLatLngColumns.personal_lat, struct.personal_lat);
			values.put(PersonalLatLngColumns.personal_lng, struct.personal_lng);
			
			Uri Evturi = context.getContentResolver().insert(PersonalLatLngColumns.CONTENT_URI, values);
			if(Evturi!=null){
				long etnid = ContentUris.parseId(Evturi);
				if (etnid < 0) {
					return -1;
				}
				return etnid;
			}else{
				return -1;
			}
		}else{
			return UpdatePersonalLatLng(struct);
		}
	}
	/**
	 * 判断个人对应的信息是否存在
	 * @param struct
	 * @return
	 */
	public boolean JudgePersonalLatLng(PersonalLatLngStruct struct){
		Cursor curtmp = null;
		try{
			String selection = PersonalLatLngColumns.personal_id + "= '"+ struct.personal_id+ "'";
			curtmp = context.getContentResolver().query(PersonalLatLngColumns.CONTENT_URI, null, selection,null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		}finally{
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}
	/**
	 * 获取个人对应的坐标
	 * @param context
	 * @param personal_id
	 * @return
	 */
	public PersonalLatLngStruct GetPersonalLatLng(Context context,long personal_id){
		Cursor curtmp = null;
		PersonalLatLngStruct struct = new PersonalLatLngStruct();
		try {
			String selection = PersonalLatLngColumns.personal_id + "= '"+ personal_id+ "'";
			curtmp = context.getContentResolver().query(PersonalLatLngColumns.CONTENT_URI, null, selection,null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				curtmp.moveToNext();
				struct.personal_id =curtmp.getLong(curtmp.getColumnIndexOrThrow(PersonalLatLngColumns.personal_id));
				struct.personal_lat =curtmp.getDouble(curtmp.getColumnIndexOrThrow(PersonalLatLngColumns.personal_lat));
				struct.personal_lng =curtmp.getDouble(curtmp.getColumnIndexOrThrow(PersonalLatLngColumns.personal_lng));
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return struct;
	}
}
