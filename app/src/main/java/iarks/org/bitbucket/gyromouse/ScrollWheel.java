package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.text.DecimalFormat;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Arkadeep on 8/30/2017.
 */
class ScrollWheel implements Runnable
{
    private static final String TAG = MainActivity.class.getName();

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

                deltas = "{\"X\":" + "\"" + "S" + "\"," + "\"Y\":\"" + df.format(axisY) + "\","+ "\"Z\":"+ "\"" + Server.sessionKey + "\"" + "}\0";

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
                    finally
                    {
                        sharedQueue.notifyAll();
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
