package de.nb.aventiure2.activity.main;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.BuildConfig;
import de.nb.aventiure2.R;
import de.nb.aventiure2.activity.main.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private TextView storyTextView;
    private ScrollView storyTextScrollView;
    private RecyclerView actionsRecyclerView;
    private GuiActionsAdapter guiActionsAdapter;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storyTextView = findViewById(R.id.storyTextView);
        storyTextScrollView = findViewById(R.id.storyTextScrollView);

        createActionsRecyclerView();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getStoryText().observe(this,
                t -> {
                    final CharSequence oldText =
                            storyTextView.getText() == null ? "" : storyTextView.getText();

                    storyTextView.setText(t);

                    storyTextScrollView.post(() -> {
                        final int top = storyTextView.getBottom() -
                                storyTextScrollView.getHeight()
                                + storyTextScrollView.getPaddingBottom();

                        if (oldText.length() == 0) {
                            storyTextScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        } else {
                            final int duration =
                                    (t.length() - oldText.length()) * 5;
                            ObjectAnimator.ofInt(storyTextScrollView, "scrollY",
                                    top)
                                    .setDuration(duration)
                                    .start();
                        }
                    });
                }
        );
        mainViewModel.getGuiActions().observe(this,
                g -> guiActionsAdapter.setGuiActions(
                        g == null ? ImmutableList.of() : g));
    }

    private void createActionsRecyclerView() {
        actionsRecyclerView = findViewById(R.id.recyclerView);

        guiActionsAdapter = new GuiActionsAdapter(this);
        actionsRecyclerView.setAdapter(guiActionsAdapter);

        actionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Keep screen on while developing in Android Studio
        // See https://stackoverflow.com/questions/8840954/how-do-i-keep-my-screen-unlocked-during-usb-debugging
        if (BuildConfig.DEBUG) { // don't even consider it otherwise
            if (Debug.isDebuggerConnected()) {
                Log.d("SCREEN", "Keeping screen on for debugging, detach debugger and force an onResume to turn it off.");
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                Log.d("SCREEN", "Keeping screen on for debugging is now deactivated.");
            }
        }
    }
}
