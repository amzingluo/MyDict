package luo.mydict;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebActivity extends AppCompatActivity {


    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Intent intent=getIntent();
        String word=intent.getStringExtra("word").trim();
        this.word=word;
        //获得控件
        WebView webView = (WebView) findViewById(R.id.wv_webview);
        //访问网页
        webView.loadUrl("http://m.youdao.com/dict?le=eng&q="+word);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportZoom(false);//不支持缩放

        //webSettings.setLoadsImagesAutomatically(false); //自动加载图片
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//优先使用缓存

        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        App.word=this.word;
    }

}
