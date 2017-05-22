package com.example.sms;

import java.math.BigInteger;
import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	EditText number, content;
	Button produce, send;
	SmsManager sManager;
	String privateKey1 = "";
//	String userName = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ��ȡSmsManager����Ϣ������
		sManager = SmsManager.getDefault();
		// ��ȡ�ı���Ͱ�ť
		number = (EditText) findViewById(R.id.number);
		content = (EditText) findViewById(R.id.content);
		produce = (Button) findViewById(R.id.produce);
		send = (Button) findViewById(R.id.send);
		
		// Ϊ��ť����¼�
		send.setOnClickListener(this);
		produce.setOnClickListener(this);
	}
	
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.produce:
			String userName = number.getText().toString();
			FileUtil.writeFileSdcard(FileUtil.Folder_NAME + "/me.txt", userName);
			DHkey dhkey1;
			privateKey1 = content.getText().toString(); //˽Կ����x
			BigInteger publicKey1;
			dhkey1 = new DHkey(privateKey1);//˽Կ����x
			FileUtil.writeFileSdcard(FileUtil.File_NAME2 + userName + ".txt",privateKey1.toString());
			publicKey1 = dhkey1.getY();//��ù�Կy
			// �������������ŵ������ļ�
			FileUtil.folderCreate();
			// ���õ����Լ��Ĺ�Կ�洢�������ļ������ֻ�������
			FileUtil.writeFileSdcard(FileUtil.File_NAME + userName + ".txt",publicKey1.toString());

			Toast.makeText(MainActivity.this, "��Կ����ɹ�", 8000).show();
			break;
		case R.id.send:
			String name = number.getText().toString();
			//FileUtil.writeFileSdcard(FileUtil.Folder_NAME + "/me.txt", name);
			String userName2 = FileUtil.readFileSdcard(FileUtil.Folder_NAME+ "/me.txt");
			String publicKey2 = ""; //�ӱ����ļ��ж�ȡ��ù�Կy
			publicKey2 = FileUtil.readFileSdcard(FileUtil.File_NAME + name+ ".txt");
			System.out.println("@@@@@@@@publicKey" + publicKey2);
			
			//number.setText(publicKey2);
			
			privateKey1 = FileUtil.readFileSdcard(FileUtil.File_NAME2 + userName2+ ".txt");
			DHkey dhkey2 = new DHkey(privateKey1);//����˽Կx
			BigInteger k = dhkey2.getK(publicKey2);//�����Կk
			System.out.println("@@@@@@@@K" + k);
			String message = content.getText().toString();
			System.out.println("@@@@@@@@message" + message);
			DESUtil Des = new DESUtil();
			String encode = "yyyy:"+Des.DESEncode(k.toString(), message);//����

			content.setText(encode);
			// ����һ��PendingIntent����
			PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0,
					new Intent(), 0);
			// ���Ͷ���
			if (encode.length() > 70) {
				ArrayList<String> msgs = sManager.divideMessage(encode);
				ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
				for (int i = 0; i < msgs.size(); i++) {
					sentIntents.add(pi);
				}
				sManager.sendMultipartTextMessage(number.getText().toString(),
						null, msgs, sentIntents, null);
			} else {
				sManager.sendTextMessage(number.getText().toString(), null,
						encode, pi, null);
			}
			// ��ʾ���ŷ��ͳɹ�
			Toast.makeText(MainActivity.this, "���ŷ��ͳɹ�", 8000).show();
			break;
		default:
			break;
		}

	}
}
