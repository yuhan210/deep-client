package edu.mit.csail.deepthing;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by Tiffany Chen on 5/20/15.
 */

public class Preview extends ViewGroup implements SurfaceHolder.Callback {

    private final String TAG = "Preview";
    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Camera.Size mPreviewSize;
    List<Camera.Size> mSupportedPreviewSizes;

    private int mDisplayRotation;
    private int mDisplayOrientation;

    Camera mCamera;
    Context mCtx;

    public Preview(Context context, SurfaceView sv) {
        super(context);
        mCtx = context;
        mSurfaceView = sv;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void setCamera(Camera camera) {
        mCamera = camera;

        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();

            // get Camera parameters
            Camera.Parameters params = mCamera.getParameters();

            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                // set the focus mode
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                // set Camera parameters
                mCamera.setParameters(params);
            }

        }
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            System.out.println("supported" + size.width + "," + size.height);
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return(result);
    }


    private void configureCamera(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        // Set the PreviewSize and AutoFocus:
        setOptimalPreviewSize(parameters, width, height);

        // And set the parameters:
        mCamera.setParameters(parameters);
    }

    private void setOptimalPreviewSize(Camera.Parameters cameraParameters, int width, int height) {
        List<Camera.Size> previewSizes = cameraParameters.getSupportedPreviewSizes();
        float targetRatio = (float) width / height;
        Camera.Size previewSize = Utility.getOptimalPreviewSize((Activity) mCtx, previewSizes, targetRatio);
        cameraParameters.setPreviewSize(previewSize.width, previewSize.height);
    }

    private void setDisplayOrientation() {
        // Now set the display orientation:
        mDisplayRotation = Utility.getDisplayRotation((Activity) mCtx);
        mDisplayOrientation = Utility.getDisplayOrientation(mDisplayRotation, 0);

        mCamera.setDisplayOrientation(mDisplayOrientation);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // Try to stop the current preview:
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // Ignore...
        }


        // start preview with new settings
        if(mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);

            } catch (IOException exception) {
                Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
            }
            Camera.Parameters parameters = mCamera.getParameters();
            configureCamera(width, height);
            setDisplayOrientation();

            mCamera.startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

}

