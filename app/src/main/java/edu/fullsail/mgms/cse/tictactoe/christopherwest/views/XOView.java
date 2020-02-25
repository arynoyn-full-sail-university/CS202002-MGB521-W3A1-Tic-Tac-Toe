package edu.fullsail.mgms.cse.tictactoe.christopherwest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;

import edu.fullsail.mgms.cse.tictactoe.christopherwest.activities.GameActivity;

public class XOView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private GameActivity mGame;
    private SurfaceHolder mHolder;
    private byte mValue;
    private Paint mPaintToken;
    private Paint mPaintText;
    private Rect mFieldDimensions;
    private int mLocation;
    // A thread where the painting activities are taking place
    private Thread mThread;

    // A flag which controls the start and stop of the repainting of the SurfaceView
    private boolean mFlag = false;


    public XOView(Context context) {
        super(context);
        initialize(context);
    }

    public XOView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public XOView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setWillNotDraw(false);
        getHolder().addCallback(this);
        setOnTouchListener(this);

        mGame = (GameActivity)context;
        mFieldDimensions = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
        mPaintText = new Paint();
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setColor(Color.RED);
        mPaintToken = new Paint();
        mPaintToken.setStyle(Paint.Style.FILL);
        mPaintToken.setColor(Color.BLUE);
        canvas.drawRect(mFieldDimensions, fillPaint);
        // if (mGame.getmGameBoard()[mLocation] == (byte)'o') {
            canvas.drawCircle(mFieldDimensions.width() / 2, mFieldDimensions.height() / 2, Math.min(mFieldDimensions.width(), mFieldDimensions.height()) / 2 - 10 * 2, mPaintText);
        // } else if (mGame.getmGameBoard()[mLocation] == (byte)'x') {
            canvas.drawCircle(mFieldDimensions.width() / 2, mFieldDimensions.height() / 2, Math.min(mFieldDimensions.width(), mFieldDimensions.height()) / 2 - 10 * 2, mPaintToken);
        // }
        super.onDraw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        Canvas c = mHolder.lockCanvas();
        if (c != null) {
            mFieldDimensions.set(0,0, c.getWidth(), c.getHeight());
        }
        mHolder.unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mLocation = mGame.getmXOViews().indexOf(v);
        mValue = mGame.getmGameBoard()[mLocation];

        select();
        Canvas c = mHolder.lockCanvas();
        draw(c);
        mHolder.unlockCanvasAndPost(c);
        mGame.endTurn(mLocation, mValue);
        return super.onTouchEvent(event);
    }

    public void select() {
        if (mValue == 0){
            byte[] newBoard = mGame.getmGameBoard();
            newBoard[mLocation] = mGame.ismIsPlayerX() ? (byte)'x' : (byte)'o';
            mGame.setmGameBoard(newBoard);
        }
    }
}
