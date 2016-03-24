package weather.young.com.xiaomiweather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Description: 自定义 view
 * Author: 0027008122 [yang.jianan@zte.com.cn]
 * Time: 2016/3/24 14:21
 * Version: 1.0
 */
public class CountView extends View implements View.OnClickListener {
    private Paint mPaint;
    private int mCount;
    private Rect mBounds;

    //文字垂直居中 用到 FontMetrics
    private Paint.FontMetrics fm; // http://blog.csdn.net/carrey1989/article/details/10399727

    public CountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBounds = new Rect();
        setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.GRAY);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

        mPaint.setColor(Color.RED);
        mPaint.setTextSize(30);
        String text = String.valueOf(mCount);
        mPaint.getTextBounds(text, 0, text.length(), mBounds);
        float textWidth = mBounds.width();
        float textHeight = mBounds.height();

        fm = mPaint.getFontMetrics();
        canvas.drawText(text, getWidth() / 2 - textWidth / 2, getHeight() / 2 - fm.descent+(fm.bottom-fm.top)/2, mPaint);
    }

    @Override
    public void onClick(View v) {
        mCount++;
        invalidate();
    }
}
