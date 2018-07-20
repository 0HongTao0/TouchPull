package xyz.awqingnian.touchpull;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import xyz.awqingnian.touchpull.widget.TouchPullView;

public class MainActivity extends AppCompatActivity {
    private static final float TOUCH_MOVE_MAX_Y = 600;
    private float mTouchMoveStartY;
    private TouchPullView mTouchPullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTouchPullView = findViewById(R.id.touchPull);
        findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //得到触摸意图
                int action = event.getActionMasked();
                switch (action) {
                    //按下操作
                    case MotionEvent.ACTION_DOWN:
                        mTouchMoveStartY = event.getY();
                        return true;
                    //移动操作
                    case MotionEvent.ACTION_MOVE:
                        float y = event.getY();
                        if (y >= mTouchMoveStartY) {
                            float moveSize = y - mTouchMoveStartY;
                            float progress = moveSize >= TOUCH_MOVE_MAX_Y ? 1 : moveSize / TOUCH_MOVE_MAX_Y;
                            mTouchPullView.setProgress(progress);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        mTouchPullView.release();
                        return true;
                    default:
                }
                return false;
            }
        });
    }

}
