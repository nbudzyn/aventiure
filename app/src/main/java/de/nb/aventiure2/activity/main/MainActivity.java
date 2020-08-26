package de.nb.aventiure2.activity.main;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Debug;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.BuildConfig;
import de.nb.aventiure2.R;
import de.nb.aventiure2.activity.main.viewmodel.MainViewModel;
import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.Walkthrough;

public class MainActivity extends AppCompatActivity {
    private static final Logger LOGGER = Logger.getLogger();

    private TextView narrationTextView;

    private ScrollView narrationScrollView;

    @Nullable
    private ObjectAnimator narrationScrollViewAnimator;

    private GuiActionsAdapter guiActionsAdapter;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO Use view binding
        narrationTextView = findViewById(R.id.narrationView);

        // TODO Blocksatz?
        // TODO Automatische Trennung?!
        narrationScrollView = findViewById(R.id.narrationScrollView);
        narrationTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                if (narrationScrollViewAnimator != null) {
                    narrationScrollViewAnimator.cancel();
                }

                return false;
            }
        });

        createActionsRecyclerView();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getNarration().observe(this,
                this::setNarrationAndScrollToBottom);
        mainViewModel.getGuiActions().observe(this,
                g -> guiActionsAdapter.setGuiActions(
                        g == null ? ImmutableList.of() : g));
    }

    private void setNarrationAndScrollToBottom(final String newText) {
        final String oldText =
                narrationTextView.getText() == null ? "" :
                        narrationTextView.getText().toString();

        final SpannableString ss = new SpannableString(newText);

        final ForegroundColorSpan fscOld = new ForegroundColorSpan(
                getResources().getColor(R.color.colorOldNarration, getTheme()));

        final ForegroundColorSpan fscNew = new ForegroundColorSpan(
                getResources().getColor(R.color.colorNewNarration, getTheme()));

        if (newText.startsWith(oldText)) {
            ss.setSpan(fscOld, 0, oldText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            ss.setSpan(fscNew, oldText.length(), newText.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            ss.setSpan(fscNew, 0, newText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        narrationTextView.setText(ss);
        final int scrollDuration = calcScrollDuration(oldText, newText);

        scrollToBottom(scrollDuration);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.walk_anfang_bis_froschversprechen:
                mainViewModel.walkActions(Walkthrough.ANFANG_BIS_FROSCHVERSPRECHEN);
                return true;
            case R.id.walk_anfang_bis_schlossfest_schloss_betreten:
                mainViewModel.walkActions(Walkthrough.ANFANG_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN);
                return true;
            case R.id.walk_anfang_bis_prinzabfahrt:
                mainViewModel.walkActions(Walkthrough.ANFANG_BIS_PRINZABFAHRT);
                return true;
            case R.id.walk_full:
                mainViewModel.walkActions(Walkthrough.FULL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static int calcScrollDuration(final CharSequence oldText,
                                          final CharSequence newText) {
        if (oldText.length() == 0) {
            return 0;
        }

        return (newText.length() - oldText.length()) * 5;
    }

    private void scrollToBottom(final int scrollDuration) {
        narrationScrollView.post(() -> {
            final int top = narrationTextView.getBottom() -
                    narrationScrollView.getHeight()
                    + narrationScrollView.getPaddingBottom();

            if (scrollDuration <= 0) {
                narrationScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            } else {
                narrationScrollViewAnimator = ObjectAnimator
                        .ofInt(narrationScrollView, "scrollY", top)
                        .setDuration(scrollDuration);
                narrationScrollViewAnimator.setAutoCancel(true);
                narrationScrollViewAnimator.start();
            }
        });
    }

    private void createActionsRecyclerView() {
        final RecyclerView actionsRecyclerView = findViewById(R.id.recyclerView);
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
                LOGGER.d(
                        "Keeping screen on for debugging, detach debugger and force an onResume to turn it off.");
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                LOGGER.d("Keeping screen on for debugging is now deactivated.");
            }
        }
    }
}
