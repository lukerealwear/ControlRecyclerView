package realwear.com.controlrecyclerview.headtracker;
/*------------------- COPYRIGHT AND TRADEMARK INFORMATION -------------------+
  |
  |    RealWear Development Software, Source Code and Object Code
  |    (c) 2015, 2016 RealWear, Inc. All rights reserved.
  |
  |    Contact info@realwear.com for further information about the use of
  |    this code.
  |
  +--------------------------------------------------------------------------*/


/*----------------------- SOURCE MODULE INFORMATION -------------------------+
 |
 | Source Name:  Headtracker Manager
 |
 | Handles the headtracking
 |
 | Version: v2.0
 | Date: January 2016
 | Author: Chris Parkinson
 |
  +--------------------------------------------------------------------------*/

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

public class HFHeadtrackerManager implements SensorEventListener {
    private static final String TAG = "HeadtrackerManager";
    private final HFHeadtrackerListener mListener;
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    private static final int SENSOR_DELAY_MICROS = 16 * 1000; // 16ms

    private int mLastAccuracy;
    private WindowManager mWindowManager;
    private float mStartZ;
    private float mZ;

    public HFHeadtrackerManager(HFHeadtrackerListener eventListener) {
        mListener = eventListener;
    }

    public void startHeadtracker(Context context, int trackingMode) {
        if (mListener == null) return; //No one listening, so don't start

        mSensorManager = (SensorManager)context.getSystemService(Activity.SENSOR_SERVICE);

        // Can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mWindowManager =  ((Activity)context).getWindowManager();
        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY_MICROS);
        Log.d(TAG, "Registering Headtracker");
    }

    public void stopHeadtracker() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            Log.d(TAG, "Unregistering Headtracker");
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (mListener == null) {
            return;
        }
        if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }
        if (event.sensor == mRotationSensor) {
            updateOrientation(event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (mLastAccuracy != accuracy) {
            mLastAccuracy = accuracy;
        }
    }

    private void updateOrientation(float[] rotationVector) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);

        final int worldAxisForDeviceAxisX;
        final int worldAxisForDeviceAxisY;
        final int worldAxisForDeviceAxisZ;

        // Remap the axes as if the device screen was the instrument panel,
        // and adjust the rotation matrix for the device orientation.
        switch (mWindowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
            default:
                worldAxisForDeviceAxisX = SensorManager.AXIS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_Z;
                worldAxisForDeviceAxisZ = SensorManager.AXIS_Y;
                break;
            case Surface.ROTATION_90:
                worldAxisForDeviceAxisX = SensorManager.AXIS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
                worldAxisForDeviceAxisZ = SensorManager.AXIS_Y;
                break;
            case Surface.ROTATION_180:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z;
                worldAxisForDeviceAxisZ = SensorManager.AXIS_MINUS_Y;
                break;
            case Surface.ROTATION_270:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_X;
                worldAxisForDeviceAxisZ = SensorManager.AXIS_Y;
                break;
        }

        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
                worldAxisForDeviceAxisY, adjustedRotationMatrix);

        // Transform rotation matrix into azimuth/pitch/roll
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        // Convert radians to degrees
        float pitch = orientation[1] * -57;
        float roll = orientation[2] * -57;
        float azimut2 = orientation[0];

        float z = (float) Math.toDegrees(orientation[0]);  //

        mZ = (z - mStartZ) * 30;
        mStartZ = z;
        if(mZ > -6 && mZ < 6)
            return;

        mListener.onOrientationChanged(pitch, roll, mZ);
    }

    public void onReset(){
        mStartZ = 0;
    }
}
