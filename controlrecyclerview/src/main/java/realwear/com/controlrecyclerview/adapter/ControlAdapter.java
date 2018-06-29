package realwear.com.controlrecyclerview.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import realwear.com.controlrecyclerview.ControlRecyclerView;
import realwear.com.controlrecyclerview.R;
import realwear.com.controlrecyclerview.model.ControlModel;
import realwear.com.controlrecyclerview.viewholder.ControlViewHolder;

public abstract class ControlAdapter<VH extends ControlViewHolder> extends RecyclerView.Adapter<VH> implements IVoiceAdapter  {
    public List<ControlModel> mControls = new ArrayList<>();
    private ControlRecyclerView mRecylerView;

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.control_layout, parent, false);
        return onCreateControlsViewHolder(itemView, viewType);
    }

    protected abstract VH onCreateControlsViewHolder(View rootlayout, int position);

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, final int position) {
        holder.getView().setTag(position);
        holder.getView().setClickable(true);
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.isSelected()){
                    holder.onCloseItem();
                }else {
                    holder.onOpenItem();
                    if(mRecylerView != null)
                        mRecylerView.gotoPosition(position);
                }
            }
        });

        holder.updateModel(mControls.get(position));
    }

    @Override
    public int getItemCount() {
        return mControls.size();
    }

    @Override
    public String getVoiceCommand(int index) {
        if(mControls != null)
            return mControls.get(index).getTitle();
        return "";
    }

    public void updateModels(List<ControlModel> controls) {
        mControls = controls;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        if(recyclerView instanceof ControlRecyclerView)
         mRecylerView = (ControlRecyclerView)recyclerView;
    }
}
