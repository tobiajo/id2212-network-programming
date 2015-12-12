package xyz.johansson.id2212.hw5.hangmanandroid.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.johansson.id2212.hw1.hangman.server.GameState;
import xyz.johansson.id2212.hw5.hangmanandroid.R;

public class GameFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setHangmanState((GameState) getArguments().get("initialGameState"));
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
