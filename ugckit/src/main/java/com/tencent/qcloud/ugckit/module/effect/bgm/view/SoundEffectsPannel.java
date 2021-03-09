package com.tencent.qcloud.ugckit.module.effect.bgm.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.google.android.material.tabs.TabLayout;
import com.tencent.qcloud.ugckit.R;
import com.tencent.qcloud.ugckit.module.record.interfaces.ISoundEffectsPannel;
import com.tencent.ugc.TXRecordCommon;

import java.util.ArrayList;
import java.util.List;

/**
 * 音效Pannel
 */
public class SoundEffectsPannel extends FrameLayout implements ISoundEffectsPannel, SeekBar.OnSeekBarChangeListener {

    private TabLayout mTabLayout;
    private RecyclerView rvChangeVoice;
    private RecyclerView rvReverbVoice;


    private SoundEffectsSettingPannelListener mSoundEffectsSettingPannelListener;
    public SoundEffectsPannel(Context context) {
        super(context);
        init(context);
    }

    public SoundEffectsPannel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SoundEffectsPannel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ugckit_layout_sound_effects, this);

        mTabLayout = (TabLayout) findViewById(R.id.beauty_tab);
        mTabLayout.addTab(mTabLayout.newTab().setText("变声"));
        mTabLayout.addTab(mTabLayout.newTab().setText("混响"));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position=tab.getPosition();
                if(position==0){
                    rvChangeVoice.setVisibility(View.VISIBLE);
                    rvReverbVoice.setVisibility(View.GONE);
                }else if(position==1){
                    rvChangeVoice.setVisibility(View.GONE);
                    rvReverbVoice.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        rvChangeVoice = (RecyclerView) findViewById(R.id.rvChangeVoice);
        rvReverbVoice = (RecyclerView) findViewById(R.id.rvReverbVoice);

        rvChangeVoice.setAdapter(new ItemAdapter(changeVoiceItems(),true));
        rvReverbVoice.setAdapter(new ItemAdapter(reverbVoiceItems(),false));


    }

    private List<Item> changeVoiceItems(){
        List<Item> list=new ArrayList<>();
        list.add(new Item("原声",R.drawable.ugckit_ic_effect_non,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_0));
        list.add(new Item("熊孩子",R.drawable.ugckit_ic_voicechange_badboy,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_1));
        list.add(new Item("萝莉",R.drawable.ugckit_ic_voicechange_loli,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_2));
        list.add(new Item("大叔",R.drawable.ugckit_ic_voicechange_uncle,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_3));
        list.add(new Item("重金属",R.drawable.ugckit_ic_voicechange_heavymechanical,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_4));
        list.add(new Item("外国人",R.drawable.ugckit_ic_voicechange_foreigner,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_6));
        list.add(new Item("困兽",R.drawable.ugckit_ic_voicechange_beast,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_7));
        list.add(new Item("死肥仔",R.drawable.ugckit_ic_voicechange_deadfathouse,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_8));
        list.add(new Item("强电流",R.drawable.ugckit_ic_voicechange_electric,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_9));
        list.add(new Item("重机械",R.drawable.ugckit_ic_voicechange_heavymechanical,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_10));
        list.add(new Item("空灵",R.drawable.ugckit_ic_voicechange_ethereal,TXRecordCommon.VIDOE_VOICECHANGER_TYPE_11));
        return list;
    }

    private List<Item> reverbVoiceItems(){
        List<Item> list=new ArrayList<>();
        list.add(new Item("原声",R.drawable.ugckit_ic_effect_non,TXRecordCommon.VIDOE_REVERB_TYPE_0));
        list.add(new Item("KTV",R.drawable.ugckit_ic_reverb_ktv,TXRecordCommon.VIDOE_REVERB_TYPE_1));
        list.add(new Item("房间",R.drawable.ugckit_ic_reverb_room,TXRecordCommon.VIDOE_REVERB_TYPE_2));
        list.add(new Item("会堂",R.drawable.ugckit_ic_reverb_hall,TXRecordCommon.VIDOE_REVERB_TYPE_3));
        list.add(new Item("低沉",R.drawable.ugckit_ic_reverb_low,TXRecordCommon.VIDOE_REVERB_TYPE_4));
        list.add(new Item("洪亮",R.drawable.ugckit_ic_reverb_sonorous,TXRecordCommon.VIDOE_REVERB_TYPE_5));
        list.add(new Item("磁性",R.drawable.ugckit_ic_reverb_magnetic,TXRecordCommon.VIDOE_REVERB_TYPE_6));
        return list;
    }

    @Override
    public void onProgressChanged(@NonNull SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void setSoundEffectsSettingPannelListener(SoundEffectsSettingPannelListener soundEffectsSettingPannelListener) {
        mSoundEffectsSettingPannelListener = soundEffectsSettingPannelListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }

    @Override
    public void setSeekbarColor(int color) {

    }

    @Override
    public void setCheckedTextColor(int color) {

    }

    @Override
    public void setNormalTextColor(int color) {

    }

    @Override
    public void setConfirmButtonBackgroundColor(int color) {

    }

    @Override
    public void setConfirmButtonTextColor(int color) {

    }

    @Override
    public void setConfirmButtonTextSize(int size) {

    }


    private void onClickVoiceChanger(int value){
        if (mSoundEffectsSettingPannelListener != null) {
            mSoundEffectsSettingPannelListener.onClickVoiceChanger(value);
        }
    }


    private void onClickReverb(int value){
        if (mSoundEffectsSettingPannelListener != null) {
            mSoundEffectsSettingPannelListener.onClickReverb(value);
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder>{

        private List<Item> items;

        private int selectedPosition;

        private boolean changeVoice;

        public ItemAdapter(List<Item> items,boolean changeVoice){
            this.items=items;
            this.changeVoice=changeVoice;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.beauty_view_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {
            final Item item = items.get(position);
            holder.title.setText(item.name);
            holder.icon.setImageResource(item.icon);
            boolean selected=position==this.selectedPosition;
            if(selected){
                holder.mask.setVisibility(View.VISIBLE);
                holder.title.setTextColor(Color.parseColor("#3d71ef"));
            }else{
                holder.mask.setVisibility(View.GONE);
                holder.title.setTextColor(Color.WHITE);
            }
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectedPosition==position){
                        return;
                    }
                    selectedPosition=position;
                    notifyDataSetChanged();
                    if(changeVoice){
                        onClickVoiceChanger(item.value);
                    }else{
                        onClickReverb(item.value);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private class Item{
        public String name;
        public int icon;
        public int value;

        public Item(String name, int icon, int value) {
            this.name = name;
            this.icon = icon;
            this.value = value;
        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder{

        private ImageView icon;
        private ImageView mask;
        private TextView title;

        public ItemHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(com.tencent.liteav.demo.beauty.R.id.beauty_iv_icon);
            mask = (ImageView) itemView.findViewById(com.tencent.liteav.demo.beauty.R.id.beauty_iv_mask);
            title = (TextView) itemView.findViewById(com.tencent.liteav.demo.beauty.R.id.beauty_tv_title);
        }
    }

}
