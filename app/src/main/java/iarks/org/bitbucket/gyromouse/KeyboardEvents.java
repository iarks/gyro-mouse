package iarks.org.bitbucket.gyromouse;

import android.view.KeyEvent;
import java.util.concurrent.BlockingQueue;

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
                    sharedQueue.put("{\"X\":" + "\"" + "BS" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "U" + "\"," + "\"Y\":\"" + (int)charAt + "\"}" + "\0");
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
            sharedQueue.put("{\"X\":" + "\"" + "U" + "\"," + "\"Y\":\"" + i + "\"}" + "\0");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}


