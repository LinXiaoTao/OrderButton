package com.android.leo.orderbutton;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action2;

/**
 * leo linxiaotao1993@vip.qq.com
 * Created on 16-11-2 上午9:42
 */

public class MagicButtonActivity extends AppCompatActivity {


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private List<MagicEntity> mMagicData;
    private MagicAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic);
        ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMagicData == null) {
            mMagicData = new ArrayList<>();
            Random random = new Random(System.currentTimeMillis());
            for (int i = 0; i < 30; i++) {
                MagicEntity entity = new MagicEntity();
                entity.mMaxNum = random.nextInt(100);
                entity.mName = String.format("商品%d", i);
                mMagicData.add(entity);
            }
        }

        if (mAdapter == null) {
            mAdapter = new MagicAdapter();
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // private method
    //
    ///////////////////////////////////////////////////////////////////////////

    private class MagicAdapter extends RecyclerView.Adapter<MagicHolder> {
        private LayoutInflater mInflater = getLayoutInflater();

        @Override
        public MagicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MagicHolder(mInflater.inflate(R.layout.item_magic, parent, false));
        }

        @Override
        public void onBindViewHolder(final MagicHolder holder, int position) {
            if (mMagicData != null && mMagicData.size() > position) {
                MagicEntity entity = mMagicData.get(position);
                holder.mButton.setMaxNum(entity.mMaxNum);
                holder.mButton.setNum(entity.mNum);
                holder.mButton.setType(entity.mType);
                holder.mButton.setTag(R.string.tag_data, entity);
                holder.mTextView.setText(entity.mIsSelect ? entity.toString() : "");
                holder.mButton.setCallback(new Action2<Integer, Integer>() {
                    @Override
                    public void call(Integer integer, Integer integer2) {
                        if (holder.mButton.getTag(R.string.tag_data) != null) {
                            MagicEntity data = (MagicEntity) holder.mButton.getTag(R.string.tag_data);
                            data.mType = integer;
                            data.mNum = integer2;
                            if (integer == OrderButton.STEP_BTN_EXPAND || integer == OrderButton.STEP_TEXT_CHANGE)
                                data.mIsSelect = true;
                            else
                                data.mIsSelect = false;
                            holder.mTextView.setText(data.mIsSelect ? data.toString() : "");
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mMagicData == null ? 0 : mMagicData.size();
        }
    }

    private class MagicHolder extends RecyclerView.ViewHolder {
        private OrderButton mButton;
        private TextView mTextView;

        public MagicHolder(View itemView) {
            super(itemView);
            mButton = (OrderButton) itemView.findViewById(R.id.btnMargic);
            mTextView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    private class MagicEntity {
        boolean mIsSelect;
        String mName;
        int mMaxNum;
        int mNum;
        int mType;

        public MagicEntity() {
            mType = OrderButton.STEP_NORMAL;
            mNum = 0;
            mIsSelect = false;
        }

        @Override
        public String toString() {
            return String.format("名称%s,已选为%d,最大为%d,状态%d"
                    , mName, mNum, mMaxNum, mType);
        }
    }
}
