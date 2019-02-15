package com.example.zc_changefont;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //声明组件
    private TextView textView;
    private Context context;
    private ScrollView scrollView;
    //private String filepath="/storage/emulated/0/Fonts/",filepath_ttf;
    private String filepath="",filepath_ttf="",filepath_text="",filepath_txt="";
    private ScaleGestureDetector mScaleGestureDetector ;
    private float fontsize;
    private float curScale;//当前的伸缩值

    private String[] ttf_items,txt_items;
    private List<String> ttf_list,txt_list;
    private int ttf_index=-1,txt_index=-1,ttf_list_size,txt_list_size;


    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public final int EXTERNAL_REQUEST = 138;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定组件
        textView=findViewById(R.id.textView);
        context=getApplicationContext();
        scrollView=findViewById(R.id.scrollview);

        //滑屏事件的监听
        mScaleGestureDetector= new ScaleGestureDetector(context,new ScaleGestureListener());

        //textView.setMovementMethod(ScrollingMovementMethod.getInstance());

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return mScaleGestureDetector.onTouchEvent(event);
            }

        });



        //android 6.0 以上需要权限注册
        requestForPermission();

        //读取文件目录
        filepath=Environment.getExternalStorageDirectory().getPath()+"/fonts";
        filepath_text=Environment.getExternalStorageDirectory().getPath()+"/txts";

        //如果没有目录 弹出警告框，然后退出程序



        Log.i("aa", "onCreate: "+filepath);
        Log.i("aa", "onCreate: "+filepath_text);
        //filepath=context.getExternalFilesDir("/fonts").getPath();




    }

    /**
     * 添加menu菜单栏选项
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**
         *  menu.add((int groupId, int itemId, int order, charsequence title)
         *  groupId:组别ID（不分组的情况下填写Menu.NONE）
         *  itemId:选项ID
         *  order：顺序ID（根据此参数的大小决定选项顺序）
         *  title:选项的文本内容
         */
        menu.add(Menu.NONE,1,1,"选择字体文件");
        menu.add(Menu.NONE,2,2,"选择文本文件");
        menu.add(Menu.NONE,3,3,"字体放大");
        menu.add(Menu.NONE,4,4,"字体缩小");
        menu.add(Menu.NONE,5,5,"生成图片");

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * menu菜单选项被选中的监听事件
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //使用switch语句根据选项id判断选中了哪个选项
        switch(item.getItemId()){
            case 1: //选择字体文件
                //读取指定目录下的字体文件（.ttf格式）
                ttf_list=GetFileName(filepath,".ttf");
                //list转换成String[]
                ttf_list_size=ttf_list.size();

                if(ttf_list_size>0){
                    ttf_items= ttf_list.toArray(new String[ttf_list_size]);
                    AlertDialog dialog = new AlertDialog.Builder(this).setTitle("单选对话框")
                            .setSingleChoiceItems(ttf_items, ttf_index, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //将选中的结果进行赋值
                                    ttf_index=which;
                                    filepath_ttf=filepath+"/"+ttf_items[which];
                                    //更新textview当中的文字渲染
                                    Textview_Rendering(filepath_ttf,filepath_txt);
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }else{
                    //弹出警告框 （目录下无字体文件）
                    AlertDialog dialog = new AlertDialog.Builder(this).setTitle("警告")
                            .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //处理确认按钮的点击事件
                                }
                            }).setMessage("目录下无字体文件").create();
                    dialog.show();
                }

                Log.i("menu菜单选择情况", "选择字体文件: ");
                break;
            case 2: //选择文本文件
                //读取指定目录下的字体文件（.txt格式）
                txt_list=GetFileName(filepath_text,".txt");
                //list转换成String[]
                txt_list_size=txt_list.size();

                if(txt_list_size>0){
                    txt_items= txt_list.toArray(new String[txt_list_size]);
                    AlertDialog dialog = new AlertDialog.Builder(this).setTitle("单选对话框")
                            .setSingleChoiceItems(txt_items, txt_index, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //将选中的结果进行赋值
                                    txt_index=which;
                                    filepath_txt=filepath_text+"/"+txt_items[which];
                                    //更新textview当中的文字渲染
                                    Textview_Rendering(filepath_ttf,filepath_txt);
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }else{
                    //弹出警告框 （目录下无文本文件）
                    AlertDialog dialog = new AlertDialog.Builder(this).setTitle("警告")
                            .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //处理确认按钮的点击事件
                                }
                            }).setMessage("目录下无文本文件").create();
                    dialog.show();
                }
                Log.i("menu菜单选择情况", "选择文本文件: ");
                break;
            case 3:
                //字体放大 每次放大6号 ，最大300
                fontsize=textView.getTextSize();
                if((fontsize+6)<300){
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontsize+6);
                }
                Log.i("menu菜单选择情况", "字体放大: ");
                break;
            case 4:
                //字体缩小 每次缩小6号 ， 最小10
                fontsize=textView.getTextSize();
                if((fontsize-6)>10){
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontsize-6);
                }else{
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,10);
                }
                Log.i("menu菜单选择情况", "字体缩小: ");
                break;
            case 5:
                //将当前文字显示情况生成图片并保存
                save_Scrollview();
                Toast.makeText(MainActivity.this, "保存完成", Toast.LENGTH_LONG).show();
                Log.i("menu菜单选择情况", "将当前文字显示情况生成图片保存: ");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 渲染字体和文本到textview中
     */
    public void Textview_Rendering(String ttf_value,String txt_value){
        if(!ttf_value.equals("")){
            //读取字体文件 并设置字体文件
            Typeface typeface2 = Typeface.createFromFile(ttf_value);
            textView.setTypeface(typeface2);
        }
        if(txt_value!=null){
            textView.setText("");
            String str="";
            try {
                InputStream is = new FileInputStream(txt_value);
                InputStreamReader input = new InputStreamReader(is, "UTF-8");
                BufferedReader reader = new BufferedReader(input);
                while ((str = reader.readLine()) != null) {
                    textView.append(str);
                    textView.append("\n");
                }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * 响应拦截
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mScaleGestureDetector.onTouchEvent(ev); //让GestureDetector响应触碰事件
        super.dispatchTouchEvent(ev); //让Activity响应触碰事件
        return false;
    }

    /**
     * 读取字体文件名到list
     */
    public List<String> GetFileName(String fileAbsolutePath,String str){
        List<String> vecFile = new ArrayList<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // 判断是否为ttf结尾
                if (filename.trim().toLowerCase().endsWith(str)) {
                    vecFile.add(filename);
                }
            }
        }
        return vecFile;
    }

    /**
     * 权限检测
     * @return
     */

    public boolean requestForPermission() {

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd() {
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));

    }


    /**
     * 手势操作
     */
    public class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

                 @Override
                 public boolean onScale(ScaleGestureDetector detector)
                 {
                         // TODO Auto-generated method stub
                     curScale=detector.getScaleFactor();
                    // Log.i("222", "onScale: 调用了缩放方法");
                     fontsize=textView.getTextSize();
                     if(curScale>1&&(fontsize+3)<300){
                         //放大
                        // Log.i("1111", "onScale: 放大");
                         textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontsize+3);
                     }
                     if(curScale<1&&(fontsize-3)>10){
                         //缩小
                        // Log.i("1111", "onScale: 缩小");
                         textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontsize-3);
                     }
                         return false;
                     }

                 @Override
                 public boolean onScaleBegin(ScaleGestureDetector detector)
                 {
                         // TODO Auto-generated method stub
                         //一定要返回true才会进入onScale()这个函数
                         return true;
                     }

                 @Override
                 public void onScaleEnd(ScaleGestureDetector detector)
                 {
                         // TODO Auto-generated method stub
                     }

           }


    /**
     * 将当前显示的scrollview保存成为图片
     */
    /**
     *  对ScrollView进行截图
     * @param scrollView
     * @return
     */
    public void save_Scrollview(){
        File mFile = new File("sdcard/a.jpeg");
        Bitmap bitmap=shotScrollView(scrollView);
        try {
            FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,mFileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.i("123123", "save_Scrollview: 保存完成!!!!!!!");
    }

    /**
     * 将ScrollView进行截图保存成为bitmap，并返回
     * @param scrollView
     * @return
     */
    public static Bitmap shotScrollView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        }
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }


}
