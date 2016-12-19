package jp.techacademy.hirotoshi.yoshioka.autoslideshowapp;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ボタンIDの取得//
        Button buttonForward = (Button) findViewById(R.id.buttonForward);
        buttonForward.setOnClickListener(this);

        Button buttonPlayStop = (Button) findViewById(R.id.buttonPlayStop);
        buttonPlayStop.setOnClickListener(this);

        Button buttonwBackward = (Button) findViewById(R.id.buttonBackward);
        buttonwBackward.setOnClickListener(this);
        //ボタンIDの取得//

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Button buttonPlayStop = (Button) findViewById(R.id.buttonPlayStop);
        Button buttonForward = (Button) findViewById(R.id.buttonForward);
        Button buttonBackward = (Button) findViewById(R.id.buttonBackward);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                else{
                    buttonPlayStop.setEnabled(false);
                    buttonForward.setEnabled(false);
                    buttonBackward.setEnabled(false);
                }

                break;
            default:
                break;
        }
    }

    // 画像の情報を取得する
    private void getContentsInfo() {
        Button buttonPlayStop = (Button) findViewById(R.id.buttonPlayStop);
        Button buttonForward = (Button) findViewById(R.id.buttonForward);
        Button buttonBackward = (Button) findViewById(R.id.buttonBackward);

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        //  Log.d("logtest", "カーソルサイズ" + cursor.getCount()); カーソルサイズ 取得
        if (cursor.moveToFirst())
        do{
            // indexからIDを取得し、そのIDから画像のURIを取得する
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
            String Uri = imageUri.toString();
        //    Log.d("logtest", "これが私の" + Uri + "です");
            imageUriList.add( imageUri ); // リストに追加
        }while (cursor.moveToNext());
        cursor.close();
       // Log.d("logtest", "while の後のリストのサイズ" + this.imageUriList.size()+"");

        if (imageUriList.size() == 0) {
            buttonPlayStop.setEnabled(false);
            buttonForward.setEnabled(false);
            buttonBackward.setEnabled(false);
        }
    }

    //画像のリスト
    int showIndex = 0;
    ArrayList<Uri> imageUriList = new ArrayList<Uri>();
    //画像のリスト


    //Timer + handler 関連
    private Timer timer = null;
    private Handler handler = new Handler();
    public void startTimer()
    {
        if(this.timer==null)
        {
            TimerTask task = new TimerTask()
            {
                @Override
                public void run()
                {
                    Runnable runnable = new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showIndex++;
                                if ( showIndex >= imageUriList.size() ) {
                                    showIndex = 0;
                                }
                                //Log.d("logtest", "run行きました2。"+ imageUriList.size() + "インデックスは" + showIndex);
                                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                //Log.d("logtest", "run行きました3。"+ imageUriList.size() + "インデックスは" + showIndex);
                                imageView.setImageURI(imageUriList.get(showIndex));
                                //Log.d("logtest", "run行きました4。"+ imageUriList.size() + "インデックスは" + showIndex);

                            //Log.d("logtest", "写真の表示"); // ここで写真を表示する。
                        }
                    };
                    handler.post(runnable);
                }
            };
            this.timer = new Timer();
            this.timer.schedule(task, 2000, 2000);
        }
    }

    public void stopTimer()
    {
        if(this.timer!=null)
        {
            this.timer.cancel();
            this.timer = null;
        }
    }

    public void onClick(View v) {
        Button buttonPlayStop = (Button) findViewById(R.id.buttonPlayStop);
        Button buttonForward = (Button) findViewById(R.id.buttonForward);
        Button buttonBackward = (Button) findViewById(R.id.buttonBackward);

            if (v.getId() == R.id.buttonPlayStop) {
                if (timer == null) {
                    startTimer();
                    buttonPlayStop.setText("停止");
                    buttonForward.setEnabled(false);
                    buttonBackward.setEnabled(false);
                } else if (timer != null) {
                    stopTimer();
                    buttonPlayStop.setText("再生");
                    buttonForward.setEnabled(true);
                    buttonBackward.setEnabled(true);
                }
            }


// 進むボタン

            if (v.getId() == R.id.buttonForward) {

                showIndex++;
                if (showIndex >= this.imageUriList.size()) {
                    showIndex = 0;
                }
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(this.imageUriList.get(showIndex));

            }

            if (v.getId() == R.id.buttonBackward) { // 戻るボタン
                showIndex--;
                if (showIndex <= 0) {
                    showIndex = this.imageUriList.size() - 1;
                }
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(this.imageUriList.get(showIndex));
            }



    }

}

