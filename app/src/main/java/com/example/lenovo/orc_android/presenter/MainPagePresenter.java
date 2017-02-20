package com.example.lenovo.orc_android.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2017/2/19.
 */

public class MainPagePresenter {

    private String sdPath;  // sd卡路径
    private String picPath; // 图片路径
    private String filename; //照片名称
    private Uri uri;

    /**
        通过启动相机拍照获得照片素材
     */
    public Intent pic_By_Camera(){
        //获取SD卡路径
        sdPath = Environment.getExternalStorageDirectory().getPath()+"/ORC_IMG";
        File filedir = new File(sdPath);
        filedir.mkdir(); //创建文件夹
        filename = getFileName();
        picPath = sdPath + "/" +filename;
        Log.e("picPath is :", "pic_By_Camera: " + picPath );
        Intent intent_by_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri = Uri.fromFile(new File(picPath));
        //为拍摄的图片指定一个存储的路径
        intent_by_camera.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        return  intent_by_camera;
    }

    //通过时间命名规则给照片命名
    public String getFileName() {
        String fileName = "";
        //获取系统时间，并按照指定格式输出
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        fileName = dateFormat.format(date) + ".jpg";
        return fileName;
    }

    /**
     * 配置相机剪裁程序的intent
     */

    public Intent Crop_Picture(){
        Intent crop_Intent = new Intent("com.android.camera.action.CROP");
        crop_Intent.setDataAndType(uri,"image/*");
        crop_Intent.putExtra("scale",true);
        crop_Intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        return crop_Intent;
    }

    /**
     *
     */
    public Bitmap showPic(Context context){
        Bitmap bitmap = null;
        try{
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
