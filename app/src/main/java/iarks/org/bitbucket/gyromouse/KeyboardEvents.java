package iarks.org.bitbucket.gyromouse;

import android.util.Log;
import android.view.KeyEvent;
import java.util.concurrent.BlockingQueue;

import static android.R.attr.tag;

class KeyboardEvents implements Runnable
{
    private static final String TAG = MainActivity.class.getName();

    private KeyEvent event;
    private BlockingQueue<String> sharedQueue;
    KeyboardEvents(KeyEvent event, BlockingQueue<String> sharedQueue)
    {
        this.event=event;
        this.sharedQueue=sharedQueue;
    }

    @Override
    public void run()
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
                        sharedQueue.put("BS;"+"xx;" + CurrentServer.sessionKey);
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
            // keycode for unknown key events
            if (keyCode == 0)
            {
//              Toast.makeText(context, "KEYCODE IS 0 - IT IS AN UNKNOWN KEY EVENT", Toast.LENGTH_SHORT).show();

                char charAt = event.getCharacters().charAt(0);
                if (charAt != '\u0000')
                {
                    String ch = event.getCharacters();
                    try
                    {
//                        Toast.makeText(context, "UNICODE CHARACTER NOT 0 : " + ch, Toast.LENGTH_SHORT).show();
                        synchronized (sharedQueue) {
                            sharedQueue.put("U;" + charAt+ ";" + CurrentServer.sessionKey);
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
            synchronized (sharedQueue) {
                sharedQueue.put("U;" + (char)i + ";" + CurrentServer.sessionKey);
//                Log.d(TAG,event.getCharacters().charAt(0)+"");
                Log.d(TAG,(char)i+"");
//                sharedQueue.put("{\"X\":" + "\"" + "U" + "\"," + "\"Y\":\"" + event.getCharacters().charAt(0) + "\"}" + "\0");
                sharedQueue.notifyAll();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}


