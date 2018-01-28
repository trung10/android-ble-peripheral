package itan.com.bluetoothle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CentralRoleActivity extends BluetoothActivity implements View.OnClickListener {


    private Button mScanButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScanButton = (Button) findViewById(R.id.button_scan);
        mScanButton.setOnClickListener(this);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_central_role;
    }


    @Override
    public void onClick(View view) {

        switch(view.getId()) {

            case R.id.button_scan:
                onBackPressed();
                break;


        }
    }


    @Override
    protected int getTitleString() {
        return R.string.peripheral_screen;
    }


}