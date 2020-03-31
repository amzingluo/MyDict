package luo.mydict.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import luo.mydict.App;
import luo.mydict.EventBean;
import luo.mydict.HardActivity;
import luo.mydict.LaunchActivity;
import luo.mydict.Util;

public class RecycleItemTouchHelper extends ItemTouchHelper.Callback {

    final float SwipeOver = (float) 0.90;

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    //拉到多少的时候，才算
    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return SwipeOver;
    }

    //滑动难易
    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        Log.d("luojianjin","getSwipeEscapeVelocity:"+defaultValue);
        return super.getSwipeEscapeVelocity(defaultValue) *100;
    }

    //滑动难易
    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        Log.d("luojianjin","getSwipeVelocityThreshold:"+defaultValue);
        return super.getSwipeVelocityThreshold(defaultValue);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        Log.d("luojianjin", "onSwiped:" + direction);

        dialog(((WordAdapter.MyViewHolder) viewHolder).word.getText().toString());
    }


    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        float howFar = dX / getScreenWidth();
        if (howFar > 0.8) {
            ((WordAdapter.MyViewHolder) viewHolder).delect.setVisibility(View.VISIBLE);
            ((WordAdapter.MyViewHolder) viewHolder).word.setVisibility(View.INVISIBLE);
            ((WordAdapter.MyViewHolder) viewHolder).delect.getBackground().setAlpha((int) (255 * howFar));
        } else {
            ((WordAdapter.MyViewHolder) viewHolder).word.setVisibility(View.VISIBLE);
            ((WordAdapter.MyViewHolder) viewHolder).delect.setVisibility(View.INVISIBLE);
        }
        Log.d("luojianjin", "howFar:" + howFar);
    }


    protected void dialog(final String word) {

        Context context;
        if (App.isHardPage) {
            context = HardActivity.activity;
        } else {
            context = LaunchActivity.activity;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确认删除吗？");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Util.addBan(word);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EventBus.getDefault().post(new EventBean(EventBean.TYPE_UPDATE_WORD_LAUNCH));
            }
        });
        builder.create().show();
    }

    private int getScreenWidth() {
        DisplayMetrics metrics = App.Application.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

}
