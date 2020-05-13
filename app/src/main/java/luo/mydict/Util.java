package luo.mydict;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Translation.TransApi;

public class Util {

    static String filePath = Environment.getExternalStorageDirectory().getPath() + "/单词/单词.txt";
    private static final String APP_ID = "20191112000356308";
    private static final String SECURITY_KEY = "eGk7VLkPBuyUqKktLfge";
    private static TransApi api = new TransApi(APP_ID, SECURITY_KEY);

    private static SharedPreferences preferences = LaunchActivity.context.getSharedPreferences("dict", Context.MODE_PRIVATE);
    private static SharedPreferences.Editor editor = preferences.edit();
    private static Gson gson = new Gson();


    public static ArrayList<String> getSDFile(Context context) {

        ArrayList newList = new ArrayList<String>();
        try {
            File file = new File(filePath);
            int count = 0;//初始化 key值
            if (file.isFile() && file.exists()) {//文件存在
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    if (!"".equals(lineTxt)) {
                        String reds = lineTxt.split("\\+")[0];  //java 正则表达式
                        newList.add(count, reds);
                        count++;
                    }
                }
                isr.close();
                br.close();
            } else {
                Toast.makeText(context, "can not find file", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newList;
    }

    public static Thread thread;

    public static void tranList() {
        if(thread!=null&&thread.isAlive()&&!thread.isInterrupted()){
            return;
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < LaunchActivity.wordList.size(); i++) {
                    if(thread.isInterrupted()){
                        break;
                    }
                    String index = LaunchActivity.wordList.get(i);
                    if ((!LaunchActivity.dictMap.containsKey(index))||
                            LaunchActivity.dictMap.get(index)==null||
                            LaunchActivity.dictMap.get(index).equals("")) {
                        Util.getTranslationAndSaveMap(index);
                        updataMenu(LaunchActivity.wordList.size(),i+1);
                    }
                    Log.d("luojianjin",""+i);
                }
                updataMenu(LaunchActivity.wordList.size(),LaunchActivity.wordList.size());
            }
        });
        thread.start();
    }

    public static void stopThread(){
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }



    public static void updataMenu(final int all,final int index){
        LaunchActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("luojianjin2","updataMenu");
                Log.d("luojianjin2","updataMenu"+(LaunchActivity.menu!=null));
                if(LaunchActivity.menu!=null){

                    if(all!=index){
                        LaunchActivity.menu.findItem(R.id.menu_download).setVisible(true);
                        LaunchActivity.menu.findItem(R.id.menu_download).setTitle("正在下载("+index+"/"+all+")");
                    }else{
                        LaunchActivity.menu.findItem(R.id.menu_download).setTitle("下载完成");
                        LaunchActivity.menu.findItem(R.id.menu_download).setVisible(false);
                    }

                }
            }
        });
    }


    public static void getTranslationAndSaveMap(final String word) {
        Log.d("luojianjin","find word : "+word);
        try{
            String alldata = api.getTransResult(word, "auto", "zh");
            int start = alldata.indexOf("\"dst\":\"") + "\"dst\":\"".length();
            int end = alldata.indexOf("\"}]}");
            String cutData=alldata.substring(start,end);
            String realData=decode(cutData);
            LaunchActivity.dictMap.put(word, realData);
            Util.setDictMap(LaunchActivity.dictMap);
            Log.d("luojianjin","put word to map: "+word+":"+realData);
        }catch (Exception e){
            Log.d("luojianjin","error data : "+word);
            e.printStackTrace();
        }
    }
    public static String decode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }

    public static void setDictMap(Map datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        String strJson = gson.toJson(datalist);
        editor.putString("dict", strJson);
        editor.commit();
    }

    public static HashMap<String, String> getDictMap() {
        String strJson = preferences.getString("dict", null);
        if (null == strJson) {
            return new HashMap<String, String>();
        }
        HashMap<String, String> dict = gson.fromJson(strJson, new TypeToken<HashMap<String, String>>() {
        }.getType());
        return dict;
    }

    public static void setHardList(ArrayList<String> datalist) {
        if (null == datalist)
            return;
        String strJson = gson.toJson(datalist);
        editor.putString("hard", strJson);
        editor.commit();
    }

    public static void clearHard() {
        setHardList(new ArrayList<String>());
    }

    public static ArrayList<String> getHardList() {
        String strJson = preferences.getString("hard", null);
        if (null == strJson) {
            return new ArrayList<String>();
        }
        ArrayList<String> hard = gson.fromJson(strJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        return hard;
    }

    public static void saveHard(String word){
        ArrayList<String> list=getHardList();
        for(int i=0;i<list.size();i++){
            if(list.get(i).equals(word)){
                return;
            }
        }
        list.add(word);
        setHardList(list);
    }
    public static void removeHard(String word){
        ArrayList<String> list=getHardList();
        for(int i=0;i<list.size();i++){
            if(list.get(i).equals(word)){
                list.remove(i);
            }
        }
        setHardList(list);
    }

    public static void replaceHard(String oldword,String newword){
        ArrayList<String> list=getHardList();
        for(int i=0;i<list.size();i++){
            if(list.get(i).equals(oldword)){
                list.set(i,newword);
            }
        }
        setHardList(list);
    }

    public static void openWeb(String word){
        //prevent double open
        App.webShowWord=word;
        Intent intent=new Intent(LaunchActivity.context,WebActivity.class);
        intent.putExtra("word",word);
        LaunchActivity.context.startActivity(intent);
    }

    public static void setBanList(ArrayList<String> datalist) {
        if (null == datalist)
            return;
        String strJson = gson.toJson(datalist);
        editor.putString("ban", strJson);
        editor.commit();
    }

    public static ArrayList<String> getBanList() {
        String strJson = preferences.getString("ban", null);
        if (null == strJson) {
            return new ArrayList<String>();
        }
        ArrayList<String> ban = gson.fromJson(strJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        return ban;
    }

    public static void addBan(String word) {
        if (word.equals(""))
            return;
        ArrayList<String> banList =getBanList();
        for(int i=0;i<banList.size();i++){
            if(banList.get(i).equals(word)){//已经存在了
                return;
            }
        }
        banList.add(word);
        setBanList(banList);
    }


    public static ArrayList<String> filterBan(ArrayList<String> oList){
        ArrayList<String> ban =getBanList();
        for(int i=0;i<oList.size();i++){
            for(int t=0;t<ban.size();t++){
                if(ban.get(t).equals(oList.get(i))){
                    oList.remove(i);
                    i--;
                    break;
                }
            }
        }
        return oList;
    }

    public static void changeWord(String oldWord,String newWord){
        if (newWord.equals("") || oldWord.equals("")) {
            return;
        }

        try {
            String all=readToString(filePath);
            all=all.replace(oldWord,newWord);

            FileOutputStream fos = new FileOutputStream(filePath);
            PrintWriter pw = new PrintWriter(fos);
            pw.write(all);
            pw.flush();
            pw.close();

            //hardpage
            replaceHard(oldWord,newWord);
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new EventBean(EventBean.TYPE_UPDATE_WORD_LAUNCH));
    }
    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

}
