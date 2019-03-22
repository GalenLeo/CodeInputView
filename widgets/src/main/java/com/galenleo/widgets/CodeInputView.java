package com.galenleo.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.galenleo.widgets.utils.KeyBoardUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by liuguansheng on 2018/10/20.
 */
public class CodeInputView extends View {

    private int mWidth;
    private int mHeight;
    //the code builder
    private StringBuilder codeBuilder;
    //the paint to draw text
    private Paint textPaint;

    private OnTextChangListener listener;

    private int itemWidth;
    private int gapWidth;
    private int testSize;
    private int textColor = Color.CYAN;
    //how many words to show
    private int itemCount = 4;
    @InputType
    private int inputType = INPUT_TYPE_TEXT;
    private Drawable itemBackground;

    private PointF[] itemPoints;

    public static final int INPUT_TYPE_NUMBER = 0;
    public static final int INPUT_TYPE_TEXT = 1;
    public static final int INPUT_TYPE_TEXT_CAP_CHARACTERS = 2;
    public static final int INPUT_TYPE_PASSWORD = 3;
    public static final int INPUT_TYPE_NUMBER_PASSWORD = 4;

    @IntDef({INPUT_TYPE_NUMBER, INPUT_TYPE_TEXT, INPUT_TYPE_TEXT_CAP_CHARACTERS, INPUT_TYPE_PASSWORD, INPUT_TYPE_NUMBER_PASSWORD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface InputType {
    }

    private boolean softInputEnable = true;

    public CodeInputView(Context context) {
        super(context);
        init(context, null);
    }

    public CodeInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CodeInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CodeInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CodeInputView);

            itemWidth = typedArray.getDimensionPixelSize(R.styleable.CodeInputView_ciItemWidth, -1);
            gapWidth = typedArray.getDimensionPixelSize(R.styleable.CodeInputView_ciGapWidth, 10);
            itemBackground = typedArray.getDrawable(R.styleable.CodeInputView_ciItemBackground);

            testSize = typedArray.getDimensionPixelSize(R.styleable.CodeInputView_ciTextSize, 24);
            textColor = typedArray.getColor(R.styleable.CodeInputView_ciTextColor, textColor);
            itemCount = typedArray.getInt(R.styleable.CodeInputView_ciItemCount, itemCount);
            if (itemCount < 2) throw new IllegalArgumentException("item count must more than 1!");
            inputType = typedArray.getInt(R.styleable.CodeInputView_ciInputType, INPUT_TYPE_TEXT);
            softInputEnable = typedArray.getBoolean(R.styleable.CodeInputView_ciSoftInputEnable, softInputEnable);

            typedArray.recycle();
        }
        if (codeBuilder == null)
            codeBuilder = new StringBuilder();

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(testSize);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        setFocusableInTouchMode(true); // allows the keyboard to pop up on
        // touch down
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (softInputEnable) {
            requestFocus();//must have focus to show the keyboard
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                KeyBoardUtil.showKeyboard(getContext(), this);
            }
            return true;
        }
        return false;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        //define keyboard to number keyboard
        BaseInputConnection fic = new BaseInputConnection(this, false);
        outAttrs.actionLabel = null;
        switch (inputType) {
            case INPUT_TYPE_NUMBER:
                outAttrs.inputType = android.text.InputType.TYPE_CLASS_NUMBER;
                break;
            case INPUT_TYPE_TEXT:
                outAttrs.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                break;
            case INPUT_TYPE_TEXT_CAP_CHARACTERS:
                outAttrs.inputType = android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                break;
            case INPUT_TYPE_PASSWORD:
                outAttrs.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;
            case INPUT_TYPE_NUMBER_PASSWORD:
                outAttrs.inputType = android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD;
                break;
        }
        outAttrs.imeOptions = EditorInfo.IME_ACTION_NEXT;
        return fic;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (softInputEnable) {
            if (codeBuilder == null) codeBuilder = new StringBuilder();
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                deleteLast();
            } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                appendText(String.valueOf(event.getNumber()));
            } else if (((inputType == INPUT_TYPE_TEXT || inputType == INPUT_TYPE_TEXT_CAP_CHARACTERS || inputType == INPUT_TYPE_PASSWORD))
                    && keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
                String text = String.valueOf((char) event.getUnicodeChar());
                appendText(inputType == INPUT_TYPE_TEXT_CAP_CHARACTERS ? text.toUpperCase() : text);
            }
            //hide soft keyboard
            if (codeBuilder.length() >= itemCount || keyCode == KeyEvent.KEYCODE_ENTER) {
                KeyBoardUtil.hideKeyboard(getContext(), this);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (itemWidth <= 0) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            itemWidth = (mWidth - gapWidth * (itemCount - 1)) / itemCount;
        } else {
            mWidth = itemWidth * itemCount + gapWidth * (itemCount - 1);
        }
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        calculateStartAndEndPoint(itemCount);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        if (codeBuilder == null) return;
        int inputLength = codeBuilder.length();
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        //text's vertical center is view's center
        int baseLine = mHeight / 2 + (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;

        for (int i = 0; i < itemCount; i++) {
            if (itemBackground != null) {
                itemBackground.setBounds((int) itemPoints[i].x, 0, (int) itemPoints[i].y, mHeight);
                itemBackground.setState(i == inputLength ? FOCUSED_STATE_SET : EMPTY_STATE_SET);
                itemBackground.draw(canvas);
            }
        }

        for (int i = 0; i < inputLength; i++) {
            drawText(canvas, i, baseLine);
        }

    }

    private void drawText(Canvas canvas, int i, int baseLine) {
        if (inputType == INPUT_TYPE_PASSWORD || inputType == INPUT_TYPE_NUMBER_PASSWORD)
            canvas.drawCircle(itemPoints[i].y - itemWidth / 2, mHeight / 2, testSize / 4, textPaint);
        else
            canvas.drawText(codeBuilder.toString(), i, i + 1, itemPoints[i].y - itemWidth / 2, baseLine, textPaint);
    }

    /**
     * get verify code string
     *
     * @return code
     */
    public String getText() {
        return codeBuilder != null ? codeBuilder.toString() : "";
    }

    /**
     * set verify code (must less than 4 letters)
     *
     * @param code code
     */
    public void setText(String code) {
        if (code == null)
            throw new NullPointerException("Code must not null!");
        if (code.length() > itemCount) {
            throw new IllegalArgumentException("Code must less than " + itemCount + " letters!");
        }
        codeBuilder = new StringBuilder();
        codeBuilder.append(code);
        invalidate();
    }

    public interface OnTextChangListener {
        void afterTextChanged(String text);
    }

    /**
     * calculate every points
     *
     * @param textCount code length
     */
    private void calculateStartAndEndPoint(int textCount) {
        itemPoints = new PointF[textCount];
        for (int i = 0; i < textCount; i++) {
            itemPoints[i] = new PointF(i * gapWidth + i * itemWidth, i * gapWidth + (i + 1) * itemWidth);
        }
    }

    public void setListener(OnTextChangListener listener) {
        this.listener = listener;
    }

    public boolean isComplete() {
        return codeBuilder != null && codeBuilder.length() == getItemCount();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(@ColorRes int textColor) {
        this.textColor = textColor;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        if (itemCount < 2) throw new IllegalArgumentException("item count must more than 1!");
        this.itemCount = itemCount;
    }

    public void setSoftInputEnable(boolean softInputEnable) {
        this.softInputEnable = softInputEnable;
    }

    public void deleteLast() {
        if (codeBuilder == null || codeBuilder.length() == 0)
            return;
        codeBuilder.deleteCharAt(codeBuilder.length() - 1);
        if (listener != null) {
            listener.afterTextChanged(codeBuilder.toString());
        }
        invalidate();
    }

    public void appendText(String text) {
        if (codeBuilder == null) codeBuilder = new StringBuilder();
        if (codeBuilder.length() >= itemCount) return;
        codeBuilder.append(text);
        if (listener != null) {
            listener.afterTextChanged(codeBuilder.toString());
        }
        invalidate();
    }
}
