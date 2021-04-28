package com.droid.ouse.widgets;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.droid.ouse.R;

public class KeyView extends RelativeLayout {
    public KeyView(Context context) {
        super(context);
        init(context);
    }

    public KeyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KeyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public KeyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private TextView tvKey;
    private TextView tvValue;

    void init(Context context) {
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.layout_key, this);
        // 获取控件
        tvKey = findViewById(R.id.tv_key);
        tvValue = findViewById(R.id.tv_value);
    }

    KeyView setKey(String key) {
        tvKey.setText(key);
        return this;
    }

    KeyView setValue(String value) {
        tvValue.setText(value);
        return this;
    }
}
