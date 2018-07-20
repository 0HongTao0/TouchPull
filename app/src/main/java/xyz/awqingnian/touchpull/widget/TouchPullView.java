package xyz.awqingnian.touchpull.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created 2018/7/19.
 *
 * @author HongTao
 */
public class TouchPullView extends View {
    private static final String TAG = TouchPullView.class.getSimpleName();
    /**
     * 圆的画笔
     */
    private Paint mCirclePaint;
    /**
     * 圆的半径
     */
    private int mCircleRadius = 40;
    /**
     * /圆心坐标
     */
    private float mCirclePointX, mCirclePointY;
    /**
     * 高度改变的进度值
     */
    private float mProgress;
    /**
     * 可拖动的高度
     */
    private int mDragHeight = 200;
    /**
     * 目标宽度
     */
    private int mTargetWidth = 200;
    /**
     * 贝塞尔曲线画笔
     */
    private Paint mPathPaint;
    /**
     * 贝塞尔曲线的路径
     */
    private Path mPath = new Path();
    /**
     * 重心点的最终高度，决定控制点的 Y 的坐标
     */
    private int mTargetGravityHeight;
    /**
     * 角度变换 0 - 135
     */
    private int mTangentAngle = 110;
    /**
     * 动画效果
     */
    private ValueAnimator mValueAnimator;

    private Interpolator mProgressInterpolator = new DecelerateInterpolator();
    private Interpolator mTanentAngleInterpolator;

    public TouchPullView(Context context) {
        super(context);
        init();
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 初始化方法
     */
    private void init() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        // 初始化圆的画笔
        //设置抗锯齿
        p.setAntiAlias(true);
        //设置防抖动
        p.setDither(true);
        //设置填充方式
        p.setStyle(Paint.Style.FILL);
        //设置颜色黑色
        p.setColor(0xFF000000);
        mCirclePaint = p;

        // 初始化 Path 画笔
        //设置抗锯齿
        p.setAntiAlias(true);
        //设置防抖动
        p.setDither(true);
        //设置填充方式
        p.setStyle(Paint.Style.FILL);
        //设置颜色黑色
        p.setColor(0xFF000000);
        mPathPaint = p;

        //切角路径插值器
        mTanentAngleInterpolator = PathInterpolatorCompat.create((mCircleRadius * 2.0f) / mDragHeight, 90.0f / mTangentAngle);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //进行基础坐标参数系改变
        int count = canvas.save();
        float tranX = (getWidth() - getValueByLine(getWidth(), mTargetWidth, mProgress)) / 2;
        canvas.translate(tranX, 0);

        //画圆
        canvas.drawCircle(mCirclePointX, mCirclePointY, mCircleRadius, mCirclePaint);
        //画贝塞尔曲线
        canvas.drawPath(mPath, mPathPaint);
        canvas.restoreToCount(count);
    }

    /**
     * 当进行测量时触发
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽度的意图，宽度的类型
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int iWidth = 2 * mCircleRadius + getPaddingLeft() + getPaddingRight();
        int iHeight = (int) ((mDragHeight * mProgress + 0.5f) + getPaddingTop() + getPaddingBottom());

        int measureWidht;
        if (widthMode == MeasureSpec.EXACTLY) {
            //确切值
            measureWidht = width;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //最多值
            measureWidht = Math.min(iWidth, width);
        } else {
            measureWidht = iWidth;
        }

        int measureHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            //确切值
            measureHeight = height;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //最多值
            measureHeight = Math.min(iHeight, height);
        } else {
            measureHeight = iHeight;
        }

        //设置测量的高度宽度
        setMeasuredDimension(measureWidht, measureHeight);
    }

    /**
     * 当大小改变时触发的的方法
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //当高度变化时进行路径更新
        updatePathLayout();
    }


    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(float progress) {
        Log.d(TAG, "setProgress: " + progress);
        mProgress = progress;
        //请求重新进行测量
        requestLayout();
    }

    /**
     * 更新路径的相关操作
     */
    private void updatePathLayout() {
        //获取进度
        final float progress = mProgressInterpolator.getInterpolation(mProgress);

        final Path path = mPath;
        //重置
        path.reset();
        //获取可绘制区域高度宽度
        final float w = getValueByLine(getWidth(), mTargetWidth, mProgress);
        final float h = getValueByLine(0, mDragHeight, mProgress);
        //X 对称轴的参数，圆心 X 坐标
        final float cPointX = w / 2.0f;
        //圆的半径
        final float cRadius = mCircleRadius;
        //圆心 Y 坐标
        final float cPointY = h - cRadius;
        //控制点结束 Y 值
        final float endControlY = mTargetGravityHeight;

        //更新圆的坐标
        mCirclePointX = cPointX;
        mCirclePointY = cPointY;

        //复位 path
        path.reset();
        path.moveTo(0, 0);

        //左边结束点
        float lEndPointX, lEndPointY;
        //左边控制点
        float lControlPointX, lControlPointY;

        //获取当前切线弧度
        float angle = mTangentAngle * mTanentAngleInterpolator.getInterpolation(progress);
        double radian = Math.toRadians(angle);
        float x = (float) (Math.sin(radian) * cRadius);
        float y = (float) (Math.cos(radian) * cRadius);

        lEndPointX = cPointX - x;
        lEndPointY = cPointY + y;

        //控制点 Y 轴变化
        lControlPointY = getValueByLine(0, endControlY, progress);
        //控制点与结束点之间的高度
        float tHeight = lEndPointY - lControlPointY;
        //控制点与 X 的坐标距离
        float tWidth = (float) (tHeight / Math.tan(radian));

        lControlPointX = lEndPointX - tWidth;
        //左边贝塞尔曲线
        path.quadTo(lControlPointX, lControlPointY, lEndPointX, lEndPointY);
        //连接到右边
        path.lineTo(cPointX + (cPointX - lEndPointX), lEndPointY);
        //右边贝塞尔曲线
        path.quadTo(cPointX + cPointX - lControlPointX, lControlPointY, w, 0);
    }

    /**
     * 获取当前值
     *
     * @param start
     * @param end
     * @param progress
     * @return
     */
    private float getValueByLine(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    public void release() {
        if (mValueAnimator == null) {
            ValueAnimator animator = ValueAnimator.ofFloat(mProgress, 0f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(400);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object val = animation.getAnimatedValue();
                    if (val instanceof Float) {
                        setProgress((Float) val);
                    }
                }
            });
            mValueAnimator = animator;
        } else {
            mValueAnimator.cancel();
            mValueAnimator.setFloatValues(mProgress, 0f);
        }
        mValueAnimator.start();
    }
}
