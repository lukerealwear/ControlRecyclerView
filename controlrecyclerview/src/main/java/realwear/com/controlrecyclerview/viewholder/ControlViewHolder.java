package realwear.com.controlrecyclerview.viewholder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import realwear.com.controlrecyclerview.R;
import realwear.com.controlrecyclerview.model.ControlModel;

public abstract class ControlViewHolder extends RecyclerView.ViewHolder {
    private final View mControlLayout;
    private final ViewGroup mMainView;
    private final TextView mVoiceCommandView;
    private final LinearLayout mControlHolder;
    private final LinearLayout mWidget;
    private final LinearLayout mWidgetSelected;
    private final LinearLayout mWidgetTitle;
    private final TextView mIconTxt;
    private final TextView mStateTxt;
    private final ImageView mIcon;
    private int mPercent;
    private ControlModel mModel;
    private boolean mSelected;
    private ValueAnimator mIncreaseAnimator;
    private iViewHolder mListener;
    private int mPercentHeight;
    private ValueAnimator mIncreaseHeightAnimator;

    public ValueAnimator getIncreaseAnimator(){
        return mIncreaseAnimator;
    }

    public ControlViewHolder(View itemView) {
        super(itemView);

        mMainView = (ViewGroup)itemView;

        mControlLayout = LayoutInflater.from(itemView.getContext()).inflate(getLayoutId(), null);

        mWidget = (LinearLayout)mMainView.findViewById(R.id.widget);
        mWidgetSelected = (LinearLayout)mMainView.findViewById(R.id.widgetSelected);
        mControlHolder = (LinearLayout)mMainView.findViewById(R.id.controlHolder);
        mWidgetTitle = (LinearLayout)mMainView.findViewById(R.id.widgetTitle);
        mVoiceCommandView = (TextView)itemView.findViewById(R.id.icon_text);
        mIconTxt = (TextView)mMainView.findViewById(R.id.icon_text2);
        mStateTxt = (TextView)mMainView.findViewById(R.id.stateTxt);
        mIcon = (ImageView)mMainView.findViewById(R.id.icon_image);

        onViewCreated(mControlLayout);

    }

    public ImageView getIconView(){
        return mIcon;
    }

    public TextView getTitle(){
        return mIconTxt;
    }

    public TextView getState(){
        return mStateTxt;
    }

    public abstract int getLayoutId();

    public abstract void onViewCreated(View view);

    public View getView(){
        return mMainView;
    }

    public void updateModel(ControlModel model){
        mModel = model;
        updateTitle();
        updateState();
        updateIcon();
    }

    private void updateTitle(){
        mVoiceCommandView.setText(mModel.getTitle());
        mIconTxt.setText(mModel.getTitle());
    }

    private void updateState(){
        mStateTxt.setText(mModel.getState());
    }

    private void updateIcon(){
        mIcon.setImageResource(mModel.getIcon());
    }

    public boolean isSelected(){
        return mSelected;
    }

    public void onOpenItem(){

        if(mSelected)
            return;

        mSelected = true;

        if(mPercent == 0) {
            mPercent = (100 * mWidget.getWidth()) / 100;
            mPercentHeight = (50 * mWidget.getWidth()) / 100;
        }

        mIncreaseHeightAnimator = ValueAnimator.ofInt(mWidget.getHeight(), mWidget.getHeight() + mPercentHeight);

        mIncreaseHeightAnimator.setDuration(500);
        mIncreaseHeightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mIncreaseHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mWidget.getLayoutParams().height = value.intValue();
                //mWidget.getLayoutParams().height = value.intValue();
                //mWidget.setMinimumHeight(value.intValue());

                mWidget.requestLayout();

                if(mListener != null){
                    mListener.onViewHolderChanged();
                }
            }

        });

        mIncreaseHeightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mWidgetSelected.setVisibility(View.VISIBLE);
                mWidgetSelected.startAnimation(AnimationUtils.loadAnimation(mMainView.getContext(), R.anim.fadein));
                mControlHolder.addView(mControlLayout);
                mControlLayout.startAnimation(AnimationUtils.loadAnimation(mMainView.getContext(), R.anim.fadein));

            }
        });

        mIncreaseHeightAnimator.start();


        mIncreaseAnimator = ValueAnimator.ofInt(mWidget.getWidth(), mWidget.getWidth() + mPercent);

        mIncreaseAnimator.setDuration(500);
        mIncreaseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mIncreaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mWidget.getLayoutParams().width = value.intValue();
                //mWidget.getLayoutParams().height = value.intValue();
                //mWidget.setMinimumHeight(value.intValue());

                mWidget.requestLayout();

                if(mListener != null){
                    mListener.onViewHolderChanged();
                }
            }

        });
        mIncreaseAnimator.start();


    }

    public void setViewHolderListener(iViewHolder viewHolderListener){
        mListener = viewHolderListener;
    }

    public void onCloseItem(){
        if(!mSelected)
            return;

       mWidgetSelected.setVisibility(View.GONE);

        mSelected = false;

        mIncreaseAnimator = ValueAnimator.ofInt(mWidget.getWidth(), mWidget.getWidth() - mPercent);

        mIncreaseAnimator.setDuration(500);
        mIncreaseAnimator.setInterpolator(new AccelerateInterpolator());
        mIncreaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mWidget.getLayoutParams().width = value.intValue();
                mWidget.getLayoutParams().height = value.intValue();
                mWidget.setMinimumHeight(value.intValue());

                mWidget.requestLayout();

                if(mListener != null){
                    mListener.onViewHolderChanged();
                }
            }

        });
        mIncreaseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);


            }
        });
        mIncreaseAnimator.start();
        mControlHolder.removeView(mControlLayout);
        //mControlLayout.startAnimation(AnimationUtils.loadAnimation(mMainView.getContext(), R.anim.fadeout));
    }

    public ControlModel getModel() {
        return mModel;
    }

    public void setFocus(boolean focus) {
       if(focus){
           mVoiceCommandView.setVisibility(View.GONE);
       }else{
           mVoiceCommandView.setVisibility(View.VISIBLE);
       }
    }

    public interface iViewHolder{
        void onViewHolderChanged();
    }
}
