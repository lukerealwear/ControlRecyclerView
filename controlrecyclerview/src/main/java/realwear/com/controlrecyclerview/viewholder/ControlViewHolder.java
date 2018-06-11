package realwear.com.controlrecyclerview.viewholder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import realwear.com.controlrecyclerview.R;
import realwear.com.controlrecyclerview.model.ControlModel;

public abstract class ControlViewHolder extends RecyclerView.ViewHolder {
    private final View mControlLayout;
    private final ViewGroup mMainView;
    private final TextView mVoiceCommandView;
    private final LinearLayout mControlHolder;
    private int mPercent;
    private ControlModel mModel;
    private boolean mSelected;
    private ValueAnimator mIncreaseAnimator;
    private iViewHolder mListener;

    public ValueAnimator getIncreaseAnimator(){
        return mIncreaseAnimator;
    }

    public ControlViewHolder(View itemView) {
        super(itemView);

        mMainView = (ViewGroup)itemView;

        mControlLayout = LayoutInflater.from(itemView.getContext()).inflate(getLayoutId(), null);

        mControlHolder = (LinearLayout)mMainView.findViewById(R.id.controlHolder);

        mVoiceCommandView = (TextView)itemView.findViewById(R.id.icon_text);

        onViewCreated(mControlLayout);

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
    }

    private void updateState(){

    }

    private void updateIcon(){

    }

    public boolean isSelected(){
        return mSelected;
    }

    public void onOpenItem(){

        mSelected = true;

        if(mPercent == 0)
            mPercent = (50 * mMainView.getWidth()) / 100;

        mIncreaseAnimator = ValueAnimator.ofInt(mMainView.getWidth(), mMainView.getWidth() + mPercent);

        mIncreaseAnimator.setDuration(500);
        mIncreaseAnimator.setInterpolator(new AccelerateInterpolator());
        mIncreaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mMainView.getLayoutParams().width = value.intValue();
                mMainView.getLayoutParams().height = value.intValue();
                mMainView.setMinimumHeight(value.intValue());
              // mMainView.setX( mMainView.getX() -( mMainView.getLayoutParams().width - value.intValue()));
                mMainView.requestLayout();

                if(mListener != null){
                    mListener.onViewHolderChanged();
                }
            }

        });
        mIncreaseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mControlHolder.addView(mControlLayout);
            }
        });
        mIncreaseAnimator.start();
    }

    public void setViewHolderListener(iViewHolder viewHolderListener){
        mListener = viewHolderListener;
    }

    public void onCloseItem(){
        mControlHolder.removeView(mControlLayout);
        mSelected = false;

        mIncreaseAnimator = ValueAnimator.ofInt(mMainView.getWidth(), mMainView.getWidth() - mPercent);

        mIncreaseAnimator.setDuration(500);
        mIncreaseAnimator.setInterpolator(new AccelerateInterpolator());
        mIncreaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mMainView.getLayoutParams().width = value.intValue();
                mMainView.getLayoutParams().height = value.intValue();
                mMainView.setMinimumHeight(value.intValue());
                // mMainView.setX( mMainView.getX() -( mMainView.getLayoutParams().width - value.intValue()));
                mMainView.requestLayout();

                if(mListener != null){
                    mListener.onViewHolderChanged();
                }
            }

        });
        mIncreaseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mControlHolder.removeView(mControlLayout);
            }
        });
        mIncreaseAnimator.start();
    }

    public interface iViewHolder{
        void onViewHolderChanged();
    }
}
