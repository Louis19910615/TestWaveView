package louis.testwaveview.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;


public class WaveHelper
{
    ObjectAnimator waveShiftAnim;
    ObjectAnimator waveSpeedAnim;
    ObjectAnimator amplitudeAnim;
    private int selectIndex = 0;
    private int[] speed = {2000, 2000, 1500, 1000};
    private float[] amplitude = {0.001f, 0.02f, 0.04f, 0.06f};
    private int duration = 5000;
    private WaveView mWaveView;
    private AnimatorSet mAnimatorSet;
    private List<Animator> mAnimators;

    public WaveHelper(WaveView waveView)
    {
        mWaveView = waveView;
        mWaveView.setShowWave(true);
        mAnimatorSet = new AnimatorSet();
        mAnimators = new ArrayList<>();
    }

    public void setSpeed(int[] speed)
    {
        this.speed = speed;
    }

    public void setAmplitude(float[] amplitude)
    {
        this.amplitude = amplitude;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public void changeLevel(int level)
    {
        int old_speed = speed[selectIndex];
        float old_amplitude = amplitude[selectIndex];
        int tgt_speed = speed[level];
        float tgt_amplitude = amplitude[level];
        selectIndex = level;

        float position = mWaveView.getWaveShiftRatio();
        cancelAnim();

        waveShiftAnim = ObjectAnimator.ofFloat(mWaveView, "waveShiftRatio", 1f, 0f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(tgt_speed);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        mAnimators.add(waveShiftAnim);

        waveSpeedAnim = ObjectAnimator.ofInt(waveShiftAnim, "duration", old_speed, tgt_speed);
        waveSpeedAnim.setDuration(duration);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        mAnimators.add(waveSpeedAnim);

        amplitudeAnim = ObjectAnimator.ofFloat(mWaveView, "amplitudeRatio", old_amplitude, tgt_amplitude);
        amplitudeAnim.setDuration(duration);
        amplitudeAnim.setInterpolator(new LinearInterpolator());
        mAnimators.add(amplitudeAnim);

        mAnimatorSet.playTogether(mAnimators);
//        mAnimatorSet.start();


        final ObjectAnimator waveResetAnim = ObjectAnimator.ofFloat(mWaveView, "waveShiftRatio", position, 0f);
        waveResetAnim.setDuration((long) (old_speed * (1f - position)));
        waveResetAnim.setInterpolator(new LinearInterpolator());
        waveResetAnim.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {

            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                waveResetAnim.cancel();
                mAnimatorSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animator)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animator)
            {

            }
        });
        waveResetAnim.start();
    }

    public void cancelAnim()
    {
        mAnimatorSet.end();
        mAnimators.clear();
        if (waveSpeedAnim != null)
        {
            waveShiftAnim.cancel();
            waveShiftAnim = null;
        }
        if (waveSpeedAnim != null)
        {
            waveSpeedAnim.cancel();
            waveSpeedAnim = null;
        }
        if (amplitudeAnim != null)
        {
            amplitudeAnim.cancel();
            amplitudeAnim = null;
        }
    }
}
