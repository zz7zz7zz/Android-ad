package com.module.ad.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class AdDialog extends Dialog implements LifecycleObserver {

    public AdDialog(@NonNull Context context) {
        super(context);
        attach(context);
    }

    public AdDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        attach(context);
    }

    public AdDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        attach(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //------------------------------------------------------------------------
    public void attach(Context mContext){
        if(mContext instanceof FragmentActivity){
            ((FragmentActivity)(mContext)).getLifecycle().addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){
        if(isShowing()){
            dismiss();
        }
    }

    //------------------------------------------------------------------------
    public interface IAdDialogListener{

        void onShow(ViewGroup ... adViewParent);

    }

    //------------------------------------------------------------------------
    public static void show(Activity activity, IAdDialogListener listener){
        if(null == activity){
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(activity);
        final AdDialog dialog = new AdDialog(activity, R.style.BottomDialog);
        View contentView = inflater.inflate(R.layout.ad_dialog, null);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        };
        contentView.findViewById(R.id.ad_close).setOnClickListener(clickListener);

        dialog.addContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(contentView);

        Window attWindow = dialog.getWindow();
        attWindow.setGravity(Gravity.BOTTOM);
        attWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();

        if(null != listener){
            listener.onShow((ViewGroup) contentView.findViewById(R.id.ad_parent_1),(ViewGroup) contentView.findViewById(R.id.ad_parent_2));
        }
    }

}
