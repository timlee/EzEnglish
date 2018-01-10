package lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.knowmemo.usermanagement.R;
import adapter.WheelAdapter;
import listener.OnItemSelectedListener;
import model.IPickerViewData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class WheelView extends View {

    public enum ACTION {
        // 點擊，滑到盡頭，拖曳事件
        CLICK, FLING, DAGGLE
    }

    Context context;

    Handler handler;
    private GestureDetector gestureDetector;
    OnItemSelectedListener onItemSelectedListener;

    // Timer mTimer;
    ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mFuture;

    Paint paintOuterText;
    Paint paintCenterText;
    Paint paintIndicator;

    WheelAdapter adapter;

    private String label;
    int textSize;//選項的文字大小
    boolean customTextSize;//自定義文字大小，為true則用於使setTextSize函數無效，只能通過xml修改
    int maxTextWidth;
    int maxTextHeight;
    float itemHeight;//每行高度

    int textColorOut;
    int textColorCenter;
    int dividerColor;

    // 間距倍數
    static final float lineSpacingMultiplier = 1.4F;
    boolean isLoop;

    // 第一條線Y坐標
    float firstLineY;
    //第二條線Y坐標
    float secondLineY;
    //中間Y坐標
    float centerY;

    //滾動總高度y值
    int totalScrollY;
    int initPosition;
    private int selectedItem;
    int preCurrentIndex;
    int change;

    // 顯示幾個
    int itemsVisible = 11;

    int measuredHeight;
    int measuredWidth;

    int halfCircumference;
    // 半径
    int radius;

    private int mOffset = 0;
    private float previousY = 0;
    long startTime = 0;

    // 改變滑行速度
    private static final int VELOCITYFLING = 5;
    int widthMeasureSpec;

    private int mGravity = Gravity.CENTER;
    private int drawCenterContentStart = 0;
    private int drawOutContentStart = 0;
    private static final float SCALECONTENT = 0.8F;
    private static final float CENTERCONTENTOFFSET = 6;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textColorOut = getResources().getColor(R.color.pickerview_wheelview_textcolor_out);
        textColorCenter = getResources().getColor(R.color.pickerview_wheelview_textcolor_center);
        dividerColor = getResources().getColor(R.color.pickerview_wheelview_textcolor_divider);
        textSize = getResources().getDimensionPixelSize(R.dimen.pickerview_textsize);
        customTextSize = getResources().getBoolean(R.bool.pickerview_customTextSize);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.pickerview, 0, 0);
            mGravity = a.getInt(R.styleable.pickerview_pickerview_gravity, Gravity.CENTER);
            textColorOut = a.getColor(R.styleable.pickerview_pickerview_textColorOut, textColorOut);
            textColorCenter = a.getColor(R.styleable.pickerview_pickerview_textColorCenter, textColorCenter);
            dividerColor = a.getColor(R.styleable.pickerview_pickerview_dividerColor, dividerColor);
            textSize = a.getDimensionPixelOffset(R.styleable.pickerview_pickerview_textSize, textSize);
        }
        initLoopView(context);
    }

    private void initLoopView(Context context) {
        this.context = context;
        handler = new MessageHandler(this);
        gestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        gestureDetector.setIsLongpressEnabled(false);

        isLoop = true;

        totalScrollY = 0;
        initPosition = -1;

        initPaints();

    }

    private void initPaints() {
        paintOuterText = new Paint();
        paintOuterText.setColor(textColorOut);
        paintOuterText.setAntiAlias(true);
        paintOuterText.setTypeface(Typeface.MONOSPACE);
        paintOuterText.setTextSize(textSize);

        paintCenterText = new Paint();
        paintCenterText.setColor(textColorCenter);
        paintCenterText.setAntiAlias(true);
        paintCenterText.setTextScaleX(1.1F);
        paintCenterText.setTypeface(Typeface.MONOSPACE);
        paintCenterText.setTextSize(textSize);

        paintIndicator = new Paint();
        paintIndicator.setColor(dividerColor);
        paintIndicator.setAntiAlias(true);

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void remeasure() {
        if (adapter == null) {
            return;
        }

        measureTextWidthHeight();

        halfCircumference = (int) (itemHeight * (itemsVisible - 1));
        measuredHeight = (int) ((halfCircumference * 2) / Math.PI);
        //求出半徑
        radius = (int) (halfCircumference / Math.PI);
        //寬度
        measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        //計算兩條橫線和物件中間點的Y位置
        firstLineY = (measuredHeight - itemHeight) / 2.0F;
        secondLineY = (measuredHeight + itemHeight) / 2.0F;
        centerY = (measuredHeight + maxTextHeight) / 2.0F - CENTERCONTENTOFFSET;
        //初始化顯示的item的position，是否loop
        if (initPosition == -1) {
            if (isLoop) {
                initPosition = (adapter.getItemsCount() + 1) / 2;
            } else {
                initPosition = 0;
            }
        }

        preCurrentIndex = initPosition;
    }
    private void measureTextWidthHeight() {
        Rect rect = new Rect();
        for (int i = 0; i < adapter.getItemsCount(); i++) {
            String s1 = getContentText(adapter.getItem(i));
            paintCenterText.getTextBounds(s1, 0, s1.length(), rect);
            int textWidth = rect.width();
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth;
            }
            paintCenterText.getTextBounds("\u661F\u671F", 0, 2, rect); // 星期
            int textHeight = rect.height();
            if (textHeight > maxTextHeight) {
                maxTextHeight = textHeight;
            }
        }
        itemHeight = lineSpacingMultiplier * maxTextHeight;
    }

    void smoothScroll(ACTION action) {
        cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            mOffset = (int) ((totalScrollY % itemHeight + itemHeight) % itemHeight);
            if ((float) mOffset > itemHeight / 2.0F) {
                mOffset = (int) (itemHeight - (float) mOffset);
            } else {
                mOffset = -mOffset;
            }
        }
        //停止的时候，把文字位置挪回中間去
        mFuture = mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset), 0, 10, TimeUnit.MILLISECONDS);
    }

    protected final void scrollBy(float velocityY) {
        cancelFuture();

        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, VELOCITYFLING, TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    public final void setCyclic(boolean cyclic) {
        isLoop = cyclic;
    }

    public final void setTextSize(float size) {
        if (size > 0.0F && !customTextSize) {
            textSize = (int) (context.getResources().getDisplayMetrics().density * size);
            paintOuterText.setTextSize(textSize);
            paintCenterText.setTextSize(textSize);
        }
    }

    public final void setCurrentItem(int currentItem) {
        this.initPosition = currentItem;
        totalScrollY = 0;//回歸頂部
        invalidate();
    }

    public final void setOnItemSelectedListener(OnItemSelectedListener OnItemSelectedListener) {
        this.onItemSelectedListener = OnItemSelectedListener;
    }

    public final void setAdapter(WheelAdapter adapter) {
        this.adapter = adapter;
        remeasure();
        invalidate();
    }

    public final WheelAdapter getAdapter() {
        return adapter;
    }

    public final int getCurrentItem() {
        return selectedItem;
    }

    protected final void onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed(new OnItemSelectedRunnable(this), 200L);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (adapter == null) {
            return;
        }
        Object visibles[] = new Object[itemsVisible];
        //滾動的Y值高度除去每行Item的高度，得到滾動了多少個item，即change的次數
        change = (int) (totalScrollY / itemHeight);
        try {
            //滾動中實際的item(即經過了中間位置的item) ＝ 滑動前的位置 ＋ 滑動相對位置
            preCurrentIndex = initPosition + change % adapter.getItemsCount();
        } catch (ArithmeticException e) {
            System.out.println("出錯了！adapter.getItemsCount() == 0，數據不匹配");
        }
        if (!isLoop) {//不循環的情况
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0;
            }
            if (preCurrentIndex > adapter.getItemsCount() - 1) {
                preCurrentIndex = adapter.getItemsCount() - 1;
            }
        } else {//循環
            if (preCurrentIndex < 0) {
                preCurrentIndex = adapter.getItemsCount() + preCurrentIndex;
            }
            if (preCurrentIndex > adapter.getItemsCount() - 1) {
                preCurrentIndex = preCurrentIndex - adapter.getItemsCount();
            }
        }

        int itemHeightOffset = (int) (totalScrollY % itemHeight);
        // 設置群組中每個元素的值
        int counter = 0;
        while (counter < itemsVisible) {
            int index = preCurrentIndex - (itemsVisible / 2 - counter);//索引值，即現在在物件中間的item看作資料源的中間，計算出相對資料源的index值

            //判斷是否循還
            if (isLoop) {
                index = getLoopMappingIndex(index);
                visibles[counter] = adapter.getItem(index);
            } else if (index < 0) {
                visibles[counter] = "";
            } else if (index > adapter.getItemsCount() - 1) {
                visibles[counter] = "";
            } else {
                visibles[counter] = adapter.getItem(index);
            }

            counter++;

        }

        //中間兩條横線
        canvas.drawLine(0.0F, firstLineY, measuredWidth, firstLineY, paintIndicator);
        canvas.drawLine(0.0F, secondLineY, measuredWidth, secondLineY, paintIndicator);
        if (label != null) {
            int drawRightContentStart = measuredWidth - getTextWidth(paintCenterText, label);
            //靠右並留出空隙
            canvas.drawText(label, drawRightContentStart - CENTERCONTENTOFFSET, centerY, paintCenterText);
        }
        counter = 0;
        while (counter < itemsVisible) {
            canvas.save();
            // L(弧長)=α（弧度）* r(半徑)
            // 求弧度--> (L * π ) / (π * r)   (弧長*pi/半圓周長)
            float itemHeight = maxTextHeight * lineSpacingMultiplier;
            double radian = ((itemHeight * counter - itemHeightOffset) * Math.PI) / halfCircumference;
            // 弧度轉換成角度(把半圓以Y軸為軸心向右轉90度，使其處於第一象限及第四象限
            float angle = (float) (90D - (radian / Math.PI) * 180D);
            // 九十度以上的不繪制
            if (angle >= 90F || angle <= -90F) {
                canvas.restore();
            } else {


                String contentText = getContentText(visibles[counter]);

                //開始繪制的位置
                measuredCenterContentStart(contentText);
                measuredOutContentStart(contentText);
                float translateY = (float) (radius - Math.cos(radian) * radius - (Math.sin(radian) * maxTextHeight) / 2D);
                canvas.translate(0.0F, translateY);
                canvas.scale(1.0F, (float) Math.sin(radian));
                if (translateY <= firstLineY && maxTextHeight + translateY >= firstLineY) {
                    // 項目經過第一條線
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, firstLineY - translateY);
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, drawOutContentStart, maxTextHeight, paintOuterText);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, firstLineY - translateY, measuredWidth, (int) (itemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * 1F);
                    canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTERCONTENTOFFSET, paintCenterText);
                    canvas.restore();
                } else if (translateY <= secondLineY && maxTextHeight + translateY >= secondLineY) {
                    // 經過第二條線
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, secondLineY - translateY);
                    canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                    canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTERCONTENTOFFSET, paintCenterText);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, secondLineY - translateY, measuredWidth, (int) (itemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, drawOutContentStart, maxTextHeight, paintOuterText);
                    canvas.restore();
                } else if (translateY >= firstLineY && maxTextHeight + translateY <= secondLineY) {
                    // 中間項目
                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                    canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTERCONTENTOFFSET, paintCenterText);
                    int preSelectedItem = adapter.indexOf(visibles[counter]);
                    if (preSelectedItem != -1) {
                        selectedItem = preSelectedItem;
                    }
                } else {
                    // 其他項目
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, drawOutContentStart, maxTextHeight, paintOuterText);
                    canvas.restore();
                }
                canvas.restore();
            }
            counter++;
        }
    }

    private int getLoopMappingIndex(int index) {
        if (index < 0) {
            index = index + adapter.getItemsCount();
            index = getLoopMappingIndex(index);
        } else if (index > adapter.getItemsCount() - 1) {
            index = index - adapter.getItemsCount();
            index = getLoopMappingIndex(index);
        }
        return index;
    }


    //item 資料源的item
   //return 對應顯示的字串
    private String getContentText(Object item) {
        if (item == null) {
            return "";
        }
        else if (item instanceof IPickerViewData) {
            return ((IPickerViewData) item).getPickerViewText();
        }
        return item.toString();
    }

    private void measuredCenterContentStart(String content) {
        Rect rect = new Rect();
        paintCenterText.getTextBounds(content, 0, content.length(), rect);
        switch (mGravity) {
            case Gravity.CENTER:
                drawCenterContentStart = (int) ((measuredWidth - rect.width()) * 0.5);
                break;
            case Gravity.LEFT:
                drawCenterContentStart = 0;
                break;
            case Gravity.RIGHT:
                drawCenterContentStart = measuredWidth - rect.width();
                break;
        }
    }

    private void measuredOutContentStart(String content) {
        Rect rect = new Rect();
        paintOuterText.getTextBounds(content, 0, content.length(), rect);
        switch (mGravity) {
            case Gravity.CENTER:
                drawOutContentStart = (int) ((measuredWidth - rect.width()) * 0.5);
                break;
            case Gravity.LEFT:
                drawOutContentStart = 0;
                break;
            case Gravity.RIGHT:
                drawOutContentStart = measuredWidth - rect.width();
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.widthMeasureSpec = widthMeasureSpec;
        remeasure();
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                cancelFuture();
                previousY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float dy = previousY - event.getRawY();
                previousY = event.getRawY();
                totalScrollY = (int) (totalScrollY + dy);

                // 邊界。
                if (!isLoop) {
                    float top = -initPosition * itemHeight;
                    float bottom = (adapter.getItemsCount() - 1 - initPosition) * itemHeight;
                    if (totalScrollY - itemHeight * 0.3 < top) {
                        top = totalScrollY - dy;
                    } else if (totalScrollY + itemHeight * 0.3 > bottom) {
                        bottom = totalScrollY - dy;
                    }

                    if (totalScrollY < top) {
                        totalScrollY = (int) top;
                    } else if (totalScrollY > bottom) {
                        totalScrollY = (int) bottom;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            default:
                if (!eventConsumed) {
                    float y = event.getY();
                    double l = Math.acos((radius - y) / radius) * radius;
                    int circlePosition = (int) ((l + itemHeight / 2) / itemHeight);

                    float extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight;
                    mOffset = (int) ((circlePosition - itemsVisible / 2) * itemHeight - extraOffset);

                    if ((System.currentTimeMillis() - startTime) > 120) {
                        // 拖曳事件
                        smoothScroll(ACTION.DAGGLE);
                    } else {
                        // 點擊事件
                        smoothScroll(ACTION.CLICK);
                    }
                }
                break;
        }
        invalidate();

        return true;
    }


    public int getItemsCount() {
        return adapter != null ? adapter.getItemsCount() : 0;
    }


    public void setLabel(String label) {
        this.label = label;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}