package net.ledii.ar_excavator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

import java.util.Random;

public class Global {
    //Singleton instance
    private static Global instance;

    //Constants
    public static final int CONTEXT = 0;
    public static final int SCREEN_WIDTH = 1;
    public static final int SCREEN_HEIGHT = 2;
    public static final int SCREEN_CENTER_X = 3;
    public static final int SCREEN_CENTER_Y = 4;

    //Variables
    private Point screenSize;
    private Context context;



    //Constructor
    private Global(Context context) {
        this.context = context;

        //Get window size
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);
    }



    //Public functions
    public static void initialize(Context context) {
        if (instance == null) {
            instance = new Global(context);
        }
    }

    public static Object get(int getType) {
        Object result = null;

        switch (getType) {
            case CONTEXT: {
                result = instance.context;
                break;
            }
            case SCREEN_WIDTH: {
                result = instance.screenSize.x;
                break;
            }
            case SCREEN_HEIGHT: {
                result = instance.screenSize.y;
                break;
            }
            case SCREEN_CENTER_X: {
                result = (int)(instance.screenSize.x / 2f);
                break;
            }
            case SCREEN_CENTER_Y: {
                result = (int)(instance.screenSize.y / 2f);
                break;
            }
        }

        return result;
    }

    public static int randomInt(int min, int max) {
        Random rand = new Random();
        int result = min + (rand.nextInt(max - min + 1));
        return result;
    }
}