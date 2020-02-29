package edu.fullsail.mgms.cse.tictactoe.christopherwest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.fullsail.mgms.cse.tictactoe.christopherwest.R;
import edu.fullsail.mgms.cse.tictactoe.christopherwest.enums.Winner;

public class OverActivity extends AppCompatActivity implements View.OnClickListener {

    private Winner mWinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over);

        //read startup params
        Bundle b = getIntent().getExtras();
        mWinner = Winner.values()[b.getInt("winner")];
        setWinnerText();
    }

    public void onClick(View view) {
        Intent i = new Intent(this, MenuActivity.class);
        startActivity(i);
        finish();
        return;
    }

    private void setWinnerText() {
        TextView boardSizeText = findViewById(R.id.over_winner_text);
        switch (mWinner) {
            case X:
                boardSizeText.setText(R.string.game_winner_player_x);
                break;
            case O:
                boardSizeText.setText(R.string.game_winner_player_o);
                break;
            case DRAW:
                boardSizeText.setText(R.string.game_winner_player_draw);
                break;
            default:
                boardSizeText.setText("Something has Gone Wrong!");
                break;
        }
    }
}
