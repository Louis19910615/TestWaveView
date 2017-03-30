package louis.testwaveview.ui.managers;

import android.os.SystemClock;

import java.util.concurrent.ConcurrentLinkedQueue;

import louis.testwaveview.ui.SurfaceWaveView;

/**
 * Created by louis on 2017/3/30.
 */

public class WaveTransitionManager {

    private SurfaceWaveView mSurfaceWaveView;
    private boolean isListening = false;

    private ConcurrentLinkedQueue<Float> mTransitionAQueue;
    private Float mCurrentA = Float.valueOf(20 * 2);

    private ListenerThread mListenerThread;

    public WaveTransitionManager(SurfaceWaveView surfaceWaveView) {
        mSurfaceWaveView = surfaceWaveView;
        mTransitionAQueue = new ConcurrentLinkedQueue<Float>();
        isListening = true;
        mListenerThread = new ListenerThread();
    }

    public void startThread () {
        isListening = true;
        mListenerThread.start();
    }

    public void stopThread() {
        isListening = false;
    }

    private class ListenerThread extends Thread {

        @Override
        public void run() {
            while(isListening) {
                if (!mSurfaceWaveView.getIsAttributeChange() && !mTransitionAQueue.isEmpty()) {
                    mCurrentA = mTransitionAQueue.poll();
                    mSurfaceWaveView.setStretchFactorA(mCurrentA);
                }

                SystemClock.sleep(100);
            }
        }
    }

    public void offer1() {
        mTransitionAQueue.clear();
        Float tempA = mCurrentA;
        Float err = (tempA - 20) / 20;
        for (int i = 0; i < 20; i++) {
            tempA -= err;
            mTransitionAQueue.offer(Float.valueOf(tempA));
        }
        mTransitionAQueue.offer(Float.valueOf(20));
    }

    public void offer2() {
        mTransitionAQueue.clear();
        Float tempA = mCurrentA;
        Float err = (tempA - 40) / 20;
        for (int i = 0; i < 20; i++) {
            tempA -= err;
            mTransitionAQueue.offer(Float.valueOf(tempA));
        }
        mTransitionAQueue.offer(Float.valueOf(40));
    }

    public void offer3() {
        mTransitionAQueue.clear();
        Float tempA = mCurrentA;
        Float err = (tempA - 60) / 20;
        for (int i = 0; i < 20; i++) {
            tempA -= err;
            mTransitionAQueue.offer(Float.valueOf(tempA));
        }
        mTransitionAQueue.offer(Float.valueOf(60));
    }
}
