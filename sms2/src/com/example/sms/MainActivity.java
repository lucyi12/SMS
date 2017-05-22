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
		// 获取SmsManager即信息管理器
		sManager = SmsManager.getDefault();
		// 获取文本框和按钮
		number = (EditText) findViewById(R.id.number);
		content = (EditText) findViewById(R.id.content);
		produce = (Button) findViewById(R.id.produce);
		send = (Button) findViewById(R.id.send);
		
		// 为按钮添加事件
		send.setOnClickListener(this);
		produce.setOnClickListener(this);
	}
	
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.produce:
			String userName = number.getText().toString();
			FileUtil.writeFileSdcard(FileUtil.Folder_NAME + "/me.txt", userName);
			DHkey dhkey1;
			privateKey1 = content.getText().toString(); //私钥产生x
			BigInteger publicKey1;
			dhkey1 = new DHkey(privateKey1);//私钥产生x
			FileUtil.writeFileSdcard(FileUtil.File_NAME2 + userName + ".txt",privateKey1.toString());
			publicKey1 = dhkey1.getY();//获得公钥y
			// 并将本机号码存放到本地文件
			FileUtil.folderCreate();
			// 将得到的自己的公钥存储到本地文件，以手机号命名
			FileUtil.writeFileSdcard(FileUtil.File_NAME + userName + ".txt",publicKey1.toString());

			Toast.makeText(MainActivity.this, "密钥保存成功", 8000).show();
			break;
		case R.id.send:
			String name = number.getText().toString();
			//FileUtil.writeFileSdcard(FileUtil.Folder_NAME + "/me.txt", name);
			String userName2 = FileUtil.readFileSdcard(FileUtil.Folder_NAME+ "/me.txt");
			String publicKey2 = ""; //从本地文件中读取获得公钥y
			publicKey2 = FileUtil.readFileSdcard(FileUtil.File_NAME + name+ ".txt");
			System.out.println("@@@@@@@@publicKey" + publicKey2);
			
			//number.setText(publicKey2);
			
			privateKey1 = FileUtil.readFileSdcard(FileUtil.File_NAME2 + userName2+ ".txt");
			DHkey dhkey2 = new DHkey(privateKey1);//产生私钥x
			BigInteger k = dhkey2.getK(publicKey2);//获得密钥k
			System.out.println("@@@@@@@@K" + k);
			String message = content.getText().toString();
			System.out.println("@@@@@@@@message" + message);
			DESUtil Des = new DESUtil();
			String encode = "yyyy:"+Des.DESEncode(k.toString(), message);//加密

			content.setText(encode);
			// 创建一个PendingIntent对象
			PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0,
					new Intent(), 0);
			// 发送短信
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
			// 提示短信发送成功
			Toast.makeText(MainActivity.this, "短信发送成功", 8000).show();
			break;
		default:
			break;
		}

	}
}
