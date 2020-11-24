package com.yunchen.piggybank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunchen.piggybank.database.converter.AmountConverter;
import com.yunchen.piggybank.database.converter.DateConverter;
import com.yunchen.piggybank.database.entity.Plan;

import java.text.ParseException;
import java.util.List;

public class PlanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final private PlanListAdapter.ItemClickListener mItemClickListener;

    private Context mContext;

    private List<Plan> mPlans;

    PlanListAdapter(Context context, PlanListAdapter.ItemClickListener listener){
        mContext = context;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if(viewType == 1) {
            itemView = LayoutInflater.from(mContext).
                    inflate(R.layout.recyclerview_plan_in_progress, parent, false);
            PlanInProgressViewHolder viewHolder = new PlanInProgressViewHolder(itemView);
            return viewHolder;
        }else if(viewType ==0){
            itemView = LayoutInflater.from(mContext).
                    inflate(R.layout.recyclerview_plans_finished, parent, false);
            PlanFinishedViewHolder viewHolder = new PlanFinishedViewHolder(itemView);
            return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(mPlans !=null){
            Plan current = mPlans.get(position);
            if(holder instanceof PlanInProgressViewHolder){
                try {
                    ((PlanInProgressViewHolder) holder).planAmountGoalTv
                            .setText(AmountConverter.toString(current.getGoalAmount()));
                    ((PlanInProgressViewHolder) holder).planDueDateTv
                            .setText(DateConverter.longToStr(current.getEndDate()));
                    ((PlanInProgressViewHolder) holder).planStateTv
                            .setText(((current.getGoalAmount()-current.getGoalRemain())*100.0/current.getGoalAmount())+"%");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(holder instanceof PlanFinishedViewHolder){
                try {
                    ((PlanFinishedViewHolder)holder).planAmountGoalTv
                            .setText(AmountConverter.toString(current.getGoalAmount()));
                    ((PlanFinishedViewHolder)holder).planPunishmentTv
                            .setText(current.getPunishment());
                    ((PlanFinishedViewHolder) holder).planTimeTv
                            .setText(DateConverter.longToStr(current.getStartDate())+" - "
                            +DateConverter.longToStr(current.getEndDate()));
                    if(current.getCurrentState()==Plan.FAILED){
                    ((PlanFinishedViewHolder) holder).planStateTv.setText(R.string.label_plan_failed);
                    }else{
                        ((PlanFinishedViewHolder) holder).planStateTv.setText(R.string.label_plan_completed);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void setmPlans(List<Plan> mPlans){
        this.mPlans = mPlans;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mPlans !=null){
            return mPlans.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Plan plan = mPlans.get(position);
        if(plan.getCurrentState()==Plan.IN_PROGRESS){
            return 1;
        }
        else{
            return 0;
        }
    }

    public interface ItemClickListener{
        void onItemClickListener(int itemId);
    }

    class PlanFinishedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView planAmountGoalTv;
        private TextView planPunishmentTv;
        private TextView planTimeTv;
        private TextView planStateTv;

        PlanFinishedViewHolder(@NonNull View itemView) {
            super(itemView);
            this.planAmountGoalTv = itemView.findViewById(R.id.plan_finished_goal_amount);
            this.planPunishmentTv = itemView.findViewById(R.id.plan_finished_punishment);
            this.planTimeTv = itemView.findViewById(R.id.plan_finished_time);
            this.planStateTv = itemView.findViewById(R.id.plan_finished_state);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mPlans.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

    class PlanInProgressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView planAmountGoalTv;
        private TextView planDueDateTv;
        private TextView planStateTv;

        PlanInProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            this.planAmountGoalTv = itemView.findViewById(R.id.plan_in_progress_amount_goal);
            this.planDueDateTv = itemView.findViewById(R.id.plan_in_progress_due_date);
            this.planStateTv = itemView.findViewById(R.id.plan_in_progress_state_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mPlans.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }


}
