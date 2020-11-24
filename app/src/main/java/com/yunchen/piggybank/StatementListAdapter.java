package com.yunchen.piggybank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunchen.piggybank.database.converter.AmountConverter;
import com.yunchen.piggybank.database.converter.DateConverter;
import com.yunchen.piggybank.database.entity.Statement;

import java.text.ParseException;
import java.util.List;

import static android.view.View.GONE;

public class StatementListAdapter extends RecyclerView.Adapter<StatementListAdapter.StatementViewHolder> {


    final private ItemClickListener mItemClickListener;

    private Context mContext;

    private List<Statement> mStatements;

    StatementListAdapter(Context context, ItemClickListener listener){
        mContext = context;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public StatementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).
                inflate(R.layout.recyclerview_statements, parent, false);
        return new StatementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StatementViewHolder holder, int position) {
        if(mStatements !=null){
            try {
                Statement current = mStatements.get(position);
                holder.secondaryCategoryTv.setText(current.getSecondaryCategory());
                if(current.getMemo()==null || current.getMemo().length()==0){
                    holder.memoTv.setVisibility(GONE);
                }else {
                    holder.memoTv.setText(current.getMemo());
                }
                holder.dateTv.setText(DateConverter.longToStr(current.getDate()));
                holder.amountTv.setText(AmountConverter.toString(current.getAmount()));
                if(current.getPrimaryCategory().equals("Saving")) {
                    holder.typeIcon.setImageResource(R.drawable.svg_ic_saving);
                }else if(current.getPrimaryCategory().equals("Budget")){
                    holder.typeIcon.setImageResource(R.drawable.svg_ic_budget);
                }else{
                    holder.typeIcon.setImageResource(R.drawable.svg_ic_expense);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    void setmStatements(List<Statement> mStatements){
        this.mStatements = mStatements;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mStatements !=null){
            return mStatements.size();
        }
        return 0;
    }


    public interface ItemClickListener{
        void onItemClickListener(int itemId);
    }

    class StatementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView typeIcon;
        private TextView secondaryCategoryTv;
        private TextView memoTv;
        private TextView dateTv;
        private TextView amountTv;

        StatementViewHolder(@NonNull View itemView) {
            super(itemView);
            this.secondaryCategoryTv = itemView.findViewById(R.id.statement_secondary_category_tv);
            this.typeIcon = itemView.findViewById(R.id.statement_type_icon);
            this.memoTv = itemView.findViewById(R.id.statement_memo_tv);
            this.amountTv = itemView.findViewById(R.id.statement_amount_tv);
            this.dateTv = itemView.findViewById(R.id.statement_date_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mStatements.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

}