package de.nb.aventiure2.activity.main;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Debug;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
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

        // TODO Use view binding
        storyTextView = findViewById(R.id.storyTextView);
        storyTextScrollView = findViewById(R.id.storyTextScrollView);

        createActionsRecyclerView();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getStoryText().observe(this,
                this::setStoryTextAndScrollToBottom);
        mainViewModel.getGuiActions().observe(this,
                g -> guiActionsAdapter.setGuiActions(
                        g == null ? ImmutableList.of() : g));
    }

    private void setStoryTextAndScrollToBottom(final String newText) {
        final String oldText =
                storyTextView.getText() == null ? "" :
                        storyTextView.getText().toString();

        final SpannableString ss = new SpannableString(newText);

        final ForegroundColorSpan fscOld = new ForegroundColorSpan(
                getResources().getColor(R.color.colorOldStoryText, getTheme()));

        final ForegroundColorSpan fscNew = new ForegroundColorSpan(
                getResources().getColor(R.color.colorNewStoryText, getTheme()));

        if (newText.startsWith(oldText)) {
            ss.setSpan(fscOld, 0, oldText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            ss.setSpan(fscNew, oldText.length(), newText.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            ss.setSpan(fscNew, 0, newText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        storyTextView.setText(ss);
        final int scrollDuration = calcScrollDuration(oldText, newText);

        scrollToBottom(scrollDuration);
    }

    private static int calcScrollDuration(final CharSequence oldText,
                                          final CharSequence newText) {
        if (oldText.length() == 0) {
            return 0;
        }

        return (newText.length() - oldText.length()) * 5;
    }

    private void scrollToBottom(final int scrollDuration) {
        storyTextScrollView.post(() -> {
            final int top = storyTextView.getBottom() -
                    storyTextScrollView.getHeight()
                    + storyTextScrollView.getPaddingBottom();

            if (scrollDuration <= 0) {
                storyTextScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            } else {
                ObjectAnimator.ofInt(storyTextScrollView, "scrollY",
                        top)
                        .setDuration(scrollDuration)
                        .start();
            }
        });
    }

    private void createActionsRecyclerView() {
        actionsRecyclerView = findViewById(R.id.recyclerView);
        actionsRecyclerView.addItemDecoration(
                new VerticalSpaceItemDecoration(convertDpToPixel(24)));

        guiActionsAdapter = new GuiActionsAdapter(this);
        actionsRecyclerView.setAdapter(guiActionsAdapter);

        actionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public int convertDpToPixel(final float dp) {
        return Math.round(
                dp * ((float) getResources().getDisplayMetrics().densityDpi /
                        DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Keep screen on while developing in Android Studio
        // See https://stackoverflow.com/questions/8840954/how-do-i-keep-my-screen-unlocked-during-usb-debugging
        if (BuildConfig.DEBUG) { // don't even consider it otherwise
            if (Debug.isDebuggerConnected()) {
                Log.d("SCREEN",
                        "Keeping screen on for debugging, detach debugger and force an onResume to turn it off.");
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                Log.d("SCREEN", "Keeping screen on for debugging is now deactivated.");
            }
        }
    }
}
