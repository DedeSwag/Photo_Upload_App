package com.dede.pictureupload;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Selector extends AppCompatActivity {

    private int maxSelectNum = 9;
    private List<LocalMedia> selectList = new ArrayList<>();    //Lists of selection
    private GridImageAdapter adapter;    //Grid Picture Adapter
    private RecyclerView mRecyclerView;
    private PopupWindow pop;    //Pop-up window

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("Value");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);



        Button btn_upload = (Button) this.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Selector.this,Upload.class);
                startActivity(intent);
                new Thread(runnable).start();
                //Log.d("path：",selectList.get(0).getPath());
                //Log.d("path2:",selectList.get(2).getPath());
                //uploadFtp(file);
            }
        });

        Button btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Selector.this,MainActivity.class);
                startActivity(intent);
            }
        });

        initWidget();
    }

    /**
     * Initialize widget
     * */
    private void initWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //The case where the list has elements, we can preview
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // picture
                            PictureSelector.create(Selector.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // video
                            PictureSelector.create(Selector.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // audio
                            PictureSelector.create(Selector.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
    }



    /**
     * The function of clicking "add" picture
     * */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {
            showPop();
        }
    };

    /**
     * The function of showing a pop-up window
     * */
    private void showPop() {
        View bottomView = View.inflate(Selector.this, R.layout.layout_bottom_dialog, null);
        TextView mAlbum = (TextView) bottomView.findViewById(R.id.tv_album);
        TextView mCamera = (TextView) bottomView.findViewById(R.id.tv_camera);
        TextView mCancel = (TextView) bottomView.findViewById(R.id.tv_cancel);

        pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_album:
                        //album
                        PictureSelector.create(Selector.this)
                                .openGallery(PictureMimeType.ofImage())
                                .maxSelectNum(maxSelectNum)
                                .minSelectNum(1)
                                .imageSpanCount(4)
                                .selectionMode(PictureConfig.MULTIPLE)
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case R.id.tv_camera:
                        //camera
                        PictureSelector.create(Selector.this)
                                .openCamera(PictureMimeType.ofImage())
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case R.id.tv_cancel:
                        //cancel
                        break;
                }
                closePopupWindow();
            }
        };

        mAlbum.setOnClickListener(clickListener);
        mCamera.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }

    public void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    /**
     * Picture selection Result Callback
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:

                    images = PictureSelector.obtainMultipleResult(data);
                    selectList.addAll(images);
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //File file =new File(selectList.get(1).getPath());
            File file = new File("/storage/emulated/0/DCIM/Camera/IMG_20190322_184718.jpg");
            try{
                FileInputStream in = new FileInputStream(file);
                boolean flag = FileTool.uploadFile("192.168.214.1",21,"312980341@qq.com","cyd19980819","/","test.jpg",in);
                Log.d("flag:",String.valueOf(flag));
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }


            Message msg = Message.obtain();
            Bundle data = new Bundle();
            data.putString("value","存放数据");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };
}
