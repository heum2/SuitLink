package com.example.suitlink;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CustomAdapter extends PagerAdapter {

    private static final String TAG = "ViewPagerAdapter";
    LayoutInflater inflater;
    Context mcontext;
    Boolean check;

    public CustomAdapter(LayoutInflater inflater,Context context,Boolean check) {
        // TODO Auto-generated constructor stub
        //전달 받은 LayoutInflater를 멤버변수로 전달
        this.inflater=inflater;
        this.mcontext = context;
        this.check = check;
    }

    //PagerAdapter가 가지고 잇는 View의 개수를 리턴

    //보통 보여줘야하는 이미지 배열 데이터의 길이를 리턴

    @Override

    public int getCount() {
        SharedPreferences suituri = mcontext.getSharedPreferences("SuitUri",Context.MODE_PRIVATE);
        String json = suituri.getString("imageList","");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> suitUris = gson.fromJson(json,type);

        // TODO Auto-generated method stub
        return suitUris.size(); //이미지 개수 리턴
    }



    //ViewPager가 현재 보여질 Item(View객체)를 생성할 필요가 있는 때 자동으로 호출

    //쉽게 말해, 스크롤을 통해 현재 보여져야 하는 View를 만들어냄.

    //첫번째 파라미터 : ViewPager

    //두번째 파라미터 : ViewPager가 보여줄 View의 위치(가장 처음부터 0,1,2,3...)

    @Override

    public Object instantiateItem(final ViewGroup container, final int position) {

        // TODO Auto-generated method stub

        Log.d("위치 값 확인",""+position);

        View view=null;

            //새로운 View 객체를 Layoutinflater를 이용해서 생성

        //만들어질 View의 설계는 res폴더>>layout폴더>>viewpater_childview.xml 레이아웃 파일 사용

        view= inflater.inflate(R.layout.viewpager_childview, null);

        //만들어진 View안에 있는 ImageView 객체 참조

        //위에서 inflated 되어 만들어진 view로부터 findViewById()를 해야 하는 것에 주의.

        ImageView img= (ImageView)view.findViewById(R.id.img_viewpager_childimage);

        //ImageView에 현재 position 번째에 해당하는 이미지를 보여주기 위한 작업

        //현재 position에 해당하는 이미지를 setting
        SharedPreferences suituri = mcontext.getSharedPreferences("SuitUri",Context.MODE_PRIVATE);
        String json = suituri.getString("imageList","");
        Log.d("json",json);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        Log.d("type",""+type);
        final ArrayList<String> suitUris = gson.fromJson(json,type);
        Log.d("suitUri",""+suitUris);
        String suri = suitUris.get(position); // 클릭했던 이미지의 절대값을 넘겨준다.
        Log.d("suri",suri);
        if(check==true) { //편집했을 때
            Glide.with(mcontext).load(suri).into(img);
        }else{ // 추가했을 때
            Uri uri = Uri.fromFile(new File(suri)); //파일 형태로 uri 를 만든 것을 uri객체로 만들어준건가?
            Glide.with(mcontext).load(uri)/*.override(100,250)*/.into(img); // 오버라이드 계산해서 리사이징 해줘야할 듯

        }

        //ViewPager에 만들어 낸 View 추가
        container.addView(view);

        //뷰페이저 꾸욱 누르면 지우는 기능
        final View finalView = view;
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e(TAG, "onLongClick: "+position);
                show(position,suitUris,container, finalView);
                return false;
            }
        });

        //Image가 세팅된 View를 리턴
        return view;

    }

    //화면에 보이지 않은 View는 파괴를 해서 메모리를 관리함.

    //첫번째 파라미터 : ViewPager

    //두번째 파라미터 : 파괴될 View의 인덱스(가장 처음부터 0,1,2,3...)

    //세번째 파라미터 : 파괴될 객체(더 이상 보이지 않은 View 객체)

    @Override

    public void destroyItem(ViewGroup container, int position, Object object) {

        //롱클릭으로 제거가 되면, 여기서 리스너처럼 받을 듯..?
        Log.e(TAG, "destroyItem: 포지션 : "+position );
        Log.e(TAG, "destroyItem: 오브젝트 : "+object);

        //ViewPager에서 보이지 않는 View는 제거
        //세번째 파라미터가 View 객체 이지만 데이터 타입이 Object여서 형변환 실시
        container.removeView((View)object);
    }

    //instantiateItem() 메소드에서 리턴된 Ojbect가 View가  맞는지 확인하는 메소드
    @Override

    public boolean isViewFromObject(View v, Object obj) {

        // TODO Auto-generated method stub

        return v==obj;

    }

    // 뷰페이저 갱신할 때 씀
    @Override

    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }

    //뷰페이저가 변경되었을 때, 호출
    @Override
    public void notifyDataSetChanged() {
        Log.e(TAG, "notifyDataSetChanged: Ok");
        super.notifyDataSetChanged();
        View view=null;
        getItemPosition(view);
    }

    //다이얼로그 보여준다.
    void show(final int position, final ArrayList<String> suituris, final ViewGroup container, final View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("삭제");
        builder.setMessage("사진을 삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "onClick: position : "+position);

                        suituris.remove(position);
                        SharedPreferences suituri = mcontext.getSharedPreferences("SuitUri",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = suituri.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(suituris);
                        editor.putString("imageList",json);
                        editor.commit();

                        destroyItem(container,position,view);
                        notifyDataSetChanged();

                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

}
