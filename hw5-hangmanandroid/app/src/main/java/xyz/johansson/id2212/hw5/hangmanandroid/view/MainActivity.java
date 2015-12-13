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

    public void onConnectionButtonClicked(View view) {
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

    public void onGameButtonClicked(View view) {
        eventHandler.guessWord(((EditText) findViewById(R.id.game_guess)).getText().toString());
    }

    public void onGameCreateView(GameFragment gameFragment) {
        setGameState((GameState) gameFragment.getArguments().get("initialGameState"));
    }

    public void onGameDestroyView() {
        eventHandler.disconnect();
    }

    public void setGameButton(final boolean enabled) {
        handler.post(new Runnable() {
            public void run() {
                ((Button) findViewById(R.id.game_button)).setEnabled(enabled);
            }
        });
    }

    public void setGameInfo(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.game_info)).setText('\n' + text);
            }
        });
    }

    public void setGameState(GameState gameState) {
        setGameImage(gameState.getMisses());
        setGameWord(gameState.getWord());
    }

    private void setGameImage(final int state) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int drawableId = R.drawable.class.getField("hangman" + state).getInt(null);
                    ((ImageView) findViewById(R.id.game_image)).setImageResource(drawableId);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setGameWord(final String word) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.game_word)).setText(word);
            }
        });
    }
}
