package luo.mydict;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HardActivity extends AppCompatActivity {

    public static List<String> wordList;
    public static Menu menu;
    public static boolean isShow;
    public static RecyclerView recyclerView;
    public static Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        activity=this;
        isShow=false;

        setTitle("生词本");

        showList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        App.isHardPage=true;
    }

    private void showList(){
        wordList=Util.getHardList();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        WordAdapter myAdapter = new WordAdapter(wordList);
        recyclerView.setAdapter(myAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));

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
        menu.findItem(R.id.menu_hard).setVisible(false);
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
        }
        return true;
    }

}
