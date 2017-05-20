package com.example.sms;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText number, content;
	Button quit, send;
	SmsManager sManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//��ȡSmsManager����Ϣ������
		sManager = SmsManager.getDefault();
		//��ȡ�ı���Ͱ�ť
		number = (EditText)findViewById(R.id.number);
		content = (EditText)findViewById(R.id.content);
		quit = (Button)findViewById(R.id.quit);
		send = (Button)findViewById(R.id.send);
		
		//Ϊ��ť����¼�
	
		send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				String message = content.getText().toString();
				String pwd = "dhaskfhaiutwr";
				DESUtil Des = new DESUtil();
				String encode = Des.DESEncode(pwd.toString(), message);
				
				System.out.println("@@@@@@@@AESEncode"+encode);
				content.setText(encode);
				String decode = Des.DESDecode(pwd.toString(),encode);
				//number.setText(ciphermessage2);
				//����һ��PendingIntent����
				PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, new Intent(), 0);
				//���Ͷ���

					if (decode.length() > 70) {  
					    ArrayList<String> msgs = sManager.divideMessage(decode);  
					    ArrayList<PendingIntent> sentIntents =  new ArrayList<PendingIntent>();  
					    for(int i = 0;i<msgs.size();i++){  
					       sentIntents.add(pi);  
					    }  
					    sManager.sendMultipartTextMessage(number.getText().toString(), null, msgs, sentIntents, null);  
					} else {   
						sManager.sendTextMessage(number.getText().toString(), null, decode, pi, null);  
					}  
				//��ʾ���ŷ��ͳɹ�
				Toast.makeText(MainActivity.this, "���ŷ��ͳɹ�", 8000).show();
			
			}
		});
		
		quit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				  android.os.Process.killProcess(android.os.Process.myPid());   //��ȡPID 
				  System.exit(0);   //����java��c#�ı�׼�˳���������ֵΪ0���������˳�				
			}
		});
		
	}
}
