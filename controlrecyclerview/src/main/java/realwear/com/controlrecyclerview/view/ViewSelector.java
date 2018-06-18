package realwear.com.controlrecyclerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import realwear.com.controlrecyclerview.R;

public class ViewSelector extends RelativeLayout {
    private TextView mTitle;
    private TextView mSubTtitle;

    public ViewSelector(Context context) {
        super(context);
        initView(context);
    }

    public ViewSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ViewSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.active_voice_command, this, true);

        mTitle = (TextView)findViewById(R.id.icon_text);
        mSubTtitle = (TextView)findViewById(R.id.subTile);
    }

    public void setTitle(String title){
        mTitle.setText(title);
    }

    public void setSubTitle(String subTitle){
        mSubTtitle.setText(subTitle);
    }



}
