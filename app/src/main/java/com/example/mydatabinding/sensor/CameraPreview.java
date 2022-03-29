package com.example.mydatabinding.sensor;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final SurfaceHolder mHolder;
    private android.hardware.Camera mCamera;
    private Camera.Parameters mParameters;

    public CameraPreview(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        //surface第一次创建时回调
        //打开相机
        mCamera = Camera.open();
        try {
            mParameters = mCamera.getParameters();
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//设置对焦模式
            mCamera.setParameters(mParameters);

            mCamera.setDisplayOrientation(90);//旋转90°，改变默认方向
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //surface变化的时候回调(格式/大小)
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        //surface销毁的时候回调
        mHolder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}
