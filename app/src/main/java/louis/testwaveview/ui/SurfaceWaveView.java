package louis.testwaveview.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import louis.testwaveview.utils.UiUtils;

/**
 * Created by louis on 2017/3/29.
 */

public class SurfaceWaveView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private RenderThread renderThread;//绘图线程


    // 波纹颜色
    private static final int FST_WAVE_PAINT_COLOR = 0x282AE2E2;//0x880000aa;
    private static final int SEC_WAVE_PAINT_COLOR = 0x3C2AE2E2;//0x880000aa;
    // y = Asin(wx+b)+h
    private static final float STRETCH_FACTOR_A = 20*2;
    private static final int OFFSET_Y = 0;
    // 第一条水波移动速度
    private static final int TRANSLATE_X_SPEED_ONE = 7*2;
    // 第二条水波移动速度
    private static final int TRANSLATE_X_SPEED_TWO = 5*2;
    private float mCycleFactorW;

    private int mTotalWidth, mTotalHeight;
    private float[] mYPositions;
    private float[] mResetOneYPositions;
    private float[] mResetTwoYPositions;
    private int mXOffsetSpeedOne;
    private int mXOffsetSpeedTwo;
    private int mXOneOffset;
    private int mXTwoOffset;

    private Paint mWavePaint;
    private DrawFilter mDrawFilter;
    private Path mPathFst;
    private Path mPathSec;

    private boolean isDrawing = false;// 控制绘制的开关

    public SurfaceWaveView(Context context) {
        super(context);
        Log.d("SurfaceWaveView", "SurfaceWaveView1");
        mHolder = this.getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);//支持透明度
        this.setZOrderOnTop(true);

        // 将dp转化为px，用于控制不同分辨率上移动速度基本一致
        mXOffsetSpeedOne = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_ONE);
        mXOffsetSpeedTwo = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_TWO);

        // 初始绘制波纹的画笔
        mWavePaint = new Paint();
        // 去除画笔锯齿
        mWavePaint.setAntiAlias(true);
        // 设置风格为实线
        mWavePaint.setStyle(Paint.Style.FILL);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mPathFst = new Path();
        mPathSec = new Path();

        renderThread = new RenderThread();
    }

    public SurfaceWaveView(Context context, AttributeSet attrs) {

        super(context, attrs);
        Log.d("SurfaceWaveView", "SurfaceWaveView2");
        mHolder = this.getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);//支持透明度
        this.setZOrderOnTop(true);

        // 将dp转化为px，用于控制不同分辨率上移动速度基本一致
        mXOffsetSpeedOne = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_ONE);
        mXOffsetSpeedTwo = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_TWO);

        // 初始绘制波纹的画笔
        mWavePaint = new Paint();
        // 去除画笔锯齿
        mWavePaint.setAntiAlias(true);
        // 设置风格为实线
        mWavePaint.setStyle(Paint.Style.FILL);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mPathFst = new Path();
        mPathSec = new Path();

        renderThread = new RenderThread();
    }

    public SurfaceWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("SurfaceWaveView", "SurfaceWaveView3");
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d("SurfaceWaveView", "surfaceCreated");
        isDrawing = true;
        new Thread(renderThread).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        Log.e("SurfaceWaveView", "surfaceChange");
        Log.d("SurfaceWaveView", String.valueOf(width));
        Log.d("SurfaceWaveView", String.valueOf(height));
        // 记录下view的宽高
        mTotalWidth = width;
        mTotalHeight = height;
        // 用于保存原始波纹的y值
        mYPositions = new float[mTotalWidth];
        // 用于保存波纹一的y值
        mResetOneYPositions = new float[mTotalWidth];
        // 用于保存波纹二的y值
        mResetTwoYPositions = new float[mTotalWidth];

        // 将周期定为view总宽度
        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        // 根据view总宽度得出所有对应的y值
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STRETCH_FACTOR_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d("SurfaceWaveView", "surfaceDestroyed");
        isDrawing = false;
    }


    /**
     * 绘制界面的线程
     *
     * @author louis
     */
    private class RenderThread implements Runnable {
        @Override
        public void run() {
            Log.d("SurfaceWaveView", "start");
            // 不停绘制界面，这里是异步绘制，不采用外部通知开启绘制的方式，水波根据数据更新才会开始增长
            while (isDrawing) {
                Log.d("SurfaceWaveView", "run");
                drawUI();

                SystemClock.sleep(20);//控制刷新速率，减少cpu占用
            }
        }
    }

    public void drawUI() {
        Canvas canvas = mHolder.lockCanvas();//锁定画布
        try {
            drawCanvas(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.d("SurfaceWaveView", "finally");
            if (canvas != null)
                mHolder.unlockCanvasAndPost(canvas);//释放画布
        }
    }

    /**
     * 绘制图像
     *
     * @author louis
     */
    private void drawCanvas(Canvas canvas) {
        if (canvas == null)
            return;
        // 从canvas层面去除绘制时锯齿
        canvas.setDrawFilter(mDrawFilter);

        resetPositonY();
        //画新的东西之前需要先清除画布内容
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        canvas.save();//保存画布状态
        mPathFst.reset();
        mPathSec.reset();
        mPathFst.moveTo(0, mTotalHeight);
        mPathSec.moveTo(0, mTotalHeight);
        for (int i = 0; i < mTotalWidth; i++) {
            // 减400只是为了控制波纹绘制的y的在屏幕的位置，大家可以改成一个变量，然后动态改变这个变量，从而形成波纹上升下降效果
            // 绘制第一条水波纹
            mPathFst.lineTo(i, mTotalHeight - mResetOneYPositions[i] - 400);
//            canvas.drawPoint(i, mTotalHeight - mResetOneYPositions[i] - 400, mWavePaint);
//            canvas.drawLine(i, mTotalHeight - mResetOneYPositions[i] - 400, i,
//                    mTotalHeight,
//                    mWavePaint);
            // 绘制第二条水波纹
            mPathSec.lineTo(i, mTotalHeight - mResetTwoYPositions[i] - 400);
//            canvas.drawPoint(i, mTotalHeight - mResetTwoYPositions[i] - 400, mWavePaint);
//            canvas.drawLine(i, mTotalHeight - mResetTwoYPositions[i] - 400, i,
//                    mTotalHeight,
//                    mWavePaint);

        }
        mPathFst.lineTo(mTotalWidth, mTotalHeight);
        mPathSec.lineTo(mTotalWidth, mTotalHeight);
        mPathFst.close();
        mPathSec.close();

        // 设置画笔颜色
        mWavePaint.setColor(FST_WAVE_PAINT_COLOR);

        canvas.drawPath(mPathFst, mWavePaint);

        // 设置画笔颜色
        mWavePaint.setColor(SEC_WAVE_PAINT_COLOR);

        canvas.drawPath(mPathSec, mWavePaint);

//        canvas.restore();//恢复画布状态

        // 改变两条波纹的移动点
        mXOneOffset += mXOffsetSpeedOne;
        mXTwoOffset += mXOffsetSpeedTwo;

        // 如果已经移动到结尾处，则重头记录
        if (mXOneOffset >= mTotalWidth) {
            mXOneOffset = 0;
        }
        if (mXTwoOffset > mTotalWidth) {
            mXTwoOffset = 0;
        }
    }

    private void resetPositonY() {
        // mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = mYPositions.length - mXOneOffset;
        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mYPositions, mXOneOffset, mResetOneYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOneYPositions, yOneInterval, mXOneOffset);

        int yTwoInterval = mYPositions.length - mXTwoOffset;
        System.arraycopy(mYPositions, mXTwoOffset, mResetTwoYPositions, 0,
                yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoYPositions, yTwoInterval, mXTwoOffset);
    }
}
