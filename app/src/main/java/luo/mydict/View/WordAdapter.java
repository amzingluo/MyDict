package luo.mydict.View;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import luo.mydict.App;
import luo.mydict.HardActivity;
import luo.mydict.LaunchActivity;
import luo.mydict.R;
import luo.mydict.Util;

public class WordAdapter extends  RecyclerView.Adapter{

    List<String> wrodList;
    private static final int FAST_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public WordAdapter(List<String> list) {
        wrodList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LaunchActivity.activity.getLayoutInflater().inflate(R.layout.word_item, parent, false);
        RecyclerView.ViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        //Launch
        if(App.isHardPage==false) {
            ((MyViewHolder) holder).word.setText(wrodList.get(position));
            if (LaunchActivity.isShow) {
                if (LaunchActivity.dictMap != null && LaunchActivity.dictMap.get(wrodList.get(position)) != null) {
                    String tranword = LaunchActivity.dictMap.get(wrodList.get(position));
                    ((MyViewHolder) holder).tword.setText(tranword);
                } else {
                    ((MyViewHolder) holder).tword.setText("");
                }
            } else {
                ((MyViewHolder) holder).tword.setText("");
            }
        }else{//Hard
            ((MyViewHolder) holder).word.setText(wrodList.get(position));
            if (HardActivity.isShow) {
                if (LaunchActivity.dictMap != null && LaunchActivity.dictMap.get(wrodList.get(position)) != null) {
                    String tranword = LaunchActivity.dictMap.get(wrodList.get(position));
                    ((MyViewHolder) holder).tword.setText(tranword);
                } else {
                    ((MyViewHolder) holder).tword.setText("");
                }
            } else {
                ((MyViewHolder) holder).tword.setText("");
            }
        }

        final int mposition=position;
        ((MyViewHolder) holder).item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClick(mposition);
                return true;
            }
        });



        ((MyViewHolder) holder).item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFastClick()){
                    return;
                }else{
                    oneClick(mposition);
                }
            }
        });
    }


    private void oneClick(int mposition){
        Log.d("luojianjin3","one click");
        String text;
        if(!App.isHardPage){
            text= LaunchActivity.wordList.get(mposition);
        }else{//hard
            text = HardActivity.wordList.get(mposition);
        }
        App.clickWord=text;
        Util.openWeb(text);
    }
    private void longClick(int mposition){
        Log.d("luojianjin3","long click");
        if(!App.isHardPage){
            Util.saveHard(LaunchActivity.wordList.get(mposition));
            LaunchActivity.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(App.Application,"加入",Toast.LENGTH_SHORT).show();
                }
            });

        }else{//hard
            Util.removeHard(HardActivity.wordList.get(mposition));
            HardActivity.wordList.remove(mposition);
            HardActivity.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

        }
    }


    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME ) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    @Override
    public int getItemCount() {
        return wrodList.size();
    }




    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView word, tword,delect;
        public ConstraintLayout item;

        public MyViewHolder(View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.word);
            tword = itemView.findViewById(R.id.translation_word);
            item = itemView.findViewById(R.id.item);
            delect = itemView.findViewById(R.id.delect);
        }
    }


}
