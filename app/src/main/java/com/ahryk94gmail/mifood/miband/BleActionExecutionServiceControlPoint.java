package com.ahryk94gmail.mifood.miband;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ahryk94gmail.mifood.Debug;

import java.util.LinkedList;
import java.util.List;

public class BleActionExecutionServiceControlPoint implements IBleActionExecutionServiceControlPoint {

    private static final boolean DBG = true;

    private Context mContext;
    private ServiceConnection mServiceConnection;
    private boolean mIsServiceConnected = false;
    private IBleActionExecutionServiceControlPoint mControlPoint;
    private List<IBleAction> mBuffer;

    public BleActionExecutionServiceControlPoint(Context context) {
        this.mContext = context;
        this.mBuffer = new LinkedList<>();

        this.mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Debug.WriteLog(DBG, "BleActionExecutionServiceControlPoint: onServiceConnected");
                mIsServiceConnected = true;
                mControlPoint = (IBleActionExecutionServiceControlPoint) service;

                if (mBuffer.size() != 0) {
                    addToQueue(mBuffer);
                    mBuffer.clear();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Debug.WriteLog(DBG, "BleActionExecutionServiceControlPoint: onServiceDisconnected");
                mIsServiceConnected = false;
            }
        };
    }

    public boolean isServiceConnected() {
        return this.mIsServiceConnected;
    }

    public boolean bind() {
        Intent intent = new Intent(this.mContext, BleActionExecutionService.class);
        return this.mContext.bindService(intent, this.mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind() {
        //TODO catch IllegalArgumentException
        this.mContext.unbindService(this.mServiceConnection);
    }

    @Override
    public boolean addToQueue(IBleAction bleAction) {
        if (!isServiceConnected()) {
            this.mBuffer.add(bleAction);
            return false;
        }

        return this.mControlPoint.addToQueue(bleAction);
    }

    public boolean addToQueue(List<IBleAction> bleActions) {
        if (!isServiceConnected()) {
            this.mBuffer.addAll(bleActions);
            return false;
        }

        return this.mControlPoint.addToQueue(bleActions);
    }
}
