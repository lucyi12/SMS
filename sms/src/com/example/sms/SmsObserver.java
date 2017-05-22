package com.example.sms;

import java.math.BigInteger;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class SmsObserver extends ContentObserver {
	private static String TAG = "SMSContentObserver";

	private int MSG_OUTBOXCONTENT = 2;

	private Context mContext;
	private Handler mHandler; // 更新UI线程

	public SmsObserver(Context context, Handler handler) {
		super(handler);
		mContext = context;
		mHandler = handler;
	}

	/**
	 * 当所监听的Uri发生改变时，就会回调此方法
	 * 
	 * @param selfChange
	 *            此值意义不大 一般情况下该回调值false
	 */
	@Override
	public void onChange(boolean selfChange) {

		// 读取发件箱中的短信
		Uri outSMSUri = Uri.parse("content://sms/sent");

		Cursor c = mContext.getContentResolver().query(outSMSUri, null, null,null, "date desc");

		if (c != null) {

			Log.i(TAG, "the number of send is" + c.getCount());

			StringBuilder sb = new StringBuilder();
			// 读取数据库中第一条短信
			boolean hasDone = false;
			while (c.moveToNext()) {
				String address = c.getString(c.getColumnIndex("address"));
				String body = c.getString(c.getColumnIndex("body"));
				System.out.println("@@@@@@@body" + body);
				/*
				 * 处理交换公钥y，以及收到加密信息时自动解密
				 * 公钥交换信息“yyy：xxx...”的形式发送，加密信息以“yyyy：xxx...”的形式发送
				 */
				String[] sbody = body.split(":");

				if (sbody.length != 1) {
					if (sbody[0].equals("yyyy")) {
						// 将密文转化为明文
						String enbody = body.substring(5, body.length());
						System.out.println("@@@@@@@enbody" + enbody);
						String name = address.substring(0,address.length());//1008611
						String privateKey = "";
						if (name.equals("")) {
							Toast.makeText(mContext, "未创建密钥", 8000).show();
						} else {
							privateKey = FileUtil.readFileSdcard(FileUtil.File_NAME2+ name + ".txt");//1008611
							if (privateKey.equals("")) {
								Toast.makeText(mContext, "hhhhh未创建密钥", 8000).show();
							} else {
								// 从本地文件中读取发件人的公钥
								String userName = FileUtil.readFileSdcard(FileUtil.Folder_NAME+ "/me.txt");
								String publicKey = "";
								publicKey = FileUtil.readFileSdcard(FileUtil.File_NAME+ userName + ".txt");//9667
								if (!publicKey.equals("")) {
									// 根据发件人的公钥以及本地私钥进行解密
									System.out.println("@@@@@@@@publicKey"+ publicKey);
									DHkey dhkey = new DHkey(privateKey);
									BigInteger k = dhkey.getK(publicKey);
									System.out.println("@@@@@@@@K" + k);

									DESUtil des = new DESUtil();
									body = des.DESDecode(k.toString(), enbody);
									System.out.println("@########@@body" + body);
								} else {
									Toast.makeText(mContext, "无发件人公钥，查证", 8000).show();
								}
							}
						}
					}
				}
				sb.append(address).append("&&&&" + body);
				if (sb.toString() != null)// && body.equals("【startMyActivity】"
				{

					hasDone = true;
					break;
				}
				if (hasDone) {
					break;
				}
			}
			c.close();
			mHandler.obtainMessage(MSG_OUTBOXCONTENT, sb.toString())
					.sendToTarget();
		}
	}
}