package edu.fullsail.mgms.cse.tictactoe.christopherwest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

import edu.fullsail.mgms.cse.tictactoe.christopherwest.R;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.enums.GameDiff;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.enums.GameMode;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.enums.Winner;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.views.XOView;

public class GameActivity extends AppCompatActivity {

    private HashSet<ArrayList<Integer>> mWinConditions3x3;
    private HashSet<ArrayList<Integer>> mWinConditions4x4;

    public byte getXValue() {
        return X;
    }

    public byte getOValue() {
        return O;
    }

    private final byte X = (byte)'x';
    private final byte O = (byte)'o';
    private final long DELAY = 300;

    private GameMode mMode;
    private GameDiff mDiff;
    private byte[] mGameBoard;
    private boolean mIsPlayerX;
    private Vector<XOView> mXOViews;
    private TextView mPlayerTurnLbl;

    public GameMode getmMode() {
        return mMode;
    }

    public void setmMode(GameMode mMode) {
        this.mMode = mMode;
    }

    public GameDiff getmDiff() {
        return mDiff;
    }

    public void setmDiff(GameDiff mDiff) {
        this.mDiff = mDiff;
    }

    public byte[] getmGameBoard() {
        return mGameBoard;
    }

    public void setmGameBoard(byte[] mGameBoard) {
        this.mGameBoard = mGameBoard;
    }

    public boolean ismIsPlayerX() {
        return mIsPlayerX;
    }

    public void setmIsPlayerX(boolean mIsPlayerX) {
        this.mIsPlayerX = mIsPlayerX;
    }

    public Vector<XOView> getmXOViews() {
        return mXOViews;
    }

    public void setmXOViews(Vector<XOView> mXOViews) {
        this.mXOViews = mXOViews;
    }

    public TextView getmPlayerTurnLbl() {
        return mPlayerTurnLbl;
    }

    public void setmPlayerTurnLbl(TextView mPlayerTurnLbl) {
        this.mPlayerTurnLbl = mPlayerTurnLbl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mXOViews = new Vector<>();
        mGameBoard = new byte[9];
        mIsPlayerX = true;

        //read startup params
        Bundle b = getIntent().getExtras();
        mMode = GameMode.values()[b.getInt("mode")];
        mDiff = GameDiff.values()[b.getInt("diff")];

        Initialize3x3WinConditions();
        Initialize4x4WinConditions();


        findXOViews((ViewGroup)getWindow().getDecorView().getRootView(), mXOViews);
    }

    private void Initialize4x4WinConditions() {
        ArrayList<ArrayList<Integer>> list = new ArrayList<>();
        list.add(new ArrayList<>(Arrays.asList(1, 2, 3, 4)));
        list.add(new ArrayList<>(Arrays.asList(5, 6, 7, 8)));
        list.add(new ArrayList<>(Arrays.asList(9, 10, 11, 12)));
        list.add(new ArrayList<>(Arrays.asList(13, 14, 15, 16)));
        list.add(new ArrayList<>(Arrays.asList(1, 5, 9, 13)));
        list.add(new ArrayList<>(Arrays.asList(2, 6, 10, 14)));
        list.add(new ArrayList<>(Arrays.asList(3, 7, 11, 15)));
        list.add(new ArrayList<>(Arrays.asList(4, 8, 12, 16)));
        list.add(new ArrayList<>(Arrays.asList(1, 6, 11, 16)));
        list.add(new ArrayList<>(Arrays.asList(4, 7, 10, 13)));
        mWinConditions4x4 = new HashSet<>(list);
    }

    private void Initialize3x3WinConditions() {
        ArrayList<ArrayList<Integer>> list = new ArrayList<>();
        list.add(new ArrayList<>(Arrays.asList(1, 2, 3)));
        list.add(new ArrayList<>(Arrays.asList(4, 5, 6)));
        list.add(new ArrayList<>(Arrays.asList(7, 8, 9)));
        list.add(new ArrayList<>(Arrays.asList(1, 4, 7)));
        list.add(new ArrayList<>(Arrays.asList(2, 5, 8)));
        list.add(new ArrayList<>(Arrays.asList(3, 6, 9)));
        list.add(new ArrayList<>(Arrays.asList(1, 5, 9)));
        list.add(new ArrayList<>(Arrays.asList(3, 5, 7)));
        mWinConditions3x3 = new HashSet<>(list);
    }

    private void findXOViews(ViewGroup parent, Vector<XOView> list) {
        View child = null;
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            child = parent.getChildAt(i);
            if (child instanceof XOView) {
                list.add((XOView) child);
            } else if (child instanceof ViewGroup) {
                findXOViews((ViewGroup) child, list);
            }
        }
    }

    public void endTurn(int loc, byte val) {
        mGameBoard[loc] = val;
        Winner win = checkWin(mIsPlayerX, mGameBoard);
        if (win != Winner.TBD) {
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {}

            Intent i = new Intent(this, OverActivity.class);
            i.putExtra("winner", win.ordinal());
            startActivity(i);
            finish();
            return;
        }

        mIsPlayerX = !mIsPlayerX;

        setTurnLabel();
        // mPlayerTurnLbl.postInvalidate();

        // next move is computer turn
        if (mMode == GameMode.CVC || (mMode == GameMode.PVC && !mIsPlayerX)){
            new ComputerTurn().execute();
        }
    }

    private Winner checkWin(boolean mIsPlayerX, byte[] mGameBoard) {

        return Winner.TBD;
    }

    private void setTurnLabel() {
        // mPlayerTurnLbl.setText(mIsPlayerX ? "Player X" : "Player O");
    }

    private class Move {
        public int score;
        public int location;
    }

    private class ComputerTurn extends AsyncTask<Void, Void, Void> {
        private Move mBestMove;
        private long mStartTime;

        @Override
        protected void onPreExecute() {
            mStartTime = System.currentTimeMillis();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (mDiff) {
                case EASY:
                    int location;
                    while(true) {
                        location = (new Random()).nextInt(9);
                        if (mGameBoard[location] == 0) {
                            mBestMove = new Move();
                            mBestMove.location = location;
                            break;
                        }
                    }
                    break;
                case HARD:
                    mBestMove = minimax(mIsPlayerX, mGameBoard);
                    break;
                default: break;
            }

            long elapsed = Math.abs(System.currentTimeMillis() - mStartTime);
            if (elapsed < DELAY)
            {
                try {
                    Thread.sleep(DELAY-elapsed);
                } catch (Exception ex) {}
            }

            return null;
        }

        private Move minimax(boolean mIsPlayerX, byte[] mGameBoard) {
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            mXOViews.get(mBestMove.location).select();
        }
    }
}
