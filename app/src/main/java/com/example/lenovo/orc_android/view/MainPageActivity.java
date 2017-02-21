package com.example.lenovo.orc_android.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lenovo.orc_android.R;
import com.example.lenovo.orc_android.presenter.MainPagePresenter;



public class MainPageActivity extends Activity implements View.OnClickListener {

    private final static int permission_code = 101; //获取权限的请求码
    private final static int file_code_1 = 201;     //启动相机拍照的请求码
    private final static int crop_code_1 = 301;     //启动裁剪程序的请求码
    Button btn_start_camera;
    ImageView img_test;
    MainPagePresenter mainPagePresenter;
    private String permission_1 = Manifest.permission.CAMERA;
    private String permission_2 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private String permission_3 = Manifest.permission.READ_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        init(); //初始化控件
        getPermission(); //动态获取各类权限，android 6.0 以上使用
                        // 6.0以下需要在清单文件中写入
    }

    private void init() {
        img_test = (ImageView)findViewById(R.id.img_test);
        mainPagePresenter = new MainPagePresenter(this);
        btn_start_camera = (Button)findViewById(R.id.btn_start_camera);
        btn_start_camera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_camera:  //启动相机
                Intent intent_by_camera = mainPagePresenter.pic_By_Camera();
                startActivityForResult(intent_by_camera,file_code_1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case file_code_1:
                /** 通过内存卡的路径进行读取图片，得到的图片是拍摄的原图
                 * 然后对图片进行裁剪
                 */
                if (resultCode == RESULT_OK){
                    Intent crop_Intent = mainPagePresenter.Crop_Picture();
                    startActivityForResult(crop_Intent,crop_code_1);
                }
                break;
            case crop_code_1:
                /**
                 * 裁剪完成之后进行反馈
                 */
                if (resultCode == RESULT_OK){
                    Bitmap bitmap = mainPagePresenter.handle_Pic();
                    img_test.setImageBitmap(bitmap);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //动态获取权限
    public void getPermission() {
        //判断android版本是有否大于6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //检查改权限是否已经获取
            int i_1 = ContextCompat.checkSelfPermission(this,permission_1);
            int i_2 = ContextCompat.checkSelfPermission(this,permission_2);
            int i_3 = ContextCompat.checkSelfPermission(this,permission_3);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (    i_1 != PackageManager.PERMISSION_GRANTED ||
                    i_2 != PackageManager.PERMISSION_GRANTED ||
                    i_3 != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{permission_1,permission_2,permission_3},permission_code);
            }

        }
    }


    //动态获取权限返回结果。。。。。。。。总是感觉这个地方有BUG
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case permission_code :
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(this,"请求权限成功！",Toast.LENGTH_SHORT).show();
                } else {
                    // Permission Denied
                    Toast.makeText(this,"请求权限失败！您的手机将可能无法使用某些功能！",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
