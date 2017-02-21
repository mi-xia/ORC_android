package com.example.lenovo.orc_android.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.width;
import static android.support.v7.appcompat.R.attr.height;

/**
 * Created by lenovo on 2017/2/19.
 */

public class MainPagePresenter {

    private String sdPath;  // sd卡路径
    private String picPath; // 图片路径
    private String filename; //照片名称
    private Uri uri;
    private Context context;

    public MainPagePresenter(Context context){
        this.context = context;
    }


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

    /** 图像处理参考网站：http://blog.csdn.net/zgwangbo/article/details/51079770
     * 灰度处理，二值化处理代码参考：http://blog.csdn.net/xdhywj/article/details/8886447
     *裁剪完图像后继续进行对图像的预处理，主要步骤包括：
     * a、灰度处理：将彩色图片变成灰度图
     * b、二值化：将灰度图变成黑白图
     * c、去噪：消除黑白图上的噪点，让图看起来更干净
     * d、旋转：对图片进行顺时针和逆时针旋转，找到一个最佳水平位置
     * e、水平切割：对调整好水平位置的图片进行一行一行的切割
     * f、垂直切割：对一行一行的图片进行一列一列的切割，产出单个的字符。
     */
    public Bitmap handle_Pic(){
        Bitmap bitmap = null;
        try{
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        bitmap = bitmap2Gray(bitmap);  //调用灰度化处理代码
        //bitmap = lineGrey(bitmap);   //调用线性灰度化处理代码---->>>这破玩意不好用，先注释掉
        //bitmap = gray2Binary(bitmap);  //调用二值化处理代码
        bitmap = binarization(bitmap); //调用另一种二值化处理代码
        return bitmap;
    }

    /**
     * 灰度化处理代码
     * @param bmSrc
     * @return
     */
    private Bitmap bitmap2Gray(Bitmap bmSrc) {
        // 得到图片的长和宽
        int width = bmSrc.getWidth();
        int height = bmSrc.getHeight();
        // 创建目标灰度图像
        Bitmap bmpGray = null;
        bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        // 创建画布
        Canvas c = new Canvas(bmpGray);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmSrc, 0, 0, paint);
        return bmpGray;
    }

    /**
     * 线性灰度化处理代码
     */
    public Bitmap lineGrey(Bitmap image)
    {
        //得到图像的宽度和长度
        int width = image.getWidth();
        int height = image.getHeight();
        //创建线性拉升灰度图像
        Bitmap linegray = null;
        linegray = image.copy(Bitmap.Config.ARGB_8888, true);
        //依次循环对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到每点的像素值
                int col = image.getPixel(i, j);
                int alpha = col & 0xFF000000;
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 增加了图像的亮度
                red = (int) (1.1 * red + 30);
                green = (int) (1.1 * green + 30);
                blue = (int) (1.1 * blue + 30);
                //对图像像素越界进行处理
                if (red >= 255)
                {
                    red = 255;
                }
                if (green >= 255) {
                    green = 255;
                }
                if (blue >= 255) {
                    blue = 255;
                }
                // 新的ARGB
                int newColor = alpha | (red << 16) | (green << 8) | blue;
                //设置新图像的RGB值
                linegray.setPixel(i, j, newColor);
            }
        }
        return linegray;
    }

    /**
     *该函数实现对图像进行二值化处理
     */
    private Bitmap gray2Binary(Bitmap graymap) {
        //得到图形的宽度和长度
        int width = graymap.getWidth();
        int height = graymap.getHeight();
        //创建二值化图像
        Bitmap binarymap = null;
        binarymap = graymap.copy(Bitmap.Config.ARGB_8888, true);
        //依次循环，对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到当前像素的值
                int col = binarymap.getPixel(i, j);
                //得到alpha通道的值
                int alpha = col & 0xFF000000;
                //得到图像的像素RGB的值
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                //对图像进行二值化处理
                if (gray <= 95) {
                    gray = 0;
                } else {
                    gray = 255;
                }
                // 新的ARGB
                int newColor = alpha | (gray << 16) | (gray << 8) | gray;
                //设置新图像的当前像素值
                binarymap.setPixel(i, j, newColor);
            }
        }
        return binarymap;
    }

    /**
     * 二值化的另一种实现,这种实现方式成像效果似乎更好一点
     * 参考：http://gqdy365.iteye.com/blog/1559180
     */


    public Bitmap binarization(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int area = width * height;
        int gray[][] = new int[width][height];
        int average = 0;// 灰度平均值
        int graysum = 0;
        int graymean = 0;
        int grayfrontmean = 0;
        int graybackmean = 0;
        int pixelGray;
        int front = 0;
        int back = 0;
        int[] pix = new int[width * height];
        img.getPixels(pix, 0, width, 0, 0, width, height);
        for (int i = 1; i < width; i++) { // 不算边界行和列，为避免越界
            for (int j = 1; j < height; j++) {
                int x = j * width + i;
                int r = (pix[x] >> 16) & 0xff;
                int g = (pix[x] >> 8) & 0xff;
                int b = pix[x] & 0xff;
                pixelGray = (int) (0.3 * r + 0.59 * g + 0.11 * b);// 计算每个坐标点的灰度
                gray[i][j] = (pixelGray << 16) + (pixelGray << 8) + (pixelGray);
                graysum += pixelGray;
            }
        }
        graymean = (int) (graysum / area);// 整个图的灰度平均值
        average = graymean;
        for (int i = 0; i < width; i++) // 计算整个图的二值化阈值
        {
            for (int j = 0; j < height; j++) {
                if (((gray[i][j]) & (0x0000ff)) < graymean) {
                    graybackmean += ((gray[i][j]) & (0x0000ff));
                    back++;
                } else {
                    grayfrontmean += ((gray[i][j]) & (0x0000ff));
                    front++;
                }
            }
        }
        int frontvalue = (int) (grayfrontmean / front);// 前景中心
        int backvalue = (int) (graybackmean / back);// 背景中心
        float G[] = new float[frontvalue - backvalue + 1];// 方差数组
        int s = 0;
        for (int i1 = backvalue; i1 < frontvalue + 1; i1++)// 以前景中心和背景中心为区间采用大津法算法（OTSU算法）
        {
            back = 0;
            front = 0;
            grayfrontmean = 0;
            graybackmean = 0;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (((gray[i][j]) & (0x0000ff)) < (i1 + 1)) {
                        graybackmean += ((gray[i][j]) & (0x0000ff));
                        back++;
                    } else {
                        grayfrontmean += ((gray[i][j]) & (0x0000ff));
                        front++;
                    }
                }
            }
            grayfrontmean = (int) (grayfrontmean / front);
            graybackmean = (int) (graybackmean / back);
            G[s] = (((float) back / area) * (graybackmean - average)
                    * (graybackmean - average) + ((float) front / area)
                    * (grayfrontmean - average) * (grayfrontmean - average));
            s++;
        }
        float max = G[0];
        int index = 0;
        for (int i = 1; i < frontvalue - backvalue + 1; i++) {
            if (max < G[i]) {
                max = G[i];
                index = i;
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int in = j * width + i;
                if (((gray[i][j]) & (0x0000ff)) < (index + backvalue)) {
                    pix[in] = Color.rgb(0, 0, 0);
                } else {
                    pix[in] = Color.rgb(255, 255, 255);
                }
            }
        }

        Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        temp.setPixels(pix, 0, width, 0, 0, width, height);
        return temp;
    }


}
