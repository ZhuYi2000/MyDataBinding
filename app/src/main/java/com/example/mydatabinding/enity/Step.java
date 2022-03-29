package com.example.mydatabinding.enity;

import androidx.annotation.NonNull;

//步数查询的记录行
public class Step {
    private int mId; // 记录在sqlite的id
    private long mBeginTime; // 计步开始时间
    private long mEndTime; // 计步结束时间
    private int mMode; // 计步模式: 0:不支持模式, 1:静止, 2:走路, 3:跑步, 11:骑车, 12:交通工具
    private int mSteps; // 总步数

    public Step(int mId, long mBeginTime, long mEndTime, int mMode, int mSteps) {
        this.mId = mId;
        this.mBeginTime = mBeginTime;
        this.mEndTime = mEndTime;
        this.mMode = mMode;
        this.mSteps = mSteps;
    }

    @NonNull
    @Override
    public String toString() {
        return "Step{" +
                "mId=" + mId +
                ", mBeginTime=" + mBeginTime +
                ", mEndTime=" + mEndTime +
                ", mMode=" + mMode +
                ", mSteps=" + mSteps +
                '}';
    }
}
