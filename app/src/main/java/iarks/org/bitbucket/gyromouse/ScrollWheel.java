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
                float axisY = event.values[1];

                deltas = ("S;"+df.format(axisY)+";"+ Client.sessionKey);

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
        mSensorManager.registerListener(mListener,mSensor,SensorManager.SENSOR_DELAY_GAME,handler);
        Looper.loop();
    }

    void stopThread()
    {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mListener);
        }
        if (mHandlerThread.isAlive())
            mHandlerThread.quitSafely();
        deltas=null;
    }
}
