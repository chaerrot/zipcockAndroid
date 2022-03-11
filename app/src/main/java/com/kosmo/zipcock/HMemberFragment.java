package com.kosmo.zipcock;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


/* 보류 */
public class HMemberFragment extends AppCompatActivity {

    private static final String TAG = "zipcock";

    String filePath1;
    ImageView image;
    EditText h_id, h_pass, h_pass2, h_email, h_name, h_phone, account, introduce;
    Spinner hSex, hAge, bank;
    Button check, memberJoin;
    TextView passCheck;
    RadioGroup vehicle;
    String getVeh;
    ProgressDialog dialog;
    ProgressDialog dialog2;

    String[] sex = {"성별 고르기" ,"남자", "여자"};
    String[] age = {"나이 고르기", "10대", "20대", "30대", "40대", "50대", "기타"};
    String[] ban = {"은행" ,"기업은행", "국민은행", "우리은행", "신한은행",
            "하나은행", "농협은행", "SC은행", "카카오뱅크"};
    private boolean validate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_h_member);

        passCheck = findViewById(R.id.passCheck);
        image = findViewById(R.id.image);
        h_id = findViewById(R.id.hId);
        h_pass = findViewById(R.id.hPass);
        h_pass2 = findViewById(R.id.hPass2);
        h_email = findViewById(R.id.hEmail);
        h_name = findViewById(R.id.hName);
        h_phone = findViewById(R.id.hPhone);
        account = findViewById(R.id.account);
        introduce = findViewById(R.id.introduce);

        /* Spinner만 처리 되는거 따로 만들기 */
        hSex = findViewById(R.id.hSex);
        hAge = findViewById(R.id.hAge);
        bank = findViewById(R.id.bank);

        /* Button */
        check = findViewById(R.id.idCheck);
        memberJoin = findViewById(R.id.memberJoin);

        /* radio */
        vehicle = findViewById(R.id.vehicle_group);



        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIcon(android.R.drawable.ic_dialog_email);
        dialog.setTitle("아이디 확인중..");
        dialog.setMessage("서버로부터 응답을 기다리고 있습니다.");

        dialog2 = new ProgressDialog(this);
        dialog2.setCancelable(true);
        dialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog2.setIcon(android.R.drawable.ic_dialog_email);
        dialog2.setTitle("회원가입 진행중..");
        dialog2.setMessage("서버로부터 응답을 기다리고 있습니다.");

        /* 라디오버튼 */
        vehicle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getId();
                switch (id) {
                    case R.id.vehicle_group:
                        getVeh = ((RadioButton)findViewById(checkedId)).getText().toString();
                        break;
                }
            }
        });

        /* 비밀번호 일치여부 */
        h_pass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (h_pass.getText().toString().equals(h_pass2.getText().toString())) {
                    passCheck.setText("비밀번호가 일치합니다.");
                }
                else {
                    passCheck.setText("비밀번호가 일치하지 않습니다.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* 저장권한 여부 */
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        /* 성별, 나이, 은행 Spinner처리 */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, sex
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        hSex.setAdapter(adapter);
        hSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, age
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        hAge.setAdapter(adapter2);
        hAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, ban
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        bank.setAdapter(adapter3);
        bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /* 아이디 중복확인 */
        check.setOnClickListener(new View.OnClickListener() {
            String ip = getResources().getString(R.string.ip);
            @Override
            public void onClick(View v) {

                String requestUrl = "http://"+ip+":8081/zipcock/android/memberCheck.do";
                requestUrl +="?member_id="+h_id.getText().toString();
                Log.d(TAG, requestUrl);

                new AsyncHttpRequest().execute(requestUrl);
            }
        });
    }



    //이미지 선택
    public void BtnGetImage(View v) {
        //인텐트를 통해 타입과 데이터를 설정한 후 선택한다.
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    //헬퍼 회원가입
    public void BtnMemberJoin(View v) {

        if(h_id.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            h_id.requestFocus();
            return;
        }
        if(h_pass.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "패스워드를 입력해주세요", Toast.LENGTH_SHORT).show();
            h_pass.requestFocus();
            return;
        }
        if(h_pass2.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "패스워드확인을 해주세요", Toast.LENGTH_SHORT).show();
            h_pass2.requestFocus();
            return;
        }
        if(passCheck.getText().toString().equals("비밀번호가 일치하지 않습니다.")) {
            Toast.makeText(getApplicationContext(), "비밀번호 일치여부 확인해주세요", Toast.LENGTH_SHORT).show();
            h_pass2.requestFocus();
            return;
        }
        if(h_email.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
            h_email.requestFocus();
            return;
        }
        if(h_name.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            h_name.requestFocus();
            return;
        }
        if(hAge.getSelectedItem().toString() == "성별") {
            Toast.makeText(getApplicationContext(), "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
            hAge.requestFocus();
            return;
        }
        if(hSex.getSelectedItem().toString() == "나이") {
            Toast.makeText(getApplicationContext(), "나이를 선택해주세요", Toast.LENGTH_SHORT).show();
            hSex.requestFocus();
            return;
        }
        if(h_phone.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "핸드폰번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            h_phone.requestFocus();
            return;
        }
        if(bank.getSelectedItem().toString().equals("은행")) {
            Toast.makeText(getApplicationContext(), "은행을 선택해주세요", Toast.LENGTH_SHORT).show();
            bank.requestFocus();
            return;
        }
        if(account.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "계좌번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            account.requestFocus();
            return;
        }
        if(vehicle.getCheckedRadioButtonId() == 0) {
            Toast.makeText(getApplicationContext(), "이동수단을 골라주세요", Toast.LENGTH_SHORT).show();
            h_id.requestFocus();
            return;
        }

        if(introduce.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "자기소개를 입력해주세요", Toast.LENGTH_SHORT).show();
            introduce.requestFocus();
            return;
        }

        String getSex = "";
        if(hSex.getSelectedItem().toString().equals("남자")) {
            getSex = "0";
        }
        else if(hSex.getSelectedItem().toString().equals("여자")) {
            getSex = "1";
        }

        String setVeh = "";
        if(getVeh.equals("자동차")) {
            setVeh = "0";
        }
        else if (getVeh.equals("오토바이")) {
            setVeh = "1";
        }
        else if (getVeh.equals("자전거")) {
            setVeh = "2";
        }
        else if (getVeh.equals("도보")) {
            setVeh = "3";
        }
        else {
            setVeh = "4";
        }

        //파라미터를 맵에 저장
        HashMap<String, String> param1 = new HashMap<>();
        param1.put("member_id", h_id.getText().toString());
        param1.put("member_pass", h_pass.getText().toString());
        param1.put("member_email", h_email.getText().toString());
        param1.put("member_name", h_name.getText().toString());
        param1.put("member_age", hAge.getSelectedItem().toString());
        param1.put("member_bank", bank.getSelectedItem().toString());
        param1.put("member_sex", getSex);
        param1.put("member_vehicle", setVeh);
        param1.put("member_phone", h_phone.getText().toString());
        param1.put("member_account", account.getText().toString());
        param1.put("member_introduce", introduce.getText().toString());
        HashMap<String, String> param2 = new HashMap<>();
        if(filePath1 == null) {

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.customer2);
            image.setImageBitmap(bm);

            //기존에 있던 파일 삭제
            try{
                File file = new File("data/data/com.kosmo.zipcock/files/");
                File[] flist = file.listFiles();
                for(int i = 0 ; i < flist.length ; i++)
                {
                    String fname = flist[i].getName();
                    if(fname.equals("customer2.png"))
                    {
                        flist[i].delete();
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            //파일 생성
            try{

                File file = new File("customer2.png");
                FileOutputStream fos = openFileOutput("customer2.png" , 0);
                bm.compress(Bitmap.CompressFormat.PNG, 100 , fos);
                fos.flush();
                fos.close();


            }
            catch(Exception e) {
                e.printStackTrace();
            }

            filePath1 = "data/data/com.kosmo.zipcock/files/customer2.png";

        }
        param2.put("filename", filePath1);

        //AsyncTask를 통해 생성한 객체를 통해 HTTP 통신 시작
        UploadAsync networkTask = new UploadAsync(getApplicationContext(), param1, param2);
        //doInBackground() 호출
        networkTask.execute();
    }

    // 중복확인 처리
    class AsyncHttpRequest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sBuffer = new StringBuffer();
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStream out = connection.getOutputStream();
                out.flush();
                out.close();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "HTTP OK 성공");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8")
                    );
                    String responseData;
                    while ((responseData = reader.readLine()) != null){
                        sBuffer.append(responseData + "\n\r");
                    }
                    reader.close();
                }
                else {
                    Log.i(TAG, "HTTP OK 안됨");
                }
                Log.i(TAG, sBuffer.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return sBuffer.toString();
        }

        //서버 통신 후 보여지는 거
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i(TAG, s);
            dialog.dismiss();
            String result = jsonParser(s);

            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }

    }
    public String jsonParser(String data){
        StringBuffer sb = new StringBuffer();
        try {
            JSONObject jsonObject = new JSONObject(data);
            int success = Integer.parseInt(jsonObject.getString("isCheck"));
            if (success==1){
                JSONObject idChe = jsonObject.getJSONObject("idList");
                String idCheck = idChe.toString();
                String id = idChe.getString("member_id").toString();

                Toast.makeText(getApplicationContext(), id+"는 이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                sb.append("사용가능한 아이디 입니다.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    //갤러리 리스트에서 사진 데이터를 가져오기 위한 부분
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri selPhotoUri = data.getData();
                showCapturedImage(selPhotoUri);
            }
        }
    }
    //사진의 절대경로 구하기(사용자 정의 메소드)
    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }
    /*
    사진의 회전값을 처리해주는 메소드 : 사진의 회전값을 처리하지 않으면 사진을
        찍은 방향대로 이미지뷰에 설정할 수 없게된다.
     */
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    //사진을 정방향대로 회전하기 위한 메소드
    private Bitmap rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(src, 0, 0,
                src.getWidth(), src.getHeight(), matrix, true);
    }
    //사진의 절대경로를 구한 후 이미지뷰에 선택한 사진을 설정함.
    private void showCapturedImage(Uri imageUri) {
        filePath1 = getRealPathFromURI(imageUri);//사용자정의함수
        Log.d(TAG, "path1:"+filePath1);
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(filePath1);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        int exifOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);//사용자정의함수

        Bitmap bitmap = BitmapFactory.decodeFile(filePath1);
        Bitmap rotatedBitmap = rotate(bitmap, exifDegree);//사용자정의함수
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 800, 800, false);
        bitmap.recycle();

        image.setImageBitmap(scaledBitmap);
    }

    //서버와 통신을 위한 클래스
    public class UploadAsync extends AsyncTask<Object, Integer, JSONArray> {

        private Context mContext;
        private HashMap<String, String> param;//파라미터
        private HashMap<String, String> files;//사진파일
        public UploadAsync(Context context, HashMap<String, String> param,
                           HashMap<String, String> files){
            mContext = context;
            this.param = param;
            this.files = files;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!dialog2.isShowing())
                dialog2.show();
        }

        String ip = getResources().getString(R.string.ip);
        @Override
        protected JSONArray doInBackground(Object... objects) {

            JSONArray rtn = null;
            try {
                String sUrl = "http://"+ip+":8081/zipcock/android/memberJoin.do";
                FileUpload multipartUpload = new FileUpload(sUrl, "UTF-8");
                rtn = multipartUpload.upload(param, files);
                Log.d(TAG, rtn.toString());
                Log.d(TAG, sUrl);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return rtn;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            dialog2.dismiss();

            if (jsonArray != null) {
                Toast.makeText(mContext, "회원가입 완료! 환영합니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
            else {
                Toast.makeText(mContext, "에러발생! 입력내용을 확인해주세요", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

