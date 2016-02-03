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
} // 界面枚举

public class WLQPActivity extends Activity {
	MainMenuView mmv; // 主界面
	GameView gameview; // 游戏界面
	WhichView curr; // 选择哪个界面
	ClientAgent ca; // 客户端代理线程
	SoundPool soundPool;
	HashMap<Integer, Integer> soundPoolMap;
	static String cardListStr;
	// 声明消息处理器
	Handler hd = new Handler() {
		@Override
		public void handleMessage(Message msg)// 重写方法
		{
			switch (msg.what) {
			case 0: // 进入等待界面
				setContentView(R.layout.wait);
				curr = WhichView.WAIT_OTHER;
				break;
			case 1: // 进入游戏界面
				gotoGameView();
				break;
			case 2: // 进入你赢了界面
				setContentView(R.layout.win);
				curr = WhichView.WIN;
				break;
			case 3: // 进入你输了界面
				setContentView(R.layout.lost);
				curr = WhichView.LOST;
				break;
			case 4: // 进入有玩家退出界面
				setContentView(R.layout.exit);
				curr = WhichView.EXIT;
				break;
			case 5: // 人数已满
				setContentView(R.layout.full);
				curr = WhichView.FULL;
				break;
			case 6: // 进入帮助页面
				setContentView(R.layout.help);
				curr = WhichView.HELP;
				break;
			case 7: // 进入关于界面
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
		// 设置全屏显示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 强制为横屏
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		initSounds();
		goToWelcomeView();

	}

	// 声音缓冲池的初始化
	public void initSounds() {
		// 创建声音缓冲池
		soundPool = new SoundPool(4, // 同时能最多播放的个数
				AudioManager.STREAM_MUSIC, // 音频的类型
				100 // 声音的播放质量，目前无效
		);

		// 创建声音资源Map
		soundPoolMap = new HashMap<Integer, Integer>();
		// 将加载的声音资源id放进此Map
		soundPoolMap.put(1, soundPool.load(this, R.raw.tweet, 1));
		// 有几个音效就有当前这个几句 R.raw.gamestart返回编号 不定 后面的1为优先级 目前不考虑
	}

	// 播放声音的方法
	public void playSound(int sound, int loop) {
		AudioManager mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);// 当前音量
		float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 最大音量
		float volume = streamVolumeCurrent / streamVolumeMax;

		soundPool.play(soundPoolMap.get(sound), // 声音资源id
				volume, // 左声道音量
				volume, // 右声道音量
				1, // 优先级
				loop, // 循环次数 -1带表永远循环
				0.5f // 回放速度0.5f～2.0f之间
		);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) { // 监听手机键盘按下事件
		if (keyCode == 4)// 调制上一个界面的键
		{// 根据记录的当前是哪个界面信息的curr可以知道要跳转到的是哪个界面
			if (curr == WhichView.WIN || curr == WhichView.LOST || curr == WhichView.EXIT) {
				goToMainMenu();
				return true;
			}
			if (curr == WhichView.WELCOMEVIEW) {
				return true;
			}
			if (curr == WhichView.IP_VIEW) {// 跳转到MainMenu
				goToMainMenu();
				return true;
			}
			if (curr == WhichView.GAME_VIEW) {// 跳转到EXIT界面
				try {
					ca.dout.writeUTF("<#EXIT#>");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return true;
			}
			if (curr == WhichView.WAIT_OTHER) {// 不跳转
				return true;
			}
			if (curr == WhichView.MAIN_MENU) {// 退出游戏
				System.exit(0);
			}
			if (curr == WhichView.FULL) {// 跳转到IPView
				gotoIpView();
				return true;
			}
			if (curr == WhichView.HELP) {// 跳转到MainMenu
				goToMainMenu();
				return true;
			}
			if (curr == WhichView.ABOUT) {// 跳转到MainMenu
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

	public void goToMainMenu() {// 去主界面的方法
		if (mmv == null) {
			mmv = new MainMenuView(this);
		}
		setContentView(mmv);
		// 当前的View为MAIN_MENU;
		curr = WhichView.MAIN_MENU;
	}

	public void gotoIpView() {// 去主IP和端口号的界面的方法
		setContentView(R.layout.main);
		final Button blj = (Button) this.findViewById(R.id.Button01);
		final Button bfh = (Button) this.findViewById(R.id.Button02);

		blj.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 得到每个EditText的引用
				final EditText eta = (EditText) findViewById(R.id.EditText01);
				final EditText etb = (EditText) findViewById(R.id.EditText02);
				String ipStr = eta.getText().toString();// 得到EditText里面的信息
				String portStr = etb.getText().toString();

				String[] ipA = ipStr.split("\\.");
				if (ipA.length != 4) {// 判断IP的格式是否合法
					Toast.makeText(WLQPActivity.this, "服务器IP地址不合法", Toast.LENGTH_SHORT).show();

					return;
				}

				for (String s : ipA) {// 在IP的格式合法的前提下判断端口号是否合法
					try {
						int ipf = Integer.parseInt(s);
						if (ipf > 255 || ipf < 0) {// 判断Ip的合法性
							Toast.makeText(// 界面弹出Toast显示信息 --->服务器IP地址不合法!
									WLQPActivity.this, "服务器IP地址不合法", Toast.LENGTH_SHORT).show();
							return;
						}
					} catch (Exception e) {
						Toast.makeText(// 界面弹出Toast显示信息 --->服务器IP地址不合法!
								WLQPActivity.this, "服务器IP地址不合法!", Toast.LENGTH_SHORT).show();
						return;
					}
				}

				try {
					int port = Integer.parseInt(portStr);
					if (port > 65535 || port < 0) {// 判断端口号是否合法
						Toast.makeText(// 界面弹出Toast显示信息 --->服务器端口号不合法!
								WLQPActivity.this, "服务器端口号不合法!", Toast.LENGTH_SHORT).show();
						return;
					}
				} catch (Exception e) {
					Toast.makeText(// 界面弹出Toast显示信息 --->服务器端口号不合法!
							WLQPActivity.this, "服务器端口号不合法!", Toast.LENGTH_SHORT).show();
					return;
				}

				// 验证过关
				int port = Integer.parseInt(portStr);
				try {// 验证过关后启动代理的客户端线程
					Socket sc = new Socket(ipStr, port);
					DataInputStream din = new DataInputStream(sc.getInputStream());
					DataOutputStream dout = new DataOutputStream(sc.getOutputStream());
					ca = new ClientAgent(WLQPActivity.this, sc, din, dout);
					ca.start();
				} catch (Exception e) {
					Toast.makeText(// 界面弹出Toast显示信息
							WLQPActivity.this, "联网失败，请稍后再试!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		bfh.setOnClickListener(// 对返回按钮设置监听 跳转到主界面
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						goToMainMenu();
					}
				});
		// 当前的View为IP_VIEW;
		curr = WhichView.IP_VIEW;
	}

	public void gotoGameView() {
		gameview = new GameView(this);
		setContentView(gameview);
		// 当前的View为GAME_VIEW;
		curr = WhichView.GAME_VIEW;
	}
}