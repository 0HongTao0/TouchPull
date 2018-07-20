package xyz.awqingnian.touchpull.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created 2018/7/19.
 *
 * @author HongTao
 */
public class Test321 extends View {
    public Test321(Context context) {
        super(context);
        init();
    }

    public Test321(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Test321(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Test321(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 画笔
     */
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();

    private void init() {
        Paint paint = mPaint;
        //抗锯齿
        paint.setAntiAlias(true);
        //抗抖动
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        //一阶贝塞尔曲线
        Path path = mPath;
        path.moveTo(100, 100);
        path.lineTo(400, 400);

        //二阶贝塞尔曲线
        //绝对地址
        path.quadTo(600, 100, 800, 400);
//        //相对地址
//        path.rQuadTo(200, 1300, 400, 0);

        path.moveTo(400, 800);

        //三阶贝塞尔曲线
        path.cubicTo(500, 600, 700, 900, 800, 800);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }
}
