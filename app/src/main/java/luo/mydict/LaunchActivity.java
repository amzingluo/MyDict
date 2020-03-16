package luo.mydict;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class LaunchActivity extends AppCompatActivity {

    public static List<String> wordList;

    public static Context context;

    public static HashMap<String,String> dictMap;

    public static Menu menu;

    public static Activity activity;

    public static boolean isShow;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        WordAdapter myAdapter = new WordAdapter(wordList);
        recyclerView.setAdapter(myAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));

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
                Toast.makeText(this,"请获取权限",Toast.LENGTH_LONG).show();
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
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.stopThread();
    }
}
