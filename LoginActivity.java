package com.example.hglas;

import android.app.AlertDialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LoginActivity extends AppCompatActivity {
    private byte[] gas = new byte[16];
    private boolean[] flag = new boolean[16];
    private String[] gas_s = new String[16];
    private TextView[] tv = new TextView[16];
    private TextView current;
    private Button details, stop, reset;
    private String[] current_s = null;
    private String now = " ";
    private MediaPlayer mediaPlayer;
    private Vibrator vibe;
    private AlertDialog.Builder oDialog;
    boolean stopflag = false;
    boolean resetflag = false;
    boolean sound = true;

    static DatagramSocket dsock;
    static DatagramPacket sPack, rPack;
    static final int port = 5000;
    static InetAddress server;
    boolean control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.merge_siren_10);

        control = getIntent().getExtras().getBoolean("manager");
        initialize();

        try {
            server = InetAddress.getByName("192.168.1.21");
        }catch (Exception k ){}

        new Thread() {
            public void run() {
                connect();
            }
        }.start();
    }

    public void bOnClick(View v) {
        Message msg;
        switch (v.getId()) {
            case R.id.details:
                oDialog = new AlertDialog.Builder(this);
                for(int i = 0; i < 16; i++){
                    if(!current_s.equals(""))
                        now += current_s[i];
                }
                oDialog.setTitle("현재 누출 위치").setMessage("현재 누출 개소 : " + now);
                oDialog.setPositiveButton("확인", null);
                AlertDialog alertDialog = oDialog.create();
                alertDialog.show();

                break;
            case R.id.stop:
                for (int i = 0; i < 16; i++)
                    flag[i] = false;
                for(int i = 0; i < 16; i++) current_s[i] = "";

                sound = false;
                vibe.cancel();
                stopflag=true;
                msg = Message.obtain(mHandler,0);
                mHandler.sendMessage(msg);
                break;
            case R.id.reset:
                for (int i = 0; i < 16; i++)
                    flag[i] = false;
                for(int i = 0; i < 16; i++) current_s[i] = "";
                now = "";
                sound = false;
                vibe.cancel();
                resetflag=true;
                msg = Message.obtain(mHandler,1);
                mHandler.sendMessage(msg);
                break;
        }
    }

    public void initialize() {
        tv[0] = findViewById(R.id.gas_0101);
        tv[1] = findViewById(R.id.gas_0102);
        tv[2] = findViewById(R.id.gas_0103);
        tv[3] = findViewById(R.id.gas_0104);
        tv[4] = findViewById(R.id.gas_0105);
        tv[5] = findViewById(R.id.gas_0106);
        tv[6] = findViewById(R.id.gas_0107);
        tv[7] = findViewById(R.id.gas_0108);
        tv[8] = findViewById(R.id.gas_0109);
        tv[9] = findViewById(R.id.gas_0110);
        tv[10] = findViewById(R.id.gas_0111);
        tv[11] = findViewById(R.id.gas_0112);
        tv[12] = findViewById(R.id.gas_0113);
        tv[13] = findViewById(R.id.gas_0114);
        tv[14] = findViewById(R.id.gas_0115);
        tv[15] = findViewById(R.id.gas_0116);
        current_s = new String[16];
        for(int i = 0; i < 16; i++) current_s[i] = "";
        details = findViewById(R.id.details);
        stop = findViewById(R.id.stop);
        reset = findViewById(R.id.reset);
        current = findViewById(R.id.current);
        current.setText("총 16개소 정상 작동 중");
    }

    public void connect() {
        Client client = new Client();
        try {
            client.communicate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    System.out.println("일시정지");
                    try {
                        String stop = "stop";
                        Toast.makeText(getApplicationContext(), "일시정지 되었습니다.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                    break;
                case 1:
                    System.out.println("리셋");
                    if (control) {
                        try {
                            String reset = "reset";
                            Toast.makeText(getApplicationContext(), "리셋되었습니다.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }
                    } else {
                        try {
                            Toast.makeText(getApplicationContext(), "Application을 완전히 종료 후 관리자 계정으로 로그인 하세요. ", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }
                    }
                    break;
                case 2:
                    System.out.println("1");
                    for (int i = 0; i < 16; i++) {
                        gas_s[i] = Byte.toString(gas[i]);
                        if (gas[i] > 20) {
                            flag[i] = true;
                            current_s[i] = (i + 1) + " / ";
                        }else{flag[i]=false;}
                        tv[i].setText(gas_s[i]);
                    }

                    for (int i = 0; i < 16; i++) {    // flag 값이 true 인 항목이 하나라도 있는지 확인하는 작업
                        if (flag[i] == true) {
                            current.setText("※ 현재 가스가 누출되고 있습니다 ※");
                            current.setTextColor(Color.RED);
                            stop.setEnabled(true);
                            reset.setEnabled(true);
                            if(sound) {
                                vibe.vibrate(2000);
                                mediaPlayer.start();
                            }
                                System.out.println("3");
                            break;
                        } else {
                            current.setText("총 16개소 정상 작동 중");
                            current.setTextColor(Color.BLACK);
                            System.out.println("4");

                        }
                    }
            }
        }
    };

    class Client {
        public Client() {
            try {
                dsock = new DatagramSocket();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public void communicate() throws Exception {
            try {
                while (true) {
                    String connection = "connect";

                    if(control) {
                        if (stopflag) {
                            connection = "stop";
                            stopflag = false;
                        } else if (resetflag) {
                            connection = "reset";
                            resetflag = false;
                        }
                    }
                    sPack = new DatagramPacket(connection.getBytes(), connection.getBytes().length, server, port);


                    dsock.send(sPack);
                    rPack = new DatagramPacket(gas, 0, gas.length);
                    dsock.receive(rPack);
                    System.out.println(gas[0] + " " + gas[15]);

                    Message msg = Message.obtain(mHandler,2);
                    mHandler.sendMessage(msg);

                    Thread.sleep(5000);

                    System.out.println("[server" + server + " : " + port + "]");

                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
