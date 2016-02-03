package com.bn.wlqp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

enum WhichView {
	WELCOMEVIEW, MAIN_MENU, IP_VIEW, GAME_VIEW, WAIT_OTHER, WIN, LOST, EXIT, FULL, ABOUT, HELP
} // ����ö��

public class WLQPActivity extends Activity {
	MainMenuView mmv; // ������
	GameView gameview; // ��Ϸ����
	WhichView curr; // ѡ���ĸ�����
	ClientAgent ca; // �ͻ��˴����߳�
	SoundPool soundPool;
	HashMap<Integer, Integer> soundPoolMap;
	static String cardListStr;
	// ������Ϣ������
	Handler hd = new Handler() {
		@Override
		public void handleMessage(Message msg)// ��д����
		{
			switch (msg.what) {
			case 0: // ����ȴ�����
				setContentView(R.layout.wait);
				curr = WhichView.WAIT_OTHER;
				break;
			case 1: // ������Ϸ����
				gotoGameView();
				break;
			case 2: // ������Ӯ�˽���
				setContentView(R.layout.win);
				curr = WhichView.WIN;
				break;
			case 3: // ���������˽���
				setContentView(R.layout.lost);
				curr = WhichView.LOST;
				break;
			case 4: // ����������˳�����
				setContentView(R.layout.exit);
				curr = WhichView.EXIT;
				break;
			case 5: // ��������
				setContentView(R.layout.full);
				curr = WhichView.FULL;
				break;
			case 6: // �������ҳ��
				setContentView(R.layout.help);
				curr = WhichView.HELP;
				break;
			case 7: // ������ڽ���
				setContentView(R.layout.about);
				curr = WhichView.ABOUT;
				break;
			case 8:
				goToMainMenu();
				curr = WhichView.WELCOMEVIEW;
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����ȫ����ʾ
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ǿ��Ϊ����
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		initSounds();
		goToWelcomeView();

	}

	// ��������صĳ�ʼ��
	public void initSounds() {
		// �������������
		soundPool = new SoundPool(4, // ͬʱ����ಥ�ŵĸ���
				AudioManager.STREAM_MUSIC, // ��Ƶ������
				100 // �����Ĳ���������Ŀǰ��Ч
		);

		// ����������ԴMap
		soundPoolMap = new HashMap<Integer, Integer>();
		// �����ص�������Դid�Ž���Map
		soundPoolMap.put(1, soundPool.load(this, R.raw.tweet, 1));
		// �м�����Ч���е�ǰ������� R.raw.gamestart���ر�� ���� �����1Ϊ���ȼ� Ŀǰ������
	}

	// ���������ķ���
	public void playSound(int sound, int loop) {
		AudioManager mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);// ��ǰ����
		float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// �������
		float volume = streamVolumeCurrent / streamVolumeMax;

		soundPool.play(soundPoolMap.get(sound), // ������Դid
				volume, // ����������
				volume, // ����������
				1, // ���ȼ�
				loop, // ѭ������ -1������Զѭ��
				0.5f // �ط��ٶ�0.5f��2.0f֮��
		);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) { // �����ֻ����̰����¼�
		if (keyCode == 4)// ������һ������ļ�
		{// ���ݼ�¼�ĵ�ǰ���ĸ�������Ϣ��curr����֪��Ҫ��ת�������ĸ�����
			if (curr == WhichView.WIN || curr == WhichView.LOST || curr == WhichView.EXIT) {
				goToMainMenu();
				return true;
			}
			if (curr == WhichView.WELCOMEVIEW) {
				return true;
			}
			if (curr == WhichView.IP_VIEW) {// ��ת��MainMenu
				goToMainMenu();
				return true;
			}
			if (curr == WhichView.GAME_VIEW) {// ��ת��EXIT����
				try {
					ca.dout.writeUTF("<#EXIT#>");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return true;
			}
			if (curr == WhichView.WAIT_OTHER) {// ����ת
				return true;
			}
			if (curr == WhichView.MAIN_MENU) {// �˳���Ϸ
				System.exit(0);
			}
			if (curr == WhichView.FULL) {// ��ת��IPView
				gotoIpView();
				return true;
			}
			if (curr == WhichView.HELP) {// ��ת��MainMenu
				goToMainMenu();
				return true;
			}
			if (curr == WhichView.ABOUT) {// ��ת��MainMenu
				goToMainMenu();
				return true;
			}
		}

		return false;
	}

	public void goToWelcomeView() {
		WelcomeView mySurfaceView = new WelcomeView(this);
		this.setContentView(mySurfaceView);
		curr = WhichView.WELCOMEVIEW;
	}

	public void goToMainMenu() {// ȥ������ķ���
		if (mmv == null) {
			mmv = new MainMenuView(this);
		}
		setContentView(mmv);
		// ��ǰ��ViewΪMAIN_MENU;
		curr = WhichView.MAIN_MENU;
	}

	public void gotoIpView() {// ȥ��IP�Ͷ˿ںŵĽ���ķ���
		setContentView(R.layout.main);
		final Button blj = (Button) this.findViewById(R.id.Button01);
		final Button bfh = (Button) this.findViewById(R.id.Button02);

		blj.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �õ�ÿ��EditText������
				final EditText eta = (EditText) findViewById(R.id.EditText01);
				final EditText etb = (EditText) findViewById(R.id.EditText02);
				String ipStr = eta.getText().toString();// �õ�EditText�������Ϣ
				String portStr = etb.getText().toString();

				String[] ipA = ipStr.split("\\.");
				if (ipA.length != 4) {// �ж�IP�ĸ�ʽ�Ƿ�Ϸ�
					Toast.makeText(WLQPActivity.this, "������IP��ַ���Ϸ�", Toast.LENGTH_SHORT).show();

					return;
				}

				for (String s : ipA) {// ��IP�ĸ�ʽ�Ϸ���ǰ�����ж϶˿ں��Ƿ�Ϸ�
					try {
						int ipf = Integer.parseInt(s);
						if (ipf > 255 || ipf < 0) {// �ж�Ip�ĺϷ���
							Toast.makeText(// ���浯��Toast��ʾ��Ϣ --->������IP��ַ���Ϸ�!
									WLQPActivity.this, "������IP��ַ���Ϸ�", Toast.LENGTH_SHORT).show();
							return;
						}
					} catch (Exception e) {
						Toast.makeText(// ���浯��Toast��ʾ��Ϣ --->������IP��ַ���Ϸ�!
								WLQPActivity.this, "������IP��ַ���Ϸ�!", Toast.LENGTH_SHORT).show();
						return;
					}
				}

				try {
					int port = Integer.parseInt(portStr);
					if (port > 65535 || port < 0) {// �ж϶˿ں��Ƿ�Ϸ�
						Toast.makeText(// ���浯��Toast��ʾ��Ϣ --->�������˿ںŲ��Ϸ�!
								WLQPActivity.this, "�������˿ںŲ��Ϸ�!", Toast.LENGTH_SHORT).show();
						return;
					}
				} catch (Exception e) {
					Toast.makeText(// ���浯��Toast��ʾ��Ϣ --->�������˿ںŲ��Ϸ�!
							WLQPActivity.this, "�������˿ںŲ��Ϸ�!", Toast.LENGTH_SHORT).show();
					return;
				}

				// ��֤����
				int port = Integer.parseInt(portStr);
				try {// ��֤���غ���������Ŀͻ����߳�
					Socket sc = new Socket(ipStr, port);
					DataInputStream din = new DataInputStream(sc.getInputStream());
					DataOutputStream dout = new DataOutputStream(sc.getOutputStream());
					ca = new ClientAgent(WLQPActivity.this, sc, din, dout);
					ca.start();
				} catch (Exception e) {
					Toast.makeText(// ���浯��Toast��ʾ��Ϣ
							WLQPActivity.this, "����ʧ�ܣ����Ժ�����!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		bfh.setOnClickListener(// �Է��ذ�ť���ü��� ��ת��������
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						goToMainMenu();
					}
				});
		// ��ǰ��ViewΪIP_VIEW;
		curr = WhichView.IP_VIEW;
	}

	public void gotoGameView() {
		gameview = new GameView(this);
		setContentView(gameview);
		// ��ǰ��ViewΪGAME_VIEW;
		curr = WhichView.GAME_VIEW;
	}
}