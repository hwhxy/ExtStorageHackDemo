package com.example.androidservicesecurity;

import java.io.File;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        boolean isSdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //没有权限则申请权限
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (isSdCardExist) {
            String sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();// 获取sdcard的根路径

            String filepath = sdpath + File.separator + "Android/data/xxxxx";
            JSONArray fileList = getAllFiles(filepath);
            textView1.setText("sd卡是存在的,缓存图片如下：" + fileList);
            assert fileList != null;
            linearLayout1.removeAllViews();
            for (int i = 0; i < fileList.length(); i++) {
                try {
                    String dirpath = (String) fileList.getJSONObject(i).get("path");
                    File file = new File(dirpath);
                    ImageView imageView = new ImageView(this);//创建一个imageView对象
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    if (file.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(dirpath);
                        // 将图片显示到ImageView中
                        imageView.setImageBitmap(bm);
                        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                        lp.width = 200;
                        lp.height = 200;
                        imageView.setLayoutParams(lp);
                        linearLayout1.addView(imageView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            textView1.setText("sd卡不存在！");
        }

    }

    public static JSONArray getAllFiles(String dirPath) {
        File f = new File(dirPath);
        if (!f.exists()) {//判断路径是否存在
            return null;
        }
        File[] files = f.listFiles();

        if (files == null) {//判断权限
            return null;
        }

        JSONArray fileList = new JSONArray();
        for (File _file : files) {//遍历目录
            if (_file.isFile()) {
                String _name = _file.getName();
                String filePath = _file.getAbsolutePath();//获取文件路径
                String fileName = _file.getName().substring(0, _name.length() - 4);//获取文件名
                try {
                    JSONObject _fInfo = new JSONObject();
                    _fInfo.put("name", fileName);
                    _fInfo.put("path", filePath);
                    fileList.put(_fInfo);
                } catch (Exception ignored) {
                }
            } else if (_file.isDirectory()) {//查询子目录
                getAllFiles(_file.getAbsolutePath());
            }
        }
        return fileList;
    }

}