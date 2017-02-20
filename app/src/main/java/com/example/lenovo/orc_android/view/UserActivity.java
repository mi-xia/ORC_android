package com.example.lenovo.orc_android.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lenovo.orc_android.R;
import com.example.lenovo.orc_android.listener.IUserView;
import com.example.lenovo.orc_android.presenter.UserPresenter;


/**
 * Created by lenovo on 2017/1/26.
 */

public class UserActivity extends Activity implements IUserView,View.OnClickListener{

    private UserPresenter presenter;
    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        findBundle();
        presenter = new UserPresenter(this);
        button.setOnClickListener(this);
    }

    private void findBundle(){
        button = (Button) findViewById(R.id.btn_b);
        textView = (TextView)findViewById(R.id.tv_s);
    }

    @Override
    public String getFristName() {
        String s = "大吉吧";
        return s;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_b:
                String s = presenter.saveUser();
                textView.setText(s);
                break;
        }
    }
}
