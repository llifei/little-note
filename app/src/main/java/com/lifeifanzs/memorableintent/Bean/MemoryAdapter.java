package com.lifeifanzs.memorableintent.Bean;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.lifeifanzs.memorableintent.Activity.ActivityFactory.sMainActivity;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryHolder> {

    private List<Memory> mMemories;//新建Memory列表
    private List<MemoryHolder>mHolders;
    private List<Integer> mDePolist;
    private boolean mIsDeleteMenu;
    private boolean mIsSelectAll;
    private Map<Integer,Boolean> mIsSelectMap;

    private OnDeleteClickListener mOnDeleteClickListener;
    private OnZhidingClickListener mOnZhidingClickListener;
    private OnSolvedClickListener mOnSolvedClickListener;

    /**
     * 带参构造方法
     *
     * @param memories
     */
    public MemoryAdapter(List<Memory> memories, Map<Integer,Boolean> isSelectMap,
                         boolean isDeleteMenu, boolean isSelectAll) {
        mMemories = memories;//为Memory列表赋值
        mIsDeleteMenu = isDeleteMenu;
        mIsSelectAll = isSelectAll;
        mIsSelectMap=isSelectMap;
        mHolders=new ArrayList<>();
        if(mIsSelectAll){
            for(int i=0;i<mMemories.size();i++)
                mIsSelectMap.put(i,true);
        }
    }

    public void setMemories(List<Memory>memories){
        this.mMemories=memories;
    }

    /**
     * 创建Holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MemoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(sMainActivity);
        //通过LayoutInflater创建MemoryHolder

        return new MemoryHolder(layoutInflater, parent);

    }


    /**
     * 每需要显示使用一个新的Holder时，就调用一次这个方法来调用Holder的bind方法来显示Memory
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final MemoryHolder holder, final int position) {
        final Memory memory = mMemories.get(position);
        holder.bind(memory, mIsDeleteMenu);
        mHolders.add(holder);
        if(mIsDeleteMenu) {
            setEditListener(holder,position);//设置编辑删除监听
        }
        setSlideListener(holder,position);//侧滑菜单监听
        if(!mIsDeleteMenu&&!memory.isNote())
            holder.mSolvedCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnSolvedClickListener.Click(position);
                }
            });

    }

    /**
     * 侧滑删除监听
     */
    private void setSlideListener(MemoryHolder holder, final int position) {
        //删除
        holder.mDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnDeleteClickListener.onClick(position);
            }
        });

        //置顶
        holder.mZhidingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnZhidingClickListener.onClick(position);
            }
        });
    }

    //设置选择删除监听
    public void setEditListener(final MemoryHolder holder, final int position){
        mDePolist = getDePolist();
        if (mIsSelectMap.get(position) != null && mIsSelectMap.get(position)){
            holder.mIsDeleteCheckBox.setChecked(true);
            getDePolist().add(position);
        }

        //设置itemView监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mIsDeleteCheckBox.setChecked(!holder.mIsDeleteCheckBox.isChecked());
            }
        });

        //设置选择删除checkbox监听
        holder.mIsDeleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setDeleClickListener(isChecked, position);
            }
        });
    }

    private void setDeleClickListener(boolean isChecked, int position) {
        mDePolist = getDePolist();
        if (isChecked
                && mDePolist.indexOf(position) == -1) {
            mDePolist.add(position);

            mIsSelectMap.put(position,true);

            if(mDePolist.size()==mMemories.size()) {
                mIsSelectAll = true;
                sMainActivity.setIsSelectAll(mIsSelectAll);
                sMainActivity.updateSelectAllMenuItem();
            }
            Log.d("---", "add position");
        } else if (!isChecked
                && mDePolist.indexOf(position) != -1) {

            mIsSelectMap.remove(position);
            System.out.println(mIsSelectMap);

            mIsSelectAll=false;
            sMainActivity.setIsSelectAll(mIsSelectAll);
            sMainActivity.updateSelectAllMenuItem();
            mDePolist.remove(mDePolist.indexOf(position));
        }

    }

    public List<Integer> getDePolist() {
        if (mDePolist == null) return new ArrayList<>();
        return mDePolist;
    }

    public List<MemoryHolder>getHolders(){
        return mHolders;
    }

    @Override
    public int getItemCount() {
        return mMemories.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //删除监听器接口
    public interface OnDeleteClickListener {
        void onClick(int position);
    }

    //设置删除监听器
    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener){
        mOnDeleteClickListener=onDeleteClickListener;
    }

    //置顶监听器接口
    public interface OnZhidingClickListener{
        void onClick(int position);
    }

    //设置置顶监听器
    public void setOnZhidingClickListener(OnZhidingClickListener onZhidingClickListener){
        mOnZhidingClickListener=onZhidingClickListener;
    }

    public interface OnSolvedClickListener{
        void Click(int position);
    }

    public void setOnSolvedClickListener(OnSolvedClickListener onSolvedClickListener){
        mOnSolvedClickListener=onSolvedClickListener;
    }
}
