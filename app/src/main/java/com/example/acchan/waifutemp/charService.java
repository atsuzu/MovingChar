package com.example.acchan.waifutemp;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class charService extends Service {

    private static final int ANIMATION_FRAME_RATE 			= 30;	// Animation frame rate per second.

    private WindowManager windowManager;
    private ImageView waifu;
    private WindowManager.LayoutParams paramsF;

    boolean mHasDoubleClicked = false;
    long lastPressTime;
    private Boolean _enable = true;

    // Controls for animations
    private Timer mTrayAnimationTimer;
    private TrayAnimationTimerTask 	mTrayTimerTask;
    private Handler mAnimationHandler = new Handler();

    AnimationDrawable animation;

    // Controls for animations
    private Timer mTrayAnimationTimer2;
    private TrayAnimationTimerTask2 	mTrayTimerTask2;
    private Handler mAnimationHandler2 = new Handler();

    //used for sideway direction.
    private int counter = 0;
    Random rand = new Random();
    int direction = rand.nextInt(3);

    public charService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        waifu = new ImageView(this);
        waifu.setImageResource(R.drawable.stand1);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;

        int screenLength = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        params.x = screenLength/2;
        params.y = screenHeight/2;

        mTrayTimerTask = new TrayAnimationTimerTask();
        mTrayAnimationTimer = new Timer();
        mTrayAnimationTimer.schedule(mTrayTimerTask, 0, ANIMATION_FRAME_RATE);

        mTrayTimerTask2 = new TrayAnimationTimerTask2();
        mTrayAnimationTimer2 = new Timer();
        mTrayAnimationTimer2.schedule(mTrayTimerTask2, 0, ANIMATION_FRAME_RATE);

        windowManager.addView(waifu, params);
        paramsF = params;

        try {
            waifu.setOnTouchListener(new View.OnTouchListener() {

                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            mTrayAnimationTimer2.cancel();

                            // Get current time in nano seconds.
                            long pressTime = System.currentTimeMillis();


//                            // If double click...
//                            if (pressTime - lastPressTime <= 300) {
//                                createNotification();
//                                ServiceFloating.this.stopSelf();
//                                mHasDoubleClicked = true;
//                            }
//                            else {     // If not double click....
//                                mHasDoubleClicked = false;
//                            }
                            lastPressTime = pressTime;
                            initialX = paramsF.x;
                            initialY = paramsF.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:

                            mTrayTimerTask = new TrayAnimationTimerTask();
                            mTrayAnimationTimer = new Timer();
                            mTrayAnimationTimer.schedule(mTrayTimerTask, 0, ANIMATION_FRAME_RATE);

                            //Log.d("CHECKING", "paramsF.y is = " + paramsF.y);
                            //windowManager.updateViewLayout(waifu, paramsF);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(waifu, paramsF);
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }

        waifu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //initiatePopupWindow(chatHead);
                _enable = false;
                //				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //				getApplicationContext().startActivity(intent);
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (waifu != null) windowManager.removeView(waifu);
    }

    // Timer for animation/automatic movement of the tray.
    private class TrayAnimationTimerTask extends TimerTask {

        // Ultimate destination coordinates toward which the tray will move
        int mDestX;
        int mDestY;

        public TrayAnimationTimerTask() {

            // Setup destination coordinates based on the tray state.
            super();

        }

        // This function is called after every frame.
        @Override
        public void run() {

            // handler is used to run the function on main UI thread in order to
            // access the layouts and UI elements.
            mAnimationHandler.post(new Runnable() {
                @Override
                public void run() {

                    int screenHeight = getResources().getDisplayMetrics().heightPixels;

                    //1600 is the destination y axis.
                    int delta = screenHeight - paramsF.y;

                    if (delta >= 50)
                        paramsF.y += 50;
                    else {
                        paramsF.y += delta;
                    }
                    // Update coordinates of the tray
                    //mRootLayoutParams.x = (2*(mRootLayoutParams.x-mDestX))/3 + mDestX;
                    //paramsF.y = (2*(paramsF.y-mDestY))/3 + mDestY;
                    windowManager.updateViewLayout(waifu, paramsF);
                    //animateButtons();

                    // Cancel animation when the destination is reached
                    if (paramsF.y == screenHeight) {
                        TrayAnimationTimerTask.this.cancel();

                        mTrayTimerTask2 = new TrayAnimationTimerTask2();
                        mTrayAnimationTimer2 = new Timer();
                        mTrayAnimationTimer2.schedule(mTrayTimerTask2, 0, ANIMATION_FRAME_RATE);
                    }
                }
            });
        }
    }

    // Timer for animation/automatic movement of the tray.
    private class TrayAnimationTimerTask2 extends TimerTask {

        // Ultimate destination coordinates toward which the tray will move
        int mDestX;
        int mDestY;

        public TrayAnimationTimerTask2() {

            // Setup destination coordinates based on the tray state.
            super();

        }

        // This function is called after every frame.
        @Override
        public void run() {

            // handler is used to run the function on main UI thread in order to
            // access the layouts and UI elements.
            mAnimationHandler.post(new Runnable() {
                @Override
                public void run() {

                    int screenLength = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;

                    //direction = 2;
                    //generate new direction every 100 steps (20 * 5)
                    if(counter >= 50) {
                        counter = 0;

                        direction = rand.nextInt(3); // Gives n such that 0 <= n < 20




                        if (direction == 0)  //goes right
                        {
                            animation = new AnimationDrawable();
                            animation.addFrame(getResources().getDrawable(R.drawable.walkr1), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkr2), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkr3), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkr4), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkr5), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkr6), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkr7), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkr8), 100);
                            animation.setOneShot(false);
                            waifu.setImageDrawable(animation);
                            animation.start();

                        }
                        else if (direction == 1)    //goes left
                        {
                            animation = new AnimationDrawable();
                            animation.addFrame(getResources().getDrawable(R.drawable.walkl1), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkl2), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkl3), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkl4), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkl5), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkl6), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkl7), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.walkl8), 100);
                            animation.setOneShot(false);
                            waifu.setImageDrawable(animation);
                            animation.start();

                        }
                        else if (direction == 2) {
                            animation = new AnimationDrawable();
                            animation.addFrame(getResources().getDrawable(R.drawable.stand1), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.stand2), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.stand3), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.stand4), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.stand5), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.stand6), 100);
                            animation.addFrame(getResources().getDrawable(R.drawable.stand7), 100);
                            animation.setOneShot(false);
                            waifu.setImageDrawable(animation);
                            animation.start();
                            //do nothing.
                            //waifu.setImageResource(R.drawable.stand1);
                        }


                    }
                    int delta = screenLength - paramsF.x;
                    if(direction == 0)
                    {
                        //waifu.setImageResource(R.drawable.chibispritesright);
                        if (delta >= 5)
                            paramsF.x += 5;
                        else
                            paramsF.x += delta;
                    }
                    else if(direction == 1)
                    {
                        //waifu.setImageResource(R.drawable.chibispritesleft);
                        if (delta <= screenLength - 5)
                            paramsF.x -= 5;
                        else
                            paramsF.x -= screenLength - delta;

                    }
                    else {
                        //do nothing.
                    }



                    windowManager.updateViewLayout(waifu, paramsF);
                    counter++;
                    // Cancel animation when the destination is reached
                    if (paramsF.y < screenHeight) {
                        TrayAnimationTimerTask2.this.cancel();
                        mTrayAnimationTimer2 = new Timer();
                    }
                }
            });
        }
    }
}
