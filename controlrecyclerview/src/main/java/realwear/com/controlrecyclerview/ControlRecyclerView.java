package realwear.com.controlrecyclerview;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import realwear.com.controlrecyclerview.adapter.ControlAdapter;
import realwear.com.controlrecyclerview.adapter.IVoiceAdapter;
import realwear.com.controlrecyclerview.headtracker.HFHeadtrackerListener;
import realwear.com.controlrecyclerview.headtracker.HFHeadtrackerManager;
import realwear.com.controlrecyclerview.model.ControlModel;
import realwear.com.controlrecyclerview.view.ControlViewMargin;
import realwear.com.controlrecyclerview.view.ViewSelector;
import realwear.com.controlrecyclerview.viewholder.ControlViewHolder;

public class ControlRecyclerView extends RecyclerView implements HFHeadtrackerListener {
    private HFHeadtrackerManager mHeadTracker;
    private static final String NO_SCROLL = "hf_scroll_none";

    private static final String ACTION_SPEECH_EVENT = "com.realwear.wearhf.intent.action.SPEECH_EVENT";
    private static final String EXTRA_INDEX = "com.realwear.wearhf.intent.extra.INDEX";
    private boolean mSelectedView;
    private int mCurrentIndex;
    private Matrix mMatrix = new Matrix();
    private Camera mCamera = new Camera();

    /**
     * Paint object to draw with
     */
    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private ViewSelector mViewSelector;
    private ControlViewHolder mActiveHolder;

    public ControlRecyclerView(Context context) {
        super(context);
        init();
    }

    public ControlRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ControlRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        addItemDecoration(new ControlViewMargin());
        if (adapter instanceof ControlAdapter) {

        }

        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
            private boolean isFirst;

            @Override
            public void onChanged() {
                super.onChanged();
                reloadCommands();

                if (isFirst) {
                    centerView();
                    isFirst = false;
                }
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                reloadCommands();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                reloadCommands();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                reloadCommands();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                reloadCommands();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                reloadCommands();
            }
        });

        onResume();
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);

        centerView();
    }

    private void init() {
        mHeadTracker = new HFHeadtrackerManager(this);
        addOnScrollListener(ScrollListener);
        setContentDescription(NO_SCROLL);
        setChildrenDrawingOrderEnabled(true);
    }

    private Bitmap getChildDrawingCache(final View child) {
        Bitmap bitmap = child.getDrawingCache();
        if (bitmap == null) {
            child.setDrawingCacheEnabled(true);
            child.buildDrawingCache();
            bitmap = child.getDrawingCache();
        }
        return bitmap;
    }

    @Override
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {

        int index = getLayoutManager().getPosition(child);
        ControlViewHolder holder = ((ControlViewHolder) findViewHolderForAdapterPosition(index));

        View view = findCenterView();
        if (view != null) {
            if (child.equals(view)) {
                holder.setFocus(true);
                ControlModel model = holder.getModel();
                mViewSelector.setTitle(model.getTitle());
                mViewSelector.setSubTitle(model.getState());
                child.setScaleX(1f);
                child.setScaleY(1f);

            } else {
                holder.setFocus(false);
                child.setScaleX(0.95f);
                child.setScaleY(0.95f);
            }
        }

        return super.drawChild(canvas, child, drawingTime);
    }




       /* if(mSelectedView && child.equals(findCenterView()))
            return super.drawChild(canvas, child, drawingTime);

        int index = getLayoutManager().getPosition(child);
        ControlViewHolder holder = ((ControlViewHolder) findViewHolderForAdapterPosition(index));



        // (top,left) is the pixel position of the child inside the list
        final int top = child.getTop();
        final int left = child.getLeft();
        // center point of child
        final int childCenterY = child.getHeight() / 2;
        final int childCenterX = child.getWidth() / 2;
        //center of list
        final int parentCenterY = getHeight() / 2;
        final int parentCenterX = getWidth() / 2;
        //center point of child relative to list
        final int absChildCenterY = child.getTop() + childCenterY;
        final int absChildCenterX = child.getLeft() + childCenterX;
        //distance of child center to the list center
        final int distanceY = parentCenterY - absChildCenterY;

        final int distanceX = parentCenterX - absChildCenterX;

        float alpha = Math.max(1, distanceX * 10);

        Bitmap bitmap = getChildDrawingCache(child);


        if(view.equals(child)) {

            prepareCentreMatrix(mMatrix, 0, getWidth() / 2);

           // if(mSelectedView)

        }else{
            prepareMatrix(mMatrix, 100, getWidth() / 2);
        }

        mMatrix.preTranslate(-childCenterX, -childCenterY);
        mMatrix.postTranslate(childCenterX, childCenterY);
        mMatrix.postTranslate(left, top);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        return false;
    }*/

    private void prepareMatrix(final Matrix outMatrix, int distance, int r) {
        //clip the distance
        final int d = Math.min(r, Math.abs(distance));
        //use circle formula
        final float translateZ = (float) Math.sqrt((r * r) - (d * d));
        mCamera.save();
        float offset = r - translateZ;
        if(offset > 100 ){
            offset = 100;
        }
        mCamera.translate(0, 0, offset);

        mCamera.getMatrix(outMatrix);
        mCamera.restore();
    }

    private void prepareCentreMatrix(final Matrix outMatrix, int distance, int r) {
        //clip the distance
        mCamera.save();
        mCamera.translate(0, 0, distance);

        mCamera.getMatrix(outMatrix);
        mCamera.restore();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        LayoutManager manager = getLayoutManager();
        View view = findCenterView();
        int centerChild = manager.getPosition(view) ;
        centerChild = childCount / 2;

        int rez = i;
        //find drawIndex by centerChild
        if (i > centerChild) {
            //below center
            rez = (childCount - 1) - i + centerChild;
        } else if (i == centerChild) {
            //center row
            //draw it last
            rez = childCount - 1;
        } else {
            //above center - draw as always
            // i < centerChild
            rez = i;
        }
        return rez;

    }

    private void centerView(){
        final LayoutManager manager = getLayoutManager();
        View view = findCenterView();

        if(view != null) {
            int position = manager.getPosition(view);
            gotoPosition(position);

            final ControlViewHolder holder = (ControlViewHolder) findViewHolderForAdapterPosition(mCurrentIndex);

            if(mViewSelector != null){
                ControlModel model = holder.getModel();
                mViewSelector.setTitle(model.getTitle());
                mViewSelector.setSubTitle(model.getState());
            }

            holder.setViewHolderListener(new ControlViewHolder.iViewHolder() {
                @Override
                public void onViewHolderChanged() {
                    centerView();

                    mSelectedView = holder.isSelected();
                    mActiveHolder = holder;

                    if(mSelectedView) {
                        mViewSelector.setVisibility(View.GONE);
                        clearCommands();
                    }
                    else{
                        mViewSelector.setVisibility(View.VISIBLE);
                        setCommands();
                    }
                }
            });
        }
    }

    public void gotoPosition(int index) {
        LayoutManager manager = getLayoutManager();
        // Center the item to the middle of the screen.
        mCurrentIndex = index;

        View view = findCenterView();
        manager.smoothScrollToPosition(this, null, mCurrentIndex);
    }

    public void snapToPosition(int index){
        LayoutManager manager = getLayoutManager();
        // Center the item to the middle of the screen.
        mCurrentIndex = index;

        manager.scrollToPosition(mCurrentIndex);
    }

    private View findCenterView() {
        LayoutManager manager = getLayoutManager();
        OrientationHelper helper = OrientationHelper.createHorizontalHelper(manager);
        int childCount = getLayoutManager().getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        final int center = getWidth() / 2;
       /* if (manager.getClipToPadding()) {
            center = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else {
            center = helper.getEnd() / 2;
        }*/
        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = manager.getChildAt(i);
            int childCenter = helper.getDecoratedStart(child)
                    + (helper.getDecoratedMeasurement(child) / 2);
            int absDistance = Math.abs(childCenter - center);

            /** if child center is closer than previous closest, set it as closest  **/
            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
        return closestChild;
    }


    public void setViewSelector(ViewSelector viewHolder){
        mViewSelector = viewHolder;
    }

    public void reloadCommands() {
        if(!mSelectedView)
            setCommands();
        else
            clearCommands();
    }

    private void setCommands() {
        StringBuilder builder = new StringBuilder();
        builder.append("hf_override:");
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof IVoiceAdapter) {
            IVoiceAdapter voiceAdapter = (IVoiceAdapter) adapter;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                String voiceCommand = voiceAdapter.getVoiceCommand(i);

                // The # indicates that the command has an associated 'Select Item <n>' command.
                builder.append('#');
                builder.append(voiceCommand);

                builder.append("|");
            }
        }
        
        setContentDescription(builder.toString());

        getContext().sendBroadcast(new Intent("com.realwear.wearhf.intent.action.REFRESH_UI"));
    }

    public void clearCommands(){
        setContentDescription("");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                getContext().sendBroadcast(new Intent("com.realwear.wearhf.intent.action.REFRESH_UI"));
            }
        }).start();

    }
    
    public void onResume(){
        if (mHeadTracker != null)
            mHeadTracker.startHeadtracker(getContext(), 0);

        getContext().registerReceiver(asrBroadcastReceiver, new IntentFilter(ACTION_SPEECH_EVENT));
    }
    
    public void onPause(){
        if (mHeadTracker != null) mHeadTracker.stopHeadtracker();

        if(getContext() != null && asrBroadcastReceiver != null)
            getContext().unregisterReceiver(asrBroadcastReceiver);
    }

    @Override
    public void onHeadMoved(float deltaX, float deltaY, float deltaZ) {

    }

    @Override
    public void onOrientationChanged(float pitch, float roll, float v) {
        if(mSelectedView){
            return;
        }

        scrollBy((int)v, 0);
    }

    @Override
    public boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        return false;
    }

    @Override
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        super.sendAccessibilityEventUnchecked(event);
    }

    /**
     * Receives broadcasts for ASR.
     */
    private BroadcastReceiver asrBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(ACTION_SPEECH_EVENT)) {
                return;
            }

            final String asrCommand = intent.getStringExtra("command").trim();
            RecyclerView.Adapter adapter = getAdapter();

            if (adapter instanceof IVoiceAdapter) {
                final IVoiceAdapter iVoiceAdapter = (IVoiceAdapter) adapter;

                int index = intent.getIntExtra(EXTRA_INDEX, -1);
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    final String voiceCommand = iVoiceAdapter.getVoiceCommand(i).trim();

                    if (asrCommand.equalsIgnoreCase(voiceCommand)) {
                        snapToPosition(i);


                        ViewHolder view = findViewHolderForAdapterPosition(i);

                        if (view instanceof ControlViewHolder) {
                            ((ControlViewHolder) view).getView().callOnClick();
                        }
                        return;
                    }

                }
            }

        }
    };
    

    public OnScrollListener ScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if(newState == SCROLL_STATE_IDLE){
                centerView();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);


        }
    };

    public boolean hasSelectedItem() {
        return mSelectedView;
    }

    public void closeItem() {
        mActiveHolder.onCloseItem();
    }
}
