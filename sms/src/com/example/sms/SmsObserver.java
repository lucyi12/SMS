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
	private Handler mHandler; // ����UI�߳�

	public SmsObserver(Context context, Handler handler) {
		super(handler);
		mContext = context;
		mHandler = handler;
	}

	/**
	 * ����������Uri�����ı�ʱ���ͻ�ص��˷���
	 * 
	 * @param selfChange
	 *            ��ֵ���岻�� һ������¸ûص�ֵfalse
	 */
	@Override
	public void onChange(boolean selfChange) {

		// ��ȡ�������еĶ���
		Uri outSMSUri = Uri.parse("content://sms/sent");

		Cursor c = mContext.getContentResolver().query(outSMSUri, null, null,null, "date desc");

		if (c != null) {

			Log.i(TAG, "the number of send is" + c.getCount());

			StringBuilder sb = new StringBuilder();
			// ��ȡ���ݿ��е�һ������
			boolean hasDone = false;
			while (c.moveToNext()) {
				String address = c.getString(c.getColumnIndex("address"));
				String body = c.getString(c.getColumnIndex("body"));
				System.out.println("@@@@@@@body" + body);
				/*
				 * ��������Կy���Լ��յ�������Ϣʱ�Զ�����
				 * ��Կ������Ϣ��yyy��xxx...������ʽ���ͣ�������Ϣ�ԡ�yyyy��xxx...������ʽ����
				 */
				String[] sbody = body.split(":");

				if (sbody.length != 1) {
					if (sbody[0].equals("yyyy")) {
						// ������ת��Ϊ����
						String enbody = body.substring(5, body.length());
						System.out.println("@@@@@@@enbody" + enbody);
						String name = address.substring(0,address.length());//1008611
						String privateKey = "";
						if (name.equals("")) {
							Toast.makeText(mContext, "δ������Կ", 8000).show();
						} else {
							privateKey = FileUtil.readFileSdcard(FileUtil.File_NAME2+ name + ".txt");//1008611
							if (privateKey.equals("")) {
								Toast.makeText(mContext, "hhhhhδ������Կ", 8000).show();
							} else {
								// �ӱ����ļ��ж�ȡ�����˵Ĺ�Կ
								String userName = FileUtil.readFileSdcard(FileUtil.Folder_NAME+ "/me.txt");
								String publicKey = "";
								publicKey = FileUtil.readFileSdcard(FileUtil.File_NAME+ userName + ".txt");//9667
								if (!publicKey.equals("")) {
									// ���ݷ����˵Ĺ�Կ�Լ�����˽Կ���н���
									System.out.println("@@@@@@@@publicKey"+ publicKey);
									DHkey dhkey = new DHkey(privateKey);
									BigInteger k = dhkey.getK(publicKey);
									System.out.println("@@@@@@@@K" + k);

									DESUtil des = new DESUtil();
									body = des.DESDecode(k.toString(), enbody);
									System.out.println("@########@@body" + body);
								} else {
									Toast.makeText(mContext, "�޷����˹�Կ����֤", 8000).show();
								}
							}
						}
					}
				}
				sb.append(address).append("&&&&" + body);
				if (sb.toString() != null)// && body.equals("��startMyActivity��"
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