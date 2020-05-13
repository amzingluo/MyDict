package luo.mydict;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import luo.mydict.View.WordAdapter;

public class HardActivity extends AppCompatActivity {

    public static ArrayList<String> wordList;
    public static Menu menu;
    public static boolean isShow;
    public static RecyclerView recyclerView;
    public static Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        EventBus.getDefault().register(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void showList(){
        wordList=Util.getHardList();
        wordList=Util.filterBan(wordList);

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
        menu.findItem(R.id.menu_info).setVisible(false);
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
            case R.id.menu_clearhard:
                Util.clearHard();
                EventBus.getDefault().post(new EventBean(EventBean.TYPE_UPDATE_WORD_LAUNCH));
                break;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(EventBean eventBean) {
        if (eventBean.getType() == EventBean.TYPE_UPDATE_WORD_LAUNCH) {

            wordList.clear();
            wordList.addAll(Util.getHardList());
            wordList=Util.filterBan(wordList);
            recyclerView.getAdapter().notifyDataSetChanged();

            Log.d("luojianjin", "eventbus");
        }
    }

}
