package com.nongguanjia.doctorTian;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nongguanjia.doctorTian.http.DoctorTianRestClient;
import com.nongguanjia.doctorTian.utils.CommonConstant;
import com.nongguanjia.doctorTian.utils.MD5Util;

/**
 * @author tx
 * 注册
 */
public class RegistActivity extends Activity implements OnClickListener{
	private Button btn_regist;
	private ProgressDialog mDialog;
	private Button btn_get_code;
	private EditText ed_phone, ed_psd, ed_psd_again, ed_verify_code;
	private String verifyCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist);
		
		init();
	}
	
	private void init(){
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("正在加载请稍后...");
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(true);
		
		btn_regist = (Button)findViewById(R.id.btn_regist);
		btn_get_code = (Button)findViewById(R.id.get_code);
		ed_phone = (EditText)findViewById(R.id.edit_phone); 
		ed_psd = (EditText)findViewById(R.id.edit_psd);
		ed_psd_again = (EditText)findViewById(R.id.edit_psd_again);
		ed_verify_code = (EditText)findViewById(R.id.edit_verify_code);
		
		btn_regist.setOnClickListener(this);
		btn_get_code.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_regist:
			//添加验证
			if(!TextUtils.isEmpty(ed_phone.getText())){
				if(!TextUtils.isEmpty(ed_psd.getText())){
					if(!TextUtils.isEmpty(ed_psd_again.getText())){
						if(!TextUtils.isEmpty(ed_verify_code.getText())){
							if(ed_verify_code.getText().toString().equals(verifyCode)){
								mDialog.show();
								String psd = MD5Util.GetMD5Code(ed_psd.getText().toString());
								regist(ed_phone.getText().toString(), psd);
							}else{
								Toast.makeText(getApplicationContext(), "验证码失效，请重新获取", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "请再次输入密码", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "请输入手机号码", Toast.LENGTH_SHORT).show();
			}
			
			break;
		case R.id.get_code:
			if(!TextUtils.isEmpty(ed_phone.getText())){
				mDialog.show();
				getCode(ed_phone.getText().toString());
			}else{
				Toast.makeText(getApplicationContext(), "请输入手机号码", Toast.LENGTH_SHORT).show();
			}
			
			break;

		default:
			break;
		}
	}
	
	
	private void getCode(String phoneNum){
		String url = CommonConstant.verifycodes + "/" + phoneNum;
		DoctorTianRestClient.get(url, null, new JsonHttpResponseHandler(){

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				Toast.makeText(getApplicationContext(), "请求接口异常", Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, responseString, throwable);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				mDialog.dismiss();
				//解析应答数据
				try {
					if(response.getJSONObject("VerifyCodes").getString("returnCode").equals("1")){
						verifyCode = response.getJSONObject("VerifyCodes").getString("verifyCode");
					}else{
						Toast.makeText(getApplicationContext(), "获取验证码失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				super.onSuccess(statusCode, headers, response);
			}
			
		});
	}
	
	
	private void regist(String name, String psd){
		String url = CommonConstant.regist + "/" + name + "," + psd;
		
		DoctorTianRestClient.get(url, null, new JsonHttpResponseHandler(){

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				Toast.makeText(getApplicationContext(), "请求接口异常", Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, responseString, throwable);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				//解析应答数据
				try {
					if(new JSONArray(response.getString("AddUser")).getJSONObject(0).getString("returnCode").equals("1")){
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						startActivity(intent);
					}else{
						Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				super.onSuccess(statusCode, headers, response);
			}
			
		});
	}
	
	

}
