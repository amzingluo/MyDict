package luo.mydict;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import luo.mydict.View.RecycleItemTouchHelper;
import luo.mydict.View.WordAdapter;

public class LaunchActivity extends AppCompatActivity {

    public static ArrayList<String> wordList;

    public static Context context;

    public static HashMap<String,String> dictMap;

    public static Menu menu;

    public static Activity activity;

    public static boolean isShow;
    private RecyclerView recyclerView;

    private SharedPreferences preferences = App.Application.getSharedPreferences("dict", Context.MODE_PRIVATE);
    private SharedPreferences.Editor editor = preferences.edit();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        EventBus.getDefault().register(this);
        context=this;
        dictMap=Util.getDictMap();
        activity =LaunchActivity.this;

        isShow=false;

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    404);
        }else{
            showList();


        }

        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        startActivity(intent);*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        App.isHardPage=false;



    }

    private void showList(){
        wordList=Util.getSDFile(this);
        wordList=Util.filterBan(wordList);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        WordAdapter myAdapter = new WordAdapter(wordList);
        recyclerView.setAdapter(myAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));



        ItemTouchHelper.Callback callback=new RecycleItemTouchHelper();
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        if (recyclerView.getAdapter().getItemCount() != 0) {
            int position = preferences.getInt("LastPosition", 0);
            if (position > recyclerView.getAdapter().getItemCount()) {
                position = recyclerView.getAdapter().getItemCount();
            }
            position = position == -1 ? 0 : position;
            recyclerView.scrollToPosition(position);
        }

        Util.tranList();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==404){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showList();
            } else {
                Toast.makeText(this,"请获取权限",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        404);
            }
        }
    }

    private void triggerShow(){

        isShow=!isShow;
        if(isShow){
            menu.findItem(R.id.menu_show).setTitle("隐藏");
        }else{
            menu.findItem(R.id.menu_show).setTitle("显示");
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        this.menu=menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_download).setVisible(false);
        menu.findItem(R.id.menu_info).setVisible(false);//暂时先禁用信息页面，显得太繁琐了
        menu.findItem(R.id.menu_clearhard).setVisible(false);//暂时先禁用信息页面，显得太繁琐了
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) //得到被点击的item的itemId
        {
            case  R.id.menu_show :
                triggerShow();
                break;
            case  R.id.menu_hard :
                startActivity(new Intent(this,HardActivity.class));
                break;
            case R.id.menu_info:
                startActivity(new Intent(this,InfoActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (recyclerView != null && recyclerView.getAdapter().getItemCount() != 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int position = layoutManager.findFirstCompletelyVisibleItemPosition();
            position = position == -1 ? 0 : position;
            editor.putInt("LastPosition", position);
            editor.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.stopThread();
        EventBus.getDefault().unregister(this);

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(EventBean eventBean) {
        if (eventBean.getType() == EventBean.TYPE_UPDATE_WORD_LAUNCH) {

            wordList.clear();
            wordList.addAll(Util.getSDFile(this));
            wordList=Util.filterBan(wordList);
            recyclerView.getAdapter().notifyDataSetChanged();

            Log.d("luojianjin", "eventbus");
        }
    }
}
