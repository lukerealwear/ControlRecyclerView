package realwear.com.controlrecyclerview.view;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Luke on 26/01/2017.
 */

public class ControlViewMargin extends RecyclerView.ItemDecoration {

    public ControlViewMargin() {

    }


    /**
     * Set different margins for the items inside the recyclerView: no top margin for the first row
     * and no left margin for the first column.
     */
    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildLayoutPosition(view);
        int last =   parent.getAdapter().getItemCount();

        outRect.left = 8;
        outRect.right = 8;

        if(position == 0) {
            outRect.left = 400;
        }

        if(last == position + 1) {
            outRect.right = 400;
        }
    }
}