package com.kosmo.zipcock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class Request extends AppCompatActivity {

    private static final String TAG = "zipcock";

    String filePath1, getSex, mission_radio, mission_date;
    Calendar calendar;//현재날짜, 시간을 얻기위해 생성
    RadioButton reservation;
    int yearStr, monthStr, dayStr; //현재날자
    ImageView image;
    Spinner cate, mission_minute;
    RadioGroup sex, date;
    EditText mission_name, mission_content ,mission_money, detail_address, detail_address2;
    Geocoder geocoder;
    TextView tvResult, tvResult2;

    EditText etAddress;

    //다음ip에 필요
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    private static final int SEARCH_ADDRESS_ACTIVITY2 = 20000;
    private EditText et_address;
    private EditText et_address2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        geocoder = new Geocoder(this);

        //위경도 구할때 필요
        tvResult = findViewById(R.id.result);
        tvResult2 = findViewById(R.id.result2);


        //상세주소
        detail_address = findViewById(R.id.detail_add);
        detail_address2 = findViewById(R.id.detail_add2);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //이미지
        image = findViewById(R.id.image);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //심부름 제목
        mission_name = findViewById(R.id.title);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //요구 사항
        mission_content = findViewById(R.id.content);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //심부름비
        mission_money= findViewById(R.id.money);

        ////////////////////////////////////////////////////////////////////////////////////////////

        //선호 성별선택
        /* radio */
        sex = findViewById(R.id.sex);

        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getId();
                switch (id) {
                    case R.id.sex:
                        getSex = ((RadioButton)findViewById(checkedId)).getText().toString();
                        break;
                }
            }
        });


        ////////////////////////////////////////////////////////////////////////////////////////////


        //일정선택
        /* radio */
        date = findViewById(R.id.date);

        date.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getId();
                switch (id) {
                    case R.id.date:
                        mission_radio = ((RadioButton)findViewById(checkedId)).getText().toString();

                        break;
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////


        //카테고리 spinner
        cate= (Spinner) findViewById(R.id.category);

        ArrayAdapter<CharSequence> adapter0 = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cate.setAdapter(adapter0);

        cate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // 값 받아오기 구현해야함
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });



        ////////////////////////////////////////////////////////////////////////////////////////////

        //다음 ip주소 사용하는데 필요한 코드
        et_address = (EditText)findViewById(R.id.et_address);
        et_address2 = (EditText)findViewById(R.id.et_address2);

        Button btn_search = (Button) findViewById(R.id.ipbutton);
        Button btn_search2 = (Button) findViewById(R.id.ipbutton2);

        if (btn_search != null) {
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(Request.this, WebViewActivity.class);
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
                }
            });
        }

        if (btn_search2 != null) {
            btn_search2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(Request.this, WebViewActivity.class);
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY2);
                }
            });
        }




        ////////////////////////////////////////////////////////////////////////////////////////////

        //라디오버튼  텍스트 바꿀거임
        reservation = (RadioButton)findViewById(R.id.reservation);

        //켈린더 클래스를 통해 날짜와 시간을 구한다.
        calendar = Calendar.getInstance();
        yearStr = calendar.get(calendar.YEAR);
        monthStr = calendar.get(calendar.MONTH);
        dayStr = calendar.get(calendar.DATE);

        reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //데이터피커 대화창 객체생성
                /*
                new DatePickerDialog(컨텍스트, 리스너, 년, 월, 일)
                위생성자를 통해 객체를 생성한 후 show()를 통해 화면에 출력한다.
                 */
                DatePickerDialog dialog = new DatePickerDialog(
                        v.getContext(), listener,
                        yearStr, monthStr, dayStr
                );
                //show()메서드를 통해 화면에 띄워준다.
                dialog.show();
            }
        });



        ////////////////////////////////////////////////////////////////////////////////////////////

        //예상소요시간에 쓸 시간,분 스피너에사용
        mission_minute = (Spinner) findViewById(R.id.minute);

        //예상 소요시간 spinner
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.minute_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mission_minute.setAdapter(adapter2);

        mission_minute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // 값 받아오기 구현해야함
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////

        /* 저장권한 여부 */
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        //마지막 종료버튼
        Button button = (Button)findViewById(R.id.finish);

        //카테고리, 제목, 위치, 내용, 성별, 일시(당장regidate,예약), 예상소요시간, 심부름비 담는 리스트 또는 map 만들어야함 등록
        //값이 비어있을경우 확인해서 입력하게 토스트 띄워야함.
        //제출및 되돌아가기 구현.. 힘들지만 해봅시다
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Address> list = null;
                List<Address> list2 = null;

                //주소가 입력된 EditText에서 값을 얻어온 후 문자열로 변경
                //String address = etAddress.getText().toString();
                String address_start = et_address.getText().toString();
                String address_end = et_address2.getText().toString();

                /*경유지*/
                try{
                    //주소를 매개변수로 위경도를 얻어온다.
                    list = geocoder.getFromLocationName(address_start, 10);
                }
                catch (IOException e){
                    tvResult.setText("");
                    e.printStackTrace();
                }
                /*도착지*/
                try{
                    //주소를 매개변수로 위경도를 얻어온다.
                    list2 = geocoder.getFromLocationName(address_end, 10);
                }
                catch (IOException e){
                    tvResult2.setText("에러 : " + e.getMessage());
                    e.printStackTrace();
                }

                //결과값이 잇으면 텍스트뷰에 출력한다.
                if(list != null){
                    //tvResult.setText(list.get(0).toString());
                    Address addr = list.get(0);
                    double lat = addr.getLatitude();
                    double lon = addr.getLongitude();

                    Log.i("lon1", ""+ lon);
                    Log.i("lat2", "" +lat);

                    tvResult.setText(""+lon+"|"+""+lat+"|");
                }

                //결과값이 잇으면 텍스트뷰에 출력한다.
                if(list2 != null){
                    //tvResult2.setText(list.get(0).toString());
                    Address addr2 = list2.get(0);
                    double lat2 = addr2.getLatitude();
                    double lon2 = addr2.getLongitude();

                    Log.i("lon1", ""+ lon2);
                    Log.i("lat2", "" +lat2);

                    tvResult2.setText(""+lon2+"|"+""+lat2+"|");
                }

                ////////////////////////////////////////////////////////////////////////////////////

                AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(Request.this);

                if(mission_name.getText().toString().length()<5 || mission_name.getText().toString().equals(""))
                {
                    myAlertBuilder.setTitle("알림");
                    myAlertBuilder.setMessage("5자이상 제목을 입력하세요.");
                    myAlertBuilder.show();
                }
                else if(et_address2.getText().toString().equals(""))
                {
                    myAlertBuilder.setTitle("알림");
                    myAlertBuilder.setMessage("도착지를 입력해 주세요.");
                    myAlertBuilder.show();
                }
                else if(mission_content.getText().toString().equals(""))
                {
                    myAlertBuilder.setTitle("알림");
                    myAlertBuilder.setMessage("요구사항을 입력해 주세요.");
                    myAlertBuilder.show();
                }
                else if(mission_money.getText().toString().equals(""))
                {
                    myAlertBuilder.setTitle("알림");
                    myAlertBuilder.setMessage("금액을 입력해 주세요.");
                    myAlertBuilder.show();
                }
                else if(Integer.parseInt(mission_money.getText().toString()) <3000)
                {
                    myAlertBuilder.setTitle("알림");
                    myAlertBuilder.setMessage("3000이상 금액을 입력해주세요.");
                    myAlertBuilder.show();
                }
                else{
                    mission_map(v);

                    //결제창 이동하기
                    Intent payIntent = new Intent(Request.this, PayFragment.class);
                    //결제페이지로 보낼 폼값들
                    payIntent.putExtra("title", mission_name.getText().toString()); // 제목
                    payIntent.putExtra("content", mission_content.getText().toString()); // 요청내용
                    payIntent.putExtra("cost", mission_money.getText().toString()); // 돈
                    startActivity(payIntent);
                }
            }

        });

        ////////////////////////////////////////////////////////////////////////////////////////////

    }//onCreate끝

    //데이트피커 리스너 선언(onCreate() 외부에 별도로 정의)
    private  DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            //선택한 날짜를 텍스트 뷰에 입력한다.
            reservation.setText(String.format("%d년 %d월 %d일", year, (month+1), dayOfMonth));
            /*
            Calendar객체를 통해 얻어온 날짜중 월은 0~11까지를 반환하므로
            +1해줘야 한다.
             */

            //위의 내용을 토스트로 띄워준다.
            Toast.makeText(getApplicationContext(),
                    year+"년"+(month+1)+"월"+dayOfMonth+"일",
                    Toast.LENGTH_LONG).show();
        }
    };


    //ip주소 사용하는데 필요 //사진갤러리가져오기
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        et_address.setText(data);
                    }
                }
                break;

            case SEARCH_ADDRESS_ACTIVITY2:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        et_address2.setText(data);
                    }
                }
                break;

            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selPhotoUri = intent.getData();
                    showCapturedImage(selPhotoUri);
                }
                break;
        }
    }

    //이미지 선택
    public void BtnGetImage(View v) {
        //인텐트를 통해 타입과 데이터를 설정한 후 선택한다.
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
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

    //map에 심부름 정보 담기
    public void mission_map(View v) {

        //선호 성별담기
        String setSex = "";
        if(getSex.equals("상관없음")) {
            setSex = "1";
        }
        else if (getSex.equals("남자")) {
            setSex = "2";
        }
        else {
            setSex = "3";
        }

        String setMinute = "";
        if((mission_minute.getSelectedItem().toString()).equals("10분이내")) {
            setMinute = "1";
        }
        if((mission_minute.getSelectedItem().toString()).equals("10분~20분")) {
            setMinute = "2";
        }
        if((mission_minute.getSelectedItem().toString()).equals("20분~40분")) {
            setMinute = "3";
        }
        if((mission_minute.getSelectedItem().toString()).equals("40분~60분")) {
            setMinute = "4";
        }
        if((mission_minute.getSelectedItem().toString()).equals("60분이상")) {
            setMinute = "5";
        }

        String member_id = getIntent().getStringExtra("member_id"); //현재 로그인된 아이디
        mission_date  = reservation.getText().toString();
        String start, end;

        start = et_address.getText().toString() +"|"+ detail_address.getText().toString();
        end = et_address2.getText().toString() +"|"+ detail_address2.getText().toString();
        if(start.equals("|")){
            start=", ||";
        }
        String startAdd[] = start.split(", ");
        String endAdd[] = end.split(", ");

        start = tvResult.getText().toString();
        start = start.concat(startAdd[1]);

        end = tvResult2.getText().toString();
        end = end.concat(endAdd[1]);


        //파라미터를 맵에 저장
        HashMap<String, String> param1 = new HashMap<>();
        param1.put("mission_id", member_id);
        param1.put("mission_category", cate.getSelectedItem().toString());
        param1.put("mission_name", mission_name.getText().toString());
        param1.put("mission_content", mission_content.getText().toString());
        param1.put("mission_sex", setSex);
        param1.put("mission_waypoint", start);
        param1.put("mission_end", end);

        if (mission_radio.equals("지금즉시"))
        {
            param1.put("mission_mission", "1");
            param1.put("mission_reservation", "지금즉시");
        }
        else
        {
            param1.put("mission_mission", "2");
            param1.put("mission_reservation", mission_date);
        }
        param1.put("mission_time", setMinute);
        param1.put("mission_cost", mission_money.getText().toString());
        param1.put("mission_status", "1");
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
        }

        String ip = getResources().getString(R.string.ip);
        @Override
        protected JSONArray doInBackground(Object... objects) {

            JSONArray rtn = null;
            try {
                String sUrl = "http://"+ip+":8081/zipcock/android/request.do";
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

            if (jsonArray != null) {
                Toast.makeText(getApplicationContext(), "결제창으로 이동합니다.", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(mContext, "에러발생! 입력내용을 확인해주세요", Toast.LENGTH_SHORT).show();
            }
        }
    }

}//class끝