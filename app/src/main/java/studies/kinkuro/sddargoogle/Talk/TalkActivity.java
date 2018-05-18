package studies.kinkuro.sddargoogle.Talk;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import studies.kinkuro.sddargoogle.R;

public class TalkActivity extends AppCompatActivity {

    public static final int REQ_IMAGE = 20;

    EditText etTitle, etName, etMsg;
    ImageView ivImg;

    AlertDialog dialog;

    String realImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_talk);

        etTitle = findViewById(R.id.et_title_write);
        etName = findViewById(R.id.et_name_write);
        etMsg = findViewById(R.id.et_msg_write);
        ivImg = findViewById(R.id.iv_img_write);

        setFinishOnTouchOutside(false);
        
    }

    @Override
    public void onBackPressed() {
        dialog = new AlertDialog.Builder(this).setTitle("글쓰기 취소").setMessage("글 쓰기를 취소하시겠습니까").setPositiveButton("네", cancelOKListener).setNegativeButton("아니오", null).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void clickImg(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQ_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    if(uri != null){
                        Glide.with(this).load(uri).into(ivImg);
                        realImgPath = getRealPathFromUri(uri);
                    }
                }
                break;
        }
    }

    public void clickUpload(View v){
        dialog = new AlertDialog.Builder(this).setTitle("게시글 업로드").setMessage("게시글을 올리시겠습니까").setPositiveButton("네", upLoadOKListener).setNegativeButton("아니오", null).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void clickCancel(View v){
        dialog = new AlertDialog.Builder(this).setTitle("글쓰기 취소").setMessage("글 쓰기를 취소하시겠습니까").setPositiveButton("네", cancelOKListener).setNegativeButton("아니오", null).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    DialogInterface.OnClickListener upLoadOKListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            //TODO::업로드 버튼 만들기
            uploadPost();
        }
    };

    DialogInterface.OnClickListener cancelOKListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            finish();
        }
    };
    
    public void uploadPost(){
        
        String serverUrl = "http://kinkuro.dothome.co.kr/sddar/insertDB.php";
        String title = etTitle.getText().toString();
        String name = etName.getText().toString();
        String msg = etMsg.getText().toString();
        String imgPath = realImgPath;

        SimpleMultiPartRequest multiPartRequest = new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(TalkActivity.this, "업로드에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TalkActivity.this, "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        
        multiPartRequest.addStringParam("title", title);
        multiPartRequest.addStringParam("name", name);
        multiPartRequest.addStringParam("msg", msg);
        if(imgPath != null) multiPartRequest.addFile("upload", imgPath);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(multiPartRequest);
        
    }

    //절대경로 구하는 메소드
    public String getRealPathFromUri(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(columnIndex);
        cursor.close();
        return result;
    }
}
