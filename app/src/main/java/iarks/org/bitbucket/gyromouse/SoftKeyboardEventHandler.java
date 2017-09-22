package iarks.org.bitbucket.gyromouse;

import android.util.Log;
import android.view.KeyEvent;
import java.util.concurrent.BlockingQueue;

class SoftKeyboardEventHandler implements Runnable
{
    private static final String TAG = MainActivity.class.getName();

    private KeyEvent event;
    private BlockingQueue<String> sharedQueue;
    SoftKeyboardEventHandler(KeyEvent event, BlockingQueue<String> sharedQueue)
    {
        try
        {
            this.event=event;
            this.sharedQueue=sharedQueue;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            int i;
            int keyCode = event.getKeyCode();
            int unicodeChar = event.getUnicodeChar();
            switch (keyCode)
            {
                case 67:
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("BS;"+"xx;" + CurrentConnection.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    return;
            }

            if (unicodeChar == 0)
            {
                if (keyCode == 0)
                {
                    char charAt = event.getCharacters().charAt(0);
                    if (charAt != '\u0000')
                    {
                        String ch = event.getCharacters();
                        try
                        {
                            synchronized (sharedQueue)
                            {
                                sharedQueue.put("U;" + charAt+ ";" + CurrentConnection.sessionKey);
                                sharedQueue.notifyAll();
                            }
                            return;
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }

            i = unicodeChar;
            try
            {
                synchronized (sharedQueue)
                {
                    sharedQueue.put("U;" + (char)i + ";" + CurrentConnection.sessionKey);
                    Log.d(TAG,(char)i+"");
                    sharedQueue.notifyAll();
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}


