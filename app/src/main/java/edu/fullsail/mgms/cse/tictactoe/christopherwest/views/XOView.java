package edu.fullsail.mgms.cse.tictactoe.christopherwest.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;

import edu.fullsail.mgms.cse.tictactoe.christopherwest.R;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.activities.GameActivity;

public class XOView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private GameActivity mGame;
    private SurfaceHolder mHolder;
    private byte mValue;
    private Paint mPaintX;
    private Paint mPaintO;
    private Paint mPaintFill;
    private Rect mFieldDimensions;
    private int mLocation;
    private Bitmap mBMPPlayerX;
    private Bitmap mBMPPlayerO;

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
        // setWillNotDraw(false);
        getHolder().addCallback(this);
        setOnTouchListener(this);

        mGame = (GameActivity)context;
        mFieldDimensions = new Rect();

        mPaintFill = new Paint();
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(Color.WHITE);

        mPaintO = new Paint();
        mPaintO.setStyle(Paint.Style.FILL);
        mPaintO.setColor(Color.RED);

        mPaintX = new Paint();
        mPaintX.setStyle(Paint.Style.FILL);
        mPaintX.setColor(Color.BLUE);

        mBMPPlayerX = BitmapFactory.decodeResource(context.getResources(), R.drawable.tictactoe_x);
        mBMPPlayerO = BitmapFactory.decodeResource(context.getResources(), R.drawable.tictactoe_o);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mFieldDimensions, mPaintFill);
        if (mValue == mGame.getOValue()) {
            canvas.drawBitmap(mBMPPlayerO, null, mFieldDimensions, null);
        } else if (mValue == mGame.getXValue()) {
            canvas.drawBitmap(mBMPPlayerX, null, mFieldDimensions, null);
        }
        super.onDraw(canvas);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        Canvas c = mHolder.lockCanvas();
        if (c != null) {
            mFieldDimensions.set(0,0, c.getWidth(), c.getHeight());
        }
        mLocation = mGame.getmXOViews().indexOf(this);
        mValue = mGame.getmGameBoard()[mLocation];
        onDraw(c);
        mHolder.unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mHolder = holder;
        mFieldDimensions.set(0,0, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mValue = mGame.getCurrentPlayer().getPlayerToken();
        mLocation = mGame.getmXOViews().indexOf(v);
        if(mGame.getmGameBoard()[mLocation] == mGame.getEmptySpaceValue()) {
            mGame.getCurrentPlayer().move(mGame.getmGameBoard(), mLocation);
            Canvas c = mHolder.lockCanvas();
            onDraw(c);
            mHolder.unlockCanvasAndPost(c);
            mGame.endTurn(mLocation);
        }
        return super.onTouchEvent(event);
    }

    public void select() {
        mValue = mGame.getCurrentPlayer().getPlayerToken();
        mLocation = mGame.getmXOViews().indexOf(this);
        if(mGame.getmGameBoard()[mLocation] == mGame.getEmptySpaceValue()) {
            mGame.getCurrentPlayer().move(mGame.getmGameBoard(), mLocation);
            Canvas c = mHolder.lockCanvas();
            onDraw(c);
            mHolder.unlockCanvasAndPost(c);
            mGame.endTurn(mLocation);
        }
    }
}
