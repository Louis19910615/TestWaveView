
package louis.testwaveview.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import louis.testwaveview.R;
import louis.testwaveview.utils.UiUtils;

/**
 * @author tianjian
 * @created 2015/2/2
 */
public class PorterDuffXfermodeView extends View {

    private static final int WAVE_TRANS_SPEED = 4;

    private Paint mBitmapPaint, mPicPaint;
    private int mTotalWidth, mTotalHeight;
    private int mCenterX, mCenterY;
    private int mSpeed;

    private Bitmap mSrcBitmap;
    private Rect mSrcRect, mDestRect;

    private PorterDuffXfermode mPorterDuffXfermode;
    private Bitmap mMaskBitmap;
    private Rect mMaskSrcRect, mMaskDestRect;
    private PaintFlagsDrawFilter mDrawFilter;

    private int mCurrentPosition;

    public PorterDuffXfermodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initBitmap();
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mSpeed = UiUtils.dipToPx(context, WAVE_TRANS_SPEED);
        mDrawFilter = new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, Paint.DITHER_FLAG);
        new Thread() {
            public void run() {
                while (true) {
                    mCurrentPosition += mSpeed;
                    if (mCurrentPosition >= mSrcBitmap.getWidth()) {
                        mCurrentPosition = 0;
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                    }

                    postInvalidate();
                }

            };
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.setDrawFilter(mDrawFilter);
        canvas.drawColor(Color.TRANSPARENT);

        /*
         */
        int sc = canvas.saveLayer(0, 0, mTotalWidth, mTotalHeight, null, Canvas.ALL_SAVE_FLAG);

        mSrcRect.set(mCurrentPosition, 0, mCurrentPosition + mCenterX, mTotalHeight);
        canvas.drawBitmap(mSrcBitmap, mSrcRect, mDestRect, mBitmapPaint);

        mBitmapPaint.setXfermode(mPorterDuffXfermode);
        canvas.drawBitmap(mMaskBitmap, mMaskSrcRect, mMaskDestRect,
                mBitmapPaint);
        mBitmapPaint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    private void initBitmap() {
        mSrcBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.wave_2000))
                .getBitmap();
        mMaskBitmap = ((BitmapDrawable) getResources().getDrawable(
                R.drawable.circle_500))
                .getBitmap();
    }

    private void initPaint() {

        mBitmapPaint = new Paint();
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mPicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPicPaint.setDither(true);
        mPicPaint.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        mCenterX = mTotalWidth / 2;
        mCenterY = mTotalHeight / 2;

        mSrcRect = new Rect();
        mDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);

        int maskWidth = mMaskBitmap.getWidth();
        int maskHeight = mMaskBitmap.getHeight();
        mMaskSrcRect = new Rect(0, 0, maskWidth, maskHeight);
        mMaskDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);
    }

}
