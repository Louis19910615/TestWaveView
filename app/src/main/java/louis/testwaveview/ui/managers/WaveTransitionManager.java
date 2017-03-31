package louis.testwaveview.ui.managers;

import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

import louis.testwaveview.ui.SurfaceWaveView;

/**
 * Created by louis on 2017/3/30.
 */

// TODO 验证对象的 == 和 equal的区别
public class WaveTransitionManager {

    private SurfaceWaveView mSurfaceWaveView;
    private boolean isListening = false;

    private ConcurrentLinkedQueue<Float> mTransitionAQueue;
    private Float mCurrentA = (float)(20 * 2); //当前波纹幅度值
    private Float mTargetA = (float)(20 * 2); //当前波纹设置目标值

    private ConcurrentLinkedQueue<Integer> mTransitionSpeedOneQueue;
    private Integer mCurrentSpeedOne = 7 * 2; //当前快波纹速度
    private Integer mTargetSpeedOne = 7 * 2;   //当前快波纹设置目标值

    private ConcurrentLinkedQueue<Integer> mTransitionSpeedTwoQueue;
    private Integer mCurrentSpeedTwo = 5 * 2; //当前慢波纹速度
    private Integer mTargetSpeedTwo = 5 * 2;   //当前慢波纹设置目标值

    private ConcurrentLinkedQueue<Integer> mTransitionWaterDepth;
    private Integer mCurrentWaterDepth = 100; //当前波纹水深
    private Integer mTargetWaterDepth = 100; //当前波纹水深目标值

    private ListenerThread mListenerThread;

    public WaveTransitionManager(SurfaceWaveView surfaceWaveView) {
        mSurfaceWaveView = surfaceWaveView;
        mTransitionAQueue = new ConcurrentLinkedQueue<>();
        mTransitionSpeedOneQueue = new ConcurrentLinkedQueue<>();
        mTransitionSpeedTwoQueue = new ConcurrentLinkedQueue<>();
        mTransitionWaterDepth = new ConcurrentLinkedQueue<>();
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
                if (!mSurfaceWaveView.getIsAttributeChange()) {
                    if (!mTransitionAQueue.isEmpty()) {
                        mCurrentA = mTransitionAQueue.poll();
                        mSurfaceWaveView.setStretchFactorA(mCurrentA);
                    }

                    if (!mTransitionSpeedOneQueue.isEmpty()) {
                        mCurrentSpeedOne = mTransitionSpeedOneQueue.poll();
                        mSurfaceWaveView.setTranslateXSpeedOne(mCurrentSpeedOne);
                    }

                    if (!mTransitionSpeedTwoQueue.isEmpty()) {
                        mCurrentSpeedTwo = mTransitionSpeedTwoQueue.poll();
                        mSurfaceWaveView.setTranslateXSpeedTwo(mCurrentSpeedTwo);
                    }

                    if (!mTransitionWaterDepth.isEmpty()) {
                        mCurrentWaterDepth = mTransitionWaterDepth.poll();
                        mSurfaceWaveView.setWaterDepth(mCurrentWaterDepth);
                    }
                }

                SystemClock.sleep(100);
            }
        }
    }

    public void offer0() {
        transitionA((float) 0);
        transitionSpeedOne(0);
        transitionSpeedTwo(0);
        transitionWaterDepth(60);
    }

    public void offer1() {
        transitionA((float) 20);
        transitionSpeedOne(7);
        transitionSpeedTwo(5);
        transitionWaterDepth(80);
    }

    public void offer2() {
        transitionA((float) (20 * 2));
        transitionSpeedOne(7 * 2);
        transitionSpeedTwo(5 * 2);
        transitionWaterDepth(100);
    }

    public void offer3() {
        transitionA((float) (20 * 3));
        transitionSpeedOne(15);
        transitionSpeedTwo(12);
        transitionWaterDepth(120);
    }
    // TODO 优化渐变过程，通过差距和点数成正比优化，及等差一样
    private void transitionA(Float targetA) {
        if (mTargetA.equals(targetA)) {
            Log.d("WaveTransitionManager", "mTargetA equal.");
            return;
        }
        mTargetA = targetA;
        mTransitionAQueue.clear();
        double tempA = mCurrentA;
        double err = (tempA - targetA) / 80.0;
        for (int i = 0; i < 80; i++) {
            tempA -= err;
            mTransitionAQueue.offer((float) tempA);
        }
        mTransitionAQueue.offer(targetA);
    }

    private void transitionSpeedOne(Integer targetSpeedOne) {
        if (mTargetSpeedOne.equals(targetSpeedOne)) {
            Log.d("WaveTransitionManager", "mTargetSpeedOne equal.");
            return;
        }
        mTargetSpeedOne = targetSpeedOne;
        mTransitionSpeedOneQueue.clear();
        double tempSpeedOne = mCurrentSpeedOne;
        double err = (tempSpeedOne - targetSpeedOne) / 80.0;
        for (int i = 0; i < 80; i++) {
            tempSpeedOne -= err;
            mTransitionSpeedOneQueue.offer((int) Math.floor(tempSpeedOne));
        }
        mTransitionSpeedOneQueue.offer(targetSpeedOne);
    }

    private void transitionSpeedTwo(Integer targetSpeedTwo) {
        if (mTargetSpeedTwo.equals(targetSpeedTwo)) {
            Log.d("WaveTransitionManager", "mTargetSpeedTwo equal.");
            return;
        }
        mTargetSpeedTwo = targetSpeedTwo;
        mTransitionSpeedTwoQueue.clear();
        double tempSpeedTwo = mCurrentSpeedTwo;
        double err = (tempSpeedTwo - targetSpeedTwo) / 80.0;
        for (int i = 0; i < 80; i++) {
            tempSpeedTwo -= err;
            mTransitionSpeedTwoQueue.offer((int) Math.floor(tempSpeedTwo));
        }
        mTransitionSpeedTwoQueue.offer(targetSpeedTwo);
    }

    private void transitionWaterDepth (Integer targetWaterDepth) {
        if (mTargetWaterDepth.equals(targetWaterDepth)) {
            Log.d("WaveTransitionManager", "mTargetWaterDepth equal.");

            return;
        }
        mTargetWaterDepth = targetWaterDepth;
        mTransitionWaterDepth.clear();
        double tempWaterDepth = mCurrentWaterDepth;
        double err = (tempWaterDepth - targetWaterDepth) / 80;
        for (int i = 0; i < 80; i ++) {
            tempWaterDepth -= err;
            mTransitionWaterDepth.offer((int) Math.floor(tempWaterDepth));
        }
        mTransitionWaterDepth.offer(targetWaterDepth);
    }
}
