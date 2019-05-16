package com.dems.uhf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.libutils.CommonUtils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends Activity implements OnClickListener {
    private static final String[] list = {"Reserved", "EPC", "TID", "USER"};
    private TextView Cur_Tag_Info;
    private TextView Status,Version;
    private Spinner Area_Select;
    private ArrayAdapter<String> adapter;
    private Button Search_Tag;
    private Button Read_Tag;
    private Button Write_Tag;
    private Button Set_Tag;
    private Button Set_Password;
    private Button Set_EPC;
    private Button Lock_Tag;
    private Button btn_inv_set;
    private EditText Tag_Content;
    private IUHFService iuhfService;
    private String current_tag_epc = null;
    private Button Speedt;
    private PowerManager pM = null;
    private WakeLock wK = null;
    private int init_progress = 0;
    private String modle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            iuhfService = UHFManager.getUHFService(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "模块不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        modle = SharedXmlUtil.getInstance(MainActivity.this).read("modle", "");
        initUI();
//        StringBuffer stringBuffer=new StringBuffer();
//        for (int i = 0; i < 100; i++) {
//            stringBuffer.append("1");
//        }
//        Tag_Content.setText(stringBuffer+"");
        Version.append("-"+modle);
        newWakeLock();
        EventBus.getDefault().register(this);

        Search_Tag.setEnabled(true);

        //Area_Select.setEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (iuhfService != null) {
                if (openDev()) return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (iuhfService != null) {
                iuhfService.CloseDev();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent mEvent) {
        String type = mEvent.getType();
        String msg = (String) mEvent.getMsg();
        if (type.equals("write_Status")) {
            MainActivity.this.Status
                    .setText(R.string.Status_Write_Card_Ok);
        }
        if (type.equals("set_current_tag_epc")) {
            current_tag_epc = msg;
            Cur_Tag_Info.setText(msg);
            MainActivity.this.Status
                    .setText(R.string.Status_Select_Card_Ok);
        }
        if (type.equals("read_Status")) {
            Tag_Content.setText(msg);
            MainActivity.this.Status
                    .setText(R.string.Status_Read_Card_Ok);
        }
        if (type.equals("setPWD_Status")) {
            MainActivity.this.Status
                    .setText(R.string.Status_Write_Card_Ok);
        }
        if (type.equals("lock_Status")) {
            MainActivity.this.Status
                    .setText("设置成功");
        }
        if (type.equals("SetEPC_Status")) {
            MainActivity.this.Status
                    .setText(R.string.Status_Write_Card_Ok);
        }
    }

    private void newWakeLock() {
        init_progress++;
        pM = (PowerManager) getSystemService(POWER_SERVICE);
        if (pM != null) {
            wK = pM.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "lock3992");
            if (wK != null) {
                wK.acquire();
                init_progress++;
            }
        }

        if (init_progress == 1) {
            Log.w("3992_6C", "wake lock init failed");
        }
    }

    private boolean openDev() {
        if (iuhfService.OpenDev() != 0) {
            Cur_Tag_Info.setText("Open serialport failed");
            new AlertDialog.Builder(this).setTitle(R.string.DIA_ALERT).setMessage(R.string.DEV_OPEN_ERR).setPositiveButton(R.string.DIA_CHECK, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    finish();
                }
            }).show();
            return true;
        }
        return false;
    }

    private void initUI() {
        setContentView(R.layout.main);

        Search_Tag = (Button) findViewById(R.id.btn_search);
        Search_Tag.setOnClickListener(this);

        Status = (TextView) findViewById(R.id.textView_status);
        Version= (TextView) findViewById(R.id.textView_version);
        Version.setText(CommonUtils.getAppVersionName(this));
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        Search_Tag.setEnabled(false);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        wK.release();
        //注销广播、对象制空
        UHFManager.closeUHFService();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if (arg0 == Search_Tag) {

            //盘点选卡
            SearchTagDialog searchTag = new SearchTagDialog(this, iuhfService, modle);
            searchTag.setTitle(R.string.Item_Choose);
            searchTag.show();

        }
    }

    private long mkeyTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                    mkeyTime = System.currentTimeMillis();
                    Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}

