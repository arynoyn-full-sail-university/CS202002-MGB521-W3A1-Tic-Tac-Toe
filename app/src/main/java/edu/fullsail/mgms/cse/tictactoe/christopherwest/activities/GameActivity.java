package edu.fullsail.mgms.cse.tictactoe.christopherwest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import edu.fullsail.mgms.cse.tictactoe.christopherwest.R;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.classes.PlayerStats;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.enums.GameDiff;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.enums.GameMode;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.enums.Winner;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.views.XOView;

public class GameActivity extends AppCompatActivity {

    private HashSet<ArrayList<Integer>> mWinConditions3x3;
    private HashSet<ArrayList<Integer>> mWinConditions4x4;
    private int mBoardSize = 3;
    private int mNumberOfMoves = 0;
    private final byte X = (byte)'x';
    private final byte O = (byte)'o';
    private final long DELAY = 300;
    private GameMode mMode;
    private GameDiff mDiff;
    private byte[] mGameBoard;
    private Vector<XOView> mXOViews;
    private TextView mPlayerTurnLbl;
    private ArrayList<PlayerStats> mPlayerStats;
    private PlayerStats mCurrentPlayer;

    public byte getXValue() {return X;}
    public byte getOValue() {
        return O;
    }
    public byte getEmptySpaceValue() {return 0;}
    public byte[] getmGameBoard() {
        return mGameBoard;
    }
    public boolean isPlayerX() {
        return getCurrentPlayer().getPlayerToken() == getXValue();
    }
    public Vector<XOView> getmXOViews() {
        return mXOViews;
    }
    public int viewReadyCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mXOViews = new Vector<>();
        //read startup params
        Bundle b = getIntent().getExtras();
        mMode = GameMode.values()[b.getInt("mode")];
        mDiff = GameDiff.values()[b.getInt("diff")];
        mBoardSize = b.getInt("board_size");

        mGameBoard = new byte[mBoardSize*mBoardSize];
        Arrays.fill(mGameBoard, getEmptySpaceValue());
        mPlayerStats = new ArrayList<>(Arrays.asList(new PlayerStats(mBoardSize, getXValue()),
                                                     new PlayerStats(mBoardSize, getOValue())));

        InitializeGameLayout();
        mCurrentPlayer = mPlayerStats.get(0);
        setTurnLabel();
        findXOViews((ViewGroup)getWindow().getDecorView().getRootView(), mXOViews);
        if (mMode == GameMode.CVC) {
            new ComputerTurn().execute();
        }
    }

    private void InitializeGameLayout() {
        LinearLayout layout = findViewById(R.id.layoutGameActivity);
        layout.removeAllViews();
        for (int i = 0; i < mBoardSize; i++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            layout.addView(rowLayout);
            for (int j = 0; j < mBoardSize; j++) {
                XOView cell = new XOView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                // code for scaling from dips to pixels found at
                // https://stackoverflow.com/questions/2238883/what-is-the-correct-way-to-specify-dimensions-in-dip-from-java-code
                int pixelValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                params.setMargins(pixelValue, pixelValue, pixelValue, pixelValue);
                cell.setLayoutParams(params);
                rowLayout.addView(cell);
            }
        }
        mPlayerTurnLbl = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        mPlayerTurnLbl.setLayoutParams(params);
        mPlayerTurnLbl.setText("Player X");
        mPlayerTurnLbl.setAllCaps(true);
        mPlayerTurnLbl.setTextColor(Color.WHITE);
        mPlayerTurnLbl.setTextSize(28);
        mPlayerTurnLbl.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mPlayerTurnLbl.setGravity(View.TEXT_ALIGNMENT_CENTER);

        layout.addView(mPlayerTurnLbl);
    }

    private PlayerStats getNextPlayer() {
        return getNextPlayer(mCurrentPlayer);
    }
    private PlayerStats getNextPlayer(PlayerStats player) {
        return mPlayerStats.get(mPlayerStats.indexOf(player) == 0 ? 1 : 0);
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

    public void endTurn(int boardIndex) {
        mCurrentPlayer = getCurrentPlayer();
        this.mNumberOfMoves++;
        Winner win = checkWin(mCurrentPlayer, boardIndex);
        if (win != Winner.TBD) {
            if (mMode != GameMode.PVP) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                }
            }
            passToGameOverScreen(win);
            return;
        }

        mCurrentPlayer = getNextPlayer();

        setTurnLabel();
        mPlayerTurnLbl.postInvalidate();

        // next move is computer turn
        if (mMode == GameMode.CVC || (mMode == GameMode.PVC && !isPlayerX())){
            new ComputerTurn().execute();
        }
    }

    private void passToGameOverScreen(Winner win) {
        Intent i = new Intent(this, OverActivity.class);
        i.putExtra("winner", win.ordinal());
        startActivity(i);
        finish();
    }

    public PlayerStats getCurrentPlayer() {
        return mCurrentPlayer;
    }

    private Winner checkWin(PlayerStats currentPlayer, int boardIndex) {
        Winner returnWinner = currentPlayer.hasPlayerWon(boardIndex)
                ? currentPlayer.getPlayerToken() == getXValue()
                    ? Winner.X
                    : Winner.O
                : Winner.TBD;

        if (returnWinner == Winner.TBD && mNumberOfMoves == (mBoardSize * mBoardSize)) {
            returnWinner = Winner.DRAW;
        }

        return returnWinner;
    }

    private Winner checkBoardState(byte[] board) {
        for (int i = 0; i < mBoardSize; i++) {
            if(board[getBoardIndex(i,0)] == getXValue()
                && board[getBoardIndex(i,1)] == getXValue()
                && board[getBoardIndex(i,2)] == getXValue()) {
                    return Winner.X;
            }
            if(board[getBoardIndex(i,0)] == getOValue()
                    && board[getBoardIndex(i,1)] == getOValue()
                    && board[getBoardIndex(i,2)] == getOValue()) {
                return Winner.O;
            }
        }

        for (int j = 0; j < mBoardSize; j++) {
            if(board[getBoardIndex(0,j)] == getXValue()
                    && board[getBoardIndex(1,j)] == getXValue()
                    && board[getBoardIndex(2,j)] == getXValue()) {
                return Winner.X;
            }
            if(board[getBoardIndex(0,j)] == getOValue()
                    && board[getBoardIndex(1,j)] == getOValue()
                    && board[getBoardIndex(2,j)] == getOValue()) {
                return Winner.O;
            }
        }

        boolean x_diagwin = true;
        for (int i = 0; i < mBoardSize; i++) {
            if (board[getBoardIndex(i,i)] != getXValue()) {
                x_diagwin = false;
                break;
            }
        }
        if (x_diagwin) { return Winner.X;}

        boolean o_diagwin = true;
        for (int i = 0; i < mBoardSize; i++) {
            if (board[getBoardIndex(i, i)] != getOValue()) {
                o_diagwin = false;
                break;
            }
        }
        if (o_diagwin) { return Winner.O;}

        x_diagwin = true;
        for (int i = 0; i < mBoardSize; i++) {
            if (board[getBoardIndex(i, (mBoardSize - 1) - i)] != getXValue()) {
                x_diagwin = false;
                break;
            }
        }
        if (x_diagwin) { return Winner.X;}

        o_diagwin = true;
        for (int i = 0; i < mBoardSize; i++) {
            if (board[getBoardIndex(i, (mBoardSize - 1) - i)] != getOValue()) {
                o_diagwin = false;
                break;
            }
        }
        if (o_diagwin) { return Winner.O;}

        boolean gameOver = true;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == getEmptySpaceValue()){
                gameOver = false;
                break;
            }
        }

        return gameOver ? Winner.DRAW : Winner.TBD;
    }

    private void setTurnLabel() {
        mPlayerTurnLbl.setText(isPlayerX() ? "Player X" : "Player O");
    }
    private int getBoardIndex(int row, int column) {
        return (row * mBoardSize) + (column % mBoardSize);
    }


    private class Move {
        public int score;
        public int location;

        public Move(int score, int location) {
            this.score = score;
            this.location = location;
        }
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
                    int boardIndex;
                    while(true) {
                        boardIndex = (new Random()).nextInt(mBoardSize * mBoardSize);
                        if (mGameBoard[boardIndex] == 0) {
                            mBestMove = new Move(-1, boardIndex);
                            break;
                        }
                    }
                    break;
                case HARD:
                    boolean isPlayerX = mCurrentPlayer.getPlayerToken() == getXValue();
                    mBestMove = getBestMove(isPlayerX, mGameBoard);
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

        private Move getBestMove(boolean isPlayerX, byte[] gameBoard) {
            boolean gameStarted = false;
            for (int i = 0; i < gameBoard.length; i++) {
                if (gameBoard[i] != getEmptySpaceValue()){
                    gameStarted = true;
                    break;
                }
            }


            int bestVal = isPlayerX ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            if(!gameStarted) {
                return new Move(bestVal, 0);
            }
            Move bestMove = new Move(bestVal, -1);

            for (int i = 0; i < gameBoard.length; i++) {
                if (gameBoard[i] == getEmptySpaceValue()) {
                    gameBoard[i] = isPlayerX ? getXValue() : getOValue();
                    int moveVal = minimax(!isPlayerX, gameBoard, 0);
                    gameBoard[i] = getEmptySpaceValue();
                    if ((isPlayerX && moveVal > bestVal) || (!isPlayerX && moveVal < bestVal)) {
                        bestMove.location = i;
                        bestVal = moveVal;
                    }
                }
            }
            bestMove.score = bestVal;
            return bestMove;
        }

        private int minimax(boolean isPlayerX, byte[] gameBoard, int depth) {
            Winner winner = checkBoardState(gameBoard);
            if (winner != Winner.TBD) {
                return getScoredMove(winner, depth);
            }

            int bestScore = isPlayerX ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            for (int i = 0; i < gameBoard.length; i++) {
                if (gameBoard[i] != getEmptySpaceValue()) {
                    continue; //Space is already filled
                }
                gameBoard[i] = isPlayerX ? getXValue() : getOValue();
                int minmax = minimax(!isPlayerX, gameBoard,depth + 1);
                gameBoard[i] = getEmptySpaceValue();
                bestScore = isPlayerX ? Math.max(bestScore, minmax ) : Math.min(bestScore, minmax);
            }
            return bestScore;
        }

        @Override
        protected void onPostExecute(Void unused) {
            mXOViews.get(mBestMove.location).select();
        }

        private int getScoredMove(Winner winner, int depth) {
            int score = winner == Winner.DRAW ?
                    0 :
                    winner == Winner.O
                            ? -(mBoardSize*mBoardSize) + depth
                            : (mBoardSize*mBoardSize) - depth;
            return score;
        }
    }
}
