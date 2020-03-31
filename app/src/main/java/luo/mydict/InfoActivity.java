package luo.mydict;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        info=findViewById(R.id.info);

        info.setMovementMethod(new ScrollingMovementMethod());

        String tInfo="";

        tInfo=tInfo+"总单词："+Util.getSDFile(this).size()+"\n";

        tInfo=tInfo+"实际要背单词："+LaunchActivity.wordList.size()+"\n";
        tInfo=tInfo+"生词本："+Util.getHardList().size()+"\n";

        ArrayList<String> banList =Util.getBanList();

        tInfo=tInfo+"\n";
        tInfo=tInfo+"删除单词："+banList.size()+"\n";

        tInfo=tInfo+"列表："+"\n";
        for(int i=0;i<banList.size();i++){
            tInfo=tInfo+banList.get(i)+"\n";
        }

        info.setText(tInfo);
    }
}
