package com.wswenyue.parkinglot.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wswenyue.parkinglot.R;
import com.wswenyue.parkinglot.constant.Constant;
import com.wswenyue.parkinglot.service.MyService;

public class PayZFBActivity extends BasicActivity {
    private Button btOk, btCancel;
    private EditText etMoney, etPasswd;
    private StringBuffer sb;
    private String uname, Passwd;
    private String Money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_zfb);
        btOk = (Button) this.findViewById(R.id.bt_ok);
        btCancel = (Button) this.findViewById(R.id.bt_cancel);

        etMoney = (EditText) this.findViewById(R.id.moneyView);
        etPasswd = (EditText) this.findViewById(R.id.passwordView);

        receiver = new BroadcastMain();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BroadCastSend);
        registerReceiver(receiver, filter);
        getData();

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Check()){
                    Pay();
                    Toast.makeText(PayZFBActivity.this,"请稍等，正在充值。。。",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(PayZFBActivity.this,"请填写完整",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayZFBActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    public boolean Check() {
        Money = etMoney.getText().toString().trim();
        Passwd = etPasswd.getText().toString().trim();
        if (Money != null && !Money.equals("")
                && Passwd != null && !Passwd.equals("")) {
            return true;
        }
        return false;
    }

    public void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("parkinglotInfo", 0);
        uname = sharedPreferences.getString("uname", "");
    }

    public void Pay() {
        Message message = new Message();
        message.what = Constant.MSG_WHAT_SENDMSG;
        sb = new StringBuffer();
        sb.append(Constant.Pay_ZFB).append("#").append(uname)
                .append("#").append(Money).append("#").append(Passwd);
        message.obj = sb.toString();
        MyService.revHandler.sendMessage(message);
    }

    BroadcastMain receiver;

    //内部类，实现BroadcastReceiver
    public class BroadcastMain extends BroadcastReceiver {
        //必须要重载的方法，用来监听是否有广播发送
        @Override
        public void onReceive(Context context, Intent intent) {
            String MsgStr = intent.getStringExtra("msg");

            if (MsgStr.equals(Constant.Pay_Succeed)) {
                Toast.makeText(PayZFBActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
                intent = new Intent(PayZFBActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else if (MsgStr.equals(Constant.Pay_Fail)) {
                Toast.makeText(PayZFBActivity.this, "充值失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
