package xyz.johansson.id2212.hw5.hangmanandroid.view;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.johansson.id2212.hw1.hangman.server.GameState;
import xyz.johansson.id2212.hw5.hangmanandroid.R;
import xyz.johansson.id2212.hw5.hangmanandroid.presenter.EventHandler;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private EventHandler eventHandler;

    //-------------------------------------------------------------------------
    // Default methods
    //-------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        eventHandler = new EventHandler(this);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            ConnectionFragment connectionFragment = new ConnectionFragment();
            connectionFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, connectionFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //-------------------------------------------------------------------------
    // Connection methods
    //-------------------------------------------------------------------------

    public void onConnectClicked(View view) {
        eventHandler.connectionAttempt(((EditText) findViewById(R.id.connection_ip)).getText().toString());
    }

    public void setConnectionButton(final boolean enabled) {
        handler.post(new Runnable() {
            public void run() {
                ((Button) findViewById(R.id.connection_button)).setEnabled(enabled);
            }
        });
    }

    public void setConnectionInfo(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.connection_info)).setText('\n' + text);
            }
        });
    }

    //-------------------------------------------------------------------------
    // Game methods
    //-------------------------------------------------------------------------

    public void onGuessClicked(View view) {
        eventHandler.guessWord(((EditText) findViewById(R.id.hangman_guess)).getText().toString());
    }

    public void onHangmanBackClicked() {
        eventHandler.disconnect();
    }

    public void setHangmanButton(final boolean enabled) {
        handler.post(new Runnable() {
            public void run() {
                ((Button) findViewById(R.id.hangman_button)).setEnabled(enabled);
            }
        });
    }

    public void setHangmanInfo(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.hangman_info)).setText('\n' + text);
            }
        });
    }

    public void setHangmanState(GameState gameState) {
        setHangmanImage(gameState.getMisses());
        setHangmanWord(gameState.getWord());
    }

    private void setHangmanImage(final int state) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int drawableId = R.drawable.class.getField("hangman" + state).getInt(null);
                    ((ImageView) findViewById(R.id.hangman_image)).setImageResource(drawableId);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setHangmanWord(final String word) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.hangman_word)).setText(word);
            }
        });
    }
}
