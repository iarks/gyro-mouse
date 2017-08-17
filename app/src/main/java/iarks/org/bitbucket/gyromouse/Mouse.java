package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.concurrent.BlockingQueue;


class Trackpad implements Runnable
{
    private Context context;
    private SensorManager mSensorManager = null;
    private Sensor sensor;
    private SensorEventListener mListener;
    private HandlerThread mHandlerThread;
    private String deltas=null;

    private final BlockingQueue<String> sharedQueue;

    private final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private static final float EPSILON = 0.000000001f;


    private DecimalFormat df = new DecimalFormat("0.00");

    Trackpad(BlockingQueue<String> bq, Context context)
    {
        this.sharedQueue = bq;
        this.context = context;
    }

    @Override
    public void run()
    {
//        Log.e("Thread Name", Thread.currentThread().getName()+ "THIS THREAD RIGHT HERE!");
        Looper.prepare();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mHandlerThread = new HandlerThread("AccelerometerLogListener");
        mHandlerThread.start();
        Handler handler = new Handler(mHandlerThread.getLooper());

        synchronized (sharedQueue)
        {
            try
            {
                sharedQueue.put("{\"X\":" + "\"" + "EOT" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
                deltas=null;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }


        mListener = new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent event)
            {
//                Log.e("Trackpad", "THIS THREAD RIGHT HERE!");

                float[] deltaOrientation = new float[9];
                deltaOrientation[0] = 1;
                deltaOrientation[4] = 1;
                deltaOrientation[8] = 1;

                double x, y, z;

                if (timestamp != 0) {
                    final float dT = (event.timestamp - timestamp) * NS2S;

                    // Axis of the rotation sample, not normalized yet.
                    float axisX = event.values[0];
                    float axisY = event.values[1];
                    float axisZ = event.values[2];

                    // Calculate the angular speed of the sample
                    float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                    // Normalize the rotation vector if it's big enough to get the axis
                    // (that is, EPSILON should represent your maximum allowable margin of error)
                    if (omegaMagnitude > EPSILON)
                    {
                        axisX /= omegaMagnitude;
                        axisY /= omegaMagnitude;
                        axisZ /= omegaMagnitude;
                    }

                    // Integrate around this axis with the angular speed by the time step
                    // in order to get a delta rotation from this sample over the time step
                    // We will convert this axis-angle representation of the delta rotation
                    // into a quaternion before turning it into the rotation matrix.
                    float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                    float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                    float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                    deltaRotationVector[0] = sinThetaOverTwo * axisX;
                    deltaRotationVector[1] = sinThetaOverTwo * axisY;
                    deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                    deltaRotationVector[3] = cosThetaOverTwo;
                }
                timestamp = event.timestamp;
                float[] deltaRotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
                // User code should concatenate the delta rotation we computed with the current rotation
                // in order to get the updated rotation.
                // rotationCurrent = rotationCurrent * deltaRotationMatrix;

                SensorManager.getOrientation(deltaRotationMatrix, deltaOrientation);

                // orientation in radians
                z = (deltaOrientation[0]);
                x = (deltaOrientation[1]);
//                y = (deltaOrientation[2]);

                //orientation in degrees
                z = (Math.toDegrees(z));
                x = (Math.toDegrees(x));
//                y = (Math.toDegrees(y));

                deltas = "{\"X\":" + "\"" + df.format(z) + "\"," + "\"Y\":\"" + df.format(x) + "\"}" + "\0";

                synchronized (sharedQueue)
                {
                    try
                    {
                        sharedQueue.put(deltas);
                        deltas=null;
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {

            }
        };
        mSensorManager.registerListener(mListener, sensor,SensorManager.SENSOR_DELAY_FASTEST,handler);
        Looper.loop();
    }

    void stopThread()
    {
        //Unregister the listener
        if (mSensorManager != null)
        {
            mSensorManager.unregisterListener(mListener);
        }

        if (mHandlerThread.isAlive())
            mHandlerThread.quitSafely();

        deltas=null;
    }
}

class ScrollWheel implements Runnable
{

    private final BlockingQueue<String> sharedQueue;
    private Context mContext;
    private SensorManager mSensorManager = null;
    private Sensor mSensor;
    private SensorEventListener mListener;
    private HandlerThread mHandlerThread;
    private String deltas=null;

    private DecimalFormat df = new DecimalFormat("0.00");

    ScrollWheel(BlockingQueue<String> bq,Context context)
    {
        this.sharedQueue = bq;
        this.mContext = context;
    }

    @Override
    public void run()
    {
//        Log.e("Thread Name", Thread.currentThread().getName()+ "THIS THREAD RIGHT HERE!");
        Looper.prepare();
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mHandlerThread = new HandlerThread("AccelerometerLogListener");
        mHandlerThread.start();
        Handler handler = new Handler(mHandlerThread.getLooper());

        mListener = new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent event)
            {
//                float axisX = event.values[0];
                float axisY = event.values[1];
//                float axisZ = event.values[2];

                deltas = "{\"X\":" + "\"" + "S" + "\"," + "\"Y\":\"" + df.format(axisY) + "\"}" + "\0";

                synchronized (sharedQueue) {
                    try {
                        sharedQueue.put(deltas);
                        deltas=null;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mSensorManager.registerListener(mListener,mSensor,SensorManager.SENSOR_DELAY_UI,handler);
        Looper.loop();
    }

    void stopThread()
    {
        //Unregister the listener
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mListener);
        }
        if (mHandlerThread.isAlive())
            mHandlerThread.quitSafely();
        deltas=null;
    }
}
