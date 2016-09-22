package com.zzti.fengyongge.uploadimages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;
import com.zzti.fengyongge.uploadimages.app.Config;
import com.zzti.fengyongge.uploadimages.dialog.CustomAlertDialog;
import com.zzti.fengyongge.uploadimages.upload.HttpMultipartPost;
import com.zzti.fengyongge.uploadimages.upload.ImageUtils;
import com.zzti.fengyongge.uploadimages.upload.UploadCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    public final static String NET_DOMAIN ="http://ktvapi.ediankai.com.cn/api/upload_img";//测试地址
    private View tv_upload,tv_profit;
    CustomAlertDialog progressDialog;
    private AlertDialog alertDialog;
    private String bitmap_url;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            final JSONObject object = (JSONObject) msg.obj;
            try {
                if (object.getString("code").trim().equals("0")) {
//                    ImageLoader.getInstance().displayImage(bitmap_url, iv_logo);
                    Toast.makeText(MainActivity.this,object.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        };
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Config.ScreenMap = Config.getScreenSize(this, this);

        tv_upload = findViewById(R.id.tv_upload);
        tv_profit = findViewById(R.id.tv_profit);

        tv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotoSelectorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("limit", 1);//number是选择图片的数量
                startActivityForResult(intent, 0);
            }
        });

        tv_profit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headImg();
            }
        });


    }

    //选择方式
    private void headImg() {
        List<String> list = new ArrayList<>();
        View vv = LayoutInflater.from(this).inflate(R.layout.item_dialog_title, null);
        list.add("相册");
        list.add("相机");
        alertDialog = new AlertDialog.Builder(MainActivity.this).setCustomTitle(vv)
                .setAdapter(new ArrayAdapter<String>(this, R.layout.select_dialog, R.id.tv_carame, list), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            showChoosePicture();
                        } else if (which == 1) {
                            showTakePicture();
                        }
                    }
                }).create();
        alertDialog.show();
    }


    //URI转绝对路径
    public String getAbsoluteImagePath(Uri uri) {
        // can post image
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    //拍照
    private void showTakePicture() {
        ImageUtils.imageUriFromCamera = ImageUtils
                .createImagePathUri(MainActivity.this);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // MediaStore.EXTRA_OUTPUT参数不设置时,系统会自动生成一个uri,但是只会返回一个缩略图
        // 返回图片在onActivityResult中通过以下代码获取
        // Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.imageUriFromCamera);
        startActivityForResult(intent, ImageUtils.GET_IMAGE_BY_CAMERA);

    }

    //选照片
    private void showChoosePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, 1);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    List<String> paths = (List<String>) data.getExtras().getSerializable("photos");//path是选择拍照或者图片的地址数组
                    //处理代码
                    if (paths.size() > 0) {
                        Map<String, String> map = new TreeMap<String, String>();
                        progressDialog = new CustomAlertDialog(MainActivity.this);
                        HttpMultipartPost httpMultipartPost = new HttpMultipartPost(
                                MainActivity.this, progressDialog, NET_DOMAIN,
                                "file", paths, map, new UploadCallback() {
                            @Override
                            public void onSuccessData(JSONObject data) {
                                String pic = JSON.parseArray(data.getString("img")).get(0).toString();
                                Toast.makeText(MainActivity.this, "上传成功,图片路径为：" + pic, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onEorrData(JSONObject data) {
                                Toast.makeText(MainActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        });
                        httpMultipartPost.execute();
                    }
                }
                break;

            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    ImageUtils.cropImage(this, data.getData(), false);
                }
                break;
            // 如果是调用相机拍照时
            case ImageUtils.GET_IMAGE_BY_CAMERA:

                if (ImageUtils.imageUriFromCamera != null && resultCode == -1) {
                    ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera, true);
                }

                break;
            // 取得裁剪后的图片
            case 3:
                break;
            case ImageUtils.CROP_IMAGE:
                if (ImageUtils.cropImageUri != null && data != null
                        && resultCode == -1) {
                    bitmap_url = getAbsoluteImagePath(ImageUtils.cropImageUri);
                    Log.i("fyg", "--bitmap_url-" + bitmap_url);
                    //上传头像接口
//                    goUploadhead();
                }
                break;


            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // 换头像
//    private void goUploadhead() {
//        headBean = MyNet.Inst().useredit(MainActivity.this, user.getToken(), user.getMerchant_id());
//        new Thread() {
//            private Map<String, String> files;
//            @Override
//            public void run() {
//                files = new HashMap<String, String>();
//                files.put("portrait", bitmap_url);
//                final Map<String, String> map = new HashMap<String, String>();
//                map.put("version", headBean.getVersion());
//                map.put("time", headBean.getTime());
//                map.put("noncestr", headBean.getNoncestr());
//                map.put("merchant_id", user.getMerchant_id());
//                map.put("token", user.getToken());
//                try {
//                    JSONObject result;
//                    result = UploadHelper.postFile(headBean.getApiUri(), map, files);
//                    Log.i("fyg","头像" + result);
//                    Message message = new Message();
//                    message.obj = result;
//                    handler.sendMessage(message);
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//
//                    e.printStackTrace();
//                }
//
//            }
//        }.start();
//    }
}
