package com.trm.myexercise.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.trm.myexercise.R;
import com.trm.myexercise.utils.FragmentNavigationRequest;

public class ActivityItemAdapter extends ListAdapter<FragmentNavigationRequest, ActivityItemAdapter.ActivityViewHold> {
    private static final String TAG = "ActivityItemAdapter_TAG";
    private OnItemClick onItemClick;

    public ActivityItemAdapter(@NonNull DiffUtil.ItemCallback<FragmentNavigationRequest> diffCallback) {
        super(diffCallback);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ActivityViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        ActivityViewHold viewHold = new ActivityViewHold(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.activity_item, parent, false));
        viewHold.setOnItemClick(onItemClick);
        return viewHold;
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHold holder, int position) {
        Log.d(TAG, "onBindViewHolder position: " + position);
        if (!(holder.itemView instanceof TextView)) {
            throw new IllegalArgumentException("bind view not TextView");
        }

        holder.request = getItem(position);

        String className = getItem(position).getFragment().getClass().getName();
        ((TextView) holder.itemView).setText(className.substring(className.lastIndexOf(".") + 1));
    }

    static class ActivityViewHold extends RecyclerView.ViewHolder {
        public FragmentNavigationRequest request;
        private OnItemClick onItemClick;

        public void setOnItemClick(OnItemClick onItemClick) {
            this.onItemClick = onItemClick;
        }

        public ActivityViewHold(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if (onItemClick != null) {
                    onItemClick.onClick(request);
                }
            });
        }
    }

    public interface OnItemClick {
        void onClick(FragmentNavigationRequest request);
    }
}
