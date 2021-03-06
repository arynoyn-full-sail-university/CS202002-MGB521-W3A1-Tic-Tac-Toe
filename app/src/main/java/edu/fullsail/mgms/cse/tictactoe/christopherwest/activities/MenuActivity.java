package edu.fullsail.mgms.cse.tictactoe.christopherwest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.fullsail.mgms.cse.tictactoe.christopherwest.R;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.enums.GameDiff;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.enums.GameMode;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private GameDiff mDifficulty;
    private int mBoardSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mDifficulty = GameDiff.EASY;
        setDifficultyText();
        mBoardSize = 3;
        setBoardSizeText();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.menu_option_PVP:
                passToGameActivity(GameMode.PVP);
                break;
            case R.id.menu_option_PVC:
                if (mBoardSize > 3 && mDifficulty != GameDiff.EASY)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Difficulty Mode not Available for Board Size");
                    dialog.setMessage("Player vs Computer Mode is only available on Easy mode for boards larger than 3x3 due to the exponential time complexity");
                    dialog.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    passToGameActivity(GameMode.PVC);
                }
                break;
            case R.id.menu_option_CVC:
                if (mBoardSize > 3 && mDifficulty != GameDiff.EASY)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Difficulty Mode not Available for Board Size");
                    dialog.setMessage("Computer vs Computer Mode is only available on Easy mode for boards larger than 3x3 due to the exponential time complexity");
                    dialog.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    passToGameActivity(GameMode.CVC);
                }
                break;
            case R.id.menu_option_difficulty:
                mDifficulty = mDifficulty == GameDiff.EASY ? GameDiff.HARD : GameDiff.EASY;
                setDifficultyText();
                break;
            case R.id.menu_option_board_size:
                mBoardSize = mBoardSize == 3 ? 4 : 3;
                setBoardSizeText();
                break;
            case R.id.menu_option_credits:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Made by");
                dialog.setMessage("Christopher West\nMBG521-O | Computer Science for Engineers\n02/24/2020");
                dialog.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;

        }
    }

    private void passToGameActivity(GameMode mode) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("mode", mode.ordinal());
        i.putExtra("diff", mDifficulty.ordinal());
        i.putExtra("board_size", mBoardSize);
        startActivity(i);
        finish();
    }

    private void setDifficultyText() {
        TextView difficultyText = findViewById(R.id.menu_option_difficulty);
        difficultyText.setText(mDifficulty == GameDiff.EASY ? R.string.game_option_difficulty_easy : R.string.game_option_difficulty_hard);
    }

    private void setBoardSizeText() {
        TextView boardSizeText = findViewById(R.id.menu_option_board_size);
        boardSizeText.setText(mBoardSize == 3 ? R.string.game_option_3x3 : R.string.game_option_4x4);
    }
}
