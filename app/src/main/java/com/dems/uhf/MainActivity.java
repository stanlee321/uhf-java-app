package com.dems.uhf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


// from lib
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;

//import com.speedata.libuhf.bean.SpdInventoryData;
//import com.speedata.libuhf.interfaces.OnSpdInventoryListener;
//import com.speedata.libuhf.utils.CommonUtils;
import com.speedata.libuhf.utils.SharedXmlUtil;


// local libs

//import com.speedata.uhf.dialog.SearchTagDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;




public class MainActivity extends AppCompatActivity {
    private static final String[] list = {"Reserved", "EPC", "TID", "USER"};

    private TextView Status, Version;
    private Spinner Area_Select;
    private IUHFService iuhfService;
    private String modle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UHFManager.setStipulationLevel(0);


        try {
            iuhfService = UHFManager.getUHFService(MainActivity.this);
            Toast.makeText(getApplicationContext(), "Module exist :D!!!!!! READ OK", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Module does not exist :/", Toast.LENGTH_SHORT).show();
            return;
        }
        // init
        // THIS STUFF CRASHES ALL

        //modle = SharedXmlUtil.getInstance(MainActivity.this).read("modle", "");

        //Version.append("-" + modle);
        //EventBus.getDefault().register(this);

    }
}
