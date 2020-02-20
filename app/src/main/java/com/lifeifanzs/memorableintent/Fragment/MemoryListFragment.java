package com.lifeifanzs.memorableintent.Fragment;

import android.annotation.SuppressLint;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.lifeifanzs.memorableintent.Bean.Memory;
import com.lifeifanzs.memorableintent.Bean.MemoryAdapter;
import com.lifeifanzs.memorableintent.Bean.SlideRecyclerView;
import com.lifeifanzs.memorableintent.SQLite.MemoryLab;
import com.lifeifanzs.memorableintent.Bean.Theme;
import com.lifeifanzs.memorableintent.SQLite.ThemeLab;
import com.lifeifanzs.memorableintent.R;
import com.lifeifanzs.memorableintent.Utils.CalendarUtils;
import com.lifeifanzs.memorableintent.Utils.PlaySoundUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lifeifanzs.memorableintent.Activity.ActivityFactory.sMainActivity;
import static com.lifeifanzs.memorableintent.Utils.TimeUtils.setMemoryTime;
import static com.lifeifanzs.memorableintent.Utils.TimeUtils.setToday;

public class MemoryListFragment extends Fragment {

    private static final String ARG_ISNOTE="isnote";

    private boolean mIsNote;

    private SlideRecyclerView mMemoryRecyclerView;
    private MemoryAdapter mAdapter;
    //新建RecyclerView与Adapter对象

    private List<Memory> mMemories;
    private MemoryLab mMemoryLab;

    private Map<Integer, Boolean> mIsSelectMap;
    private Map<Memory, Integer> mMemoryMap;

    private TextView mNoOneTextView;
    private ImageView mNoOneImgeView;
    //实例化视图组件

    private int mposition;
    private Date mToday;

    private String mThemeColor;
    private ThemeLab mThemeLab;
    private Theme mTheme;

    private boolean mIsDeleteMenu;
    private boolean mIsSelectAll;
    private boolean mIsDeleteState;

    private SoundPool soundPool;
    private int soundID;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlaySoundUtils.initSound(getActivity());//加载音效

        this.mIsNote= (boolean) getArguments().getSerializable(ARG_ISNOTE);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memory_list, container, false);
        mMemoryRecyclerView = v.findViewById(R.id.memory_recycler_view);//将此对象与recycler布局关联
        mMemoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//设置LayoutManager

        mNoOneTextView = v.findViewById(R.id.memory_noOne);
        mNoOneImgeView = v.findViewById(R.id.noOne_imgView);
        mNoOneTextView.setText(mIsNote ? getResources().getString(R.string.note_noOne) :
                getResources().getString(R.string.memory_noOne));
        mNoOneImgeView.setImageDrawable(getResources().getDrawable(mIsNote ? R.drawable.noone_note
                : R.drawable.noone_memory));

        mIsSelectMap = new HashMap<>();
        updateUI();//更新界面

        return v;
    }


    /**
     * 重写onResume方法更新列表
     */
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * 设置toolbar颜色
     */
    public void setToolBarColor() {
        mThemeLab = ThemeLab.getThemeLab(getActivity());
        mTheme = mThemeLab.getTheme();
        mThemeColor = mTheme.getColor();
    }

    /**
     * 更新UI状态（选择删除或正常状态）
     *
     * @param isDeleteMenu
     * @param isSelectAll
     */
    public void updateUIState(boolean isDeleteMenu, boolean isSelectAll) {
        this.mIsDeleteMenu = isDeleteMenu;
        this.mIsSelectAll = isSelectAll;
        updateUI();
    }


    /**
     * 更新界面
     */
    private void updateUI() {
        mMemoryLab = MemoryLab.get(getActivity());//创建单例MemoryLab对象
        mMemories = mIsNote ? mMemoryLab.getNotes() : mMemoryLab.getMemories();//得到Memory列表
        mNoOneTextView.setVisibility(mMemories.size() == 0 ? View.VISIBLE : View.GONE);//设置数据为空时 界面的提示视图
        mNoOneImgeView.setVisibility(mMemories.size() == 0 ? View.VISIBLE : View.GONE);
        sortMemories(mMemories,null);
        if (mIsDeleteMenu) mIsDeleteState = true;
        if (mAdapter == null || mIsDeleteMenu || (!mIsDeleteMenu && mIsDeleteState)) {
            initAdapter();
            if (!mIsDeleteMenu && mIsDeleteState) mIsDeleteState = false;
        } else {
            mAdapter.setMemories(mMemories);
            mAdapter.notifyDataSetChanged();
        }


    }

    /**
     * 初始化Adapter
     */
    private void initAdapter() {
        mAdapter = new MemoryAdapter(mMemories, mIsSelectMap, mIsDeleteMenu, mIsSelectAll);//用此列表初始化mAdapter

        //设置solved监听
        mAdapter.setOnSolvedClickListener(new MemoryAdapter.OnSolvedClickListener() {
            @Override
            public void Click(int position) {
                int newPos = -1;
                Memory memory = mMemories.get(position);
                memory.setSolved(!memory.isSolved());
                if(memory.isSolved()) {
                    PlaySoundUtils.playSound();
//                    sortMemories(mMemories,null);
//                    newPos=mMemoryMap.get(memory);
                    if(memory.isRemind())   deleteCalendarRemind(memory);//删除日历提醒
                }else{

                    if(memory.isRemind())   addCalendarRemind(memory);
                }
                newPos=sortMemories(mMemories,memory);
                if(newPos!=-1)
                    mAdapter.notifyItemMoved(position, newPos);
                mAdapter.notifyItemChanged(mMemoryMap.get(memory));
                mAdapter.notifyItemRangeChanged(0, mMemories.size());
                MemoryLab.get(getActivity()).updateMemory(memory);
            }
        });

        //设置侧滑删除监听
        mAdapter.setOnDeleteClickListener(new MemoryAdapter.OnDeleteClickListener() {
            @Override
            public void onClick(int position) {
                MemoryLab.get(getActivity()).deleteMemory(mMemories.get(position));
                deleteCalendarRemind(mMemories.get(position));
                mMemories.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(0, mMemories.size());
//                mAdapter.notifyDataSetChanged();
                mMemoryRecyclerView.closeMenu();
                if (mMemories.size() == 0) {
                    mNoOneTextView.setVisibility(View.VISIBLE);
                    mNoOneImgeView.setVisibility(View.VISIBLE);
                }
            }
        });

        //设置侧滑置顶监听
        mAdapter.setOnZhidingClickListener(new MemoryAdapter.OnZhidingClickListener() {
            @Override
            public void onClick(int position) {
                Memory temp = mMemories.get(position);
                temp.setZhiding(!temp.isZhiding());
                temp.setZhidingdate(temp.isZhiding() ? new Date() : null);
                MemoryLab.get(getActivity()).updateMemory(temp);
                if (temp.isZhiding()) {
                    for (int i = position - 1; i > -1; i--) {
                        mMemories.set(i + 1, mMemories.get(i));
                    }
                    mMemories.set(0, temp);
                    mAdapter.notifyItemMoved(position, 0);
                    mAdapter.notifyItemChanged(0);
                    mAdapter.notifyItemRangeChanged(0, mMemories.size());
                } else {
                    sortMemories(mMemories,null);
                    mAdapter.notifyItemMoved(position, mMemoryMap.get(temp));
                    mAdapter.notifyItemChanged(mMemoryMap.get(temp));
                    mAdapter.notifyItemRangeChanged(0, mMemories.size());
//                    mAdapter.notifyDataSetChanged();
                }
//                mAdapter.notifyDataSetChanged();
                mMemoryRecyclerView.closeMenu();
            }
        });

        mMemoryRecyclerView.setAdapter(mAdapter);//将mMemoryRecyclerView与mAdapter关联
    }


    public Map<Integer, Boolean> getIsSelectMap() {
        return mIsSelectMap;
    }

    /**
     * 删除Memory
     */
    public void deleteItem() {
        List<Integer> mDePolist = mAdapter.getDePolist();
        for (int i = 0; i < mDePolist.size(); i++) {
            int deposition = mDePolist.get(i);
            deleteCalendarRemind(mMemories.get(deposition));
            mMemoryLab.deleteMemory(mMemories.get(deposition));
            mMemories.set(deposition, null);
            mIsSelectMap.remove(deposition);
        }
        for (int i = 0; i < mMemories.size(); i++) {
            if (mMemories.get(i) == null) {
                mMemories.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    /**
     * 删除日历提醒事件
     *
     * @param memory
     */
    private void deleteCalendarRemind(Memory memory) {
        if (memory.isRemind()) {
            CalendarUtils.deleteCalendarEventRemind(getActivity(), memory.getTitle()
                    , memory.getDetail(), memory.getDate().getTime(), null);
        }
    }

    private void addCalendarRemind(Memory memory) {
        CalendarUtils.addCalendarEventRemind(getActivity(),memory.getTitle()
        ,memory.getDetail(),memory.getDate().getTime(),memory.getDate().getTime()+60000,5,null);
    }



    /**
     * 给Memory按时间排序
     *
     * @param memories
     */
    private int sortMemories(List<Memory> memories,Memory m) {
        mMemoryMap = new HashMap<>();
        List<Memory> zhidingMemories = new ArrayList<>();//置顶的(包括未/已完成)
        List<Memory> beforeMemories = new ArrayList<>();//过期的(且未完成)
        List<Memory> futureMemories = new ArrayList<>();//未来的
        List<Memory> solvedMemories = new ArrayList<>();//已完成的(不论过期和未来的)
        for (Memory memory : memories) {
            setToday(new Date());
            String memorytime = setMemoryTime(memory);
            if (!memory.isZhiding()) {
                if (memory.isSolved()) {
                    solvedMemories.add(memory);
                } else if (memory.isOver())
                    beforeMemories.add(memory);
                else
                    futureMemories.add(memory);
            } else {
                zhidingMemories.add(memory);
            }
        }
        sortByTime(solvedMemories);
        sortByTime(zhidingMemories);
        sortByTime(beforeMemories);
        sortByTime(futureMemories);

        memories.clear();
        memories.addAll(zhidingMemories);
        memories.addAll(futureMemories);
        memories.addAll(beforeMemories);
        memories.addAll(solvedMemories);

        for (int i = 0; i < memories.size(); i++) {
            mMemoryMap.put(memories.get(i), i);
            if(m==memories.get(i))
                return i;
        }

        return -1;
    }


    /**
     * 排序
     *
     * @param memories
     * @return
     */
    private void sortByTime(List<Memory> memories) {
        for (int i = 1; i < memories.size(); i++) {
            for (int j = 0; j < memories.size() - i; j++) {
                Memory M1 = memories.get(j);
                Memory M2 = memories.get(j + 1);
                Calendar c1 = Calendar.getInstance();
                c1.setTime(M1.getDate());
                Calendar c2 = Calendar.getInstance();
                c2.setTime(M2.getDate());
                int y1 = c1.get(Calendar.YEAR);
                int y2 = c2.get(Calendar.YEAR);
                int m1 = c1.get(Calendar.MONTH);
                int m2 = c2.get(Calendar.MONTH);
                int d1 = c1.get(Calendar.DAY_OF_MONTH);
                int d2 = c2.get(Calendar.DAY_OF_MONTH);
                long t1 = M1.getDate().getTime();
                long t2 = M2.getDate().getTime();

                if (!M1.isZhiding()) {//如果不是置顶的
                    if (!mIsNote) {//如果不是便签
                        if (y1 > y2 || (y1 == y2 && m1 > m2) || (y1 == y2 && m1 == m2 && d1 > d2)
                                || (y1 == y2 && m1 == m2 && d1 == d2 && t1 > t2)) {
                            swapMemory(memories, j);
                        }
                    } else {
                        if (M1.getDate().getTime() < M2.getDate().getTime()) {
                            swapMemory(memories, j);
                        }
                    }
                } else {
                    if (M1.getZhidingdate().getTime() < M2.getZhidingdate().getTime()) {
                        swapMemory(memories, j);
                    }
                }
            }
        }
    }

    private void swapMemory(List<Memory> memories, int j) {
        Memory temp = memories.get(j);
        memories.set(j, memories.get(j + 1));
        memories.set(j + 1, temp);
    }


}
