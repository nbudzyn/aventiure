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

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.BuildConfig;
import de.nb.aventiure2.R;
import de.nb.aventiure2.activity.main.viewmodel.MainViewModel;
import de.nb.aventiure2.databinding.MainActivityBinding;
import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.Walkthrough;

public class MainActivity extends AppCompatActivity {
    // IDEA "Mein Märchen" "Schneepunzel"? -> Appstore?

    // IDEA Was sollte man in den AppStore schreiben?
    //  - Choose your own Adventure?
    //  - Abenteuer-Spielbuch?
    //  - In den (Grimmschen) Märchen
    //  - Märchen deiner Kindheit?
    //  - Rollenspiel
    //  - Nostalgie

    // TODO Symbole für Bewegungsrichtungen (wie auf einer Karte) links am Rand der Buttons
    //  Wie auf einer Karte von oben...

    // IDEA Weitere Symbole für grobe Typen von Aktionen (Gegenstände / Reden...)

    private static final Logger LOGGER = Logger.getLogger();

    private MainActivityBinding binding;

    private TextView narrationTextView;

    private ScrollView narrationScrollView;

    @Nullable
    private ObjectAnimator narrationScrollViewAnimator;

    private GuiActionsAdapter guiActionsAdapter;
    // TODO Text der Geschichte teilen (share)?

    private MainViewModel mainViewModel;
    private Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        narrationTextView = binding.narrationView;

        // TODO Blocksatz?
        // TODO Automatische Trennung?!
        narrationScrollView = binding.narrationScrollView;
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

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getScore().observe(this,
                this::setScore);
        mainViewModel.getNarration().observe(this,
                this::setNarrationAndScrollToBottom);
        mainViewModel.getGuiActions().observe(this,
                g -> guiActionsAdapter.setGuiActions(
                        g == null ? ImmutableList.of() : g));
    }

    private void setScore(final int score) {
        toolbar.setSubtitle(score + "%");
    }

    private void setNarrationAndScrollToBottom(final String newText) {
        final String oldText =
                narrationTextView.getText() == null ? "" :
                        narrationTextView.getText().toString();

        final int oldNarrationTextViewBottom = narrationTextView.getBottom();
        final int oldScrollY = narrationScrollView.getScrollY();
        final int oldTextHeight =
                Math.max(
                        narrationTextView.getLineHeight() * narrationTextView.getLineCount(),
                        0);
        final boolean narrationTextViewWasScrolledToBottom =
                !narrationScrollView.canScrollVertically(1);

        narrationTextView.setText(buildNarrationSpannableString(newText, oldText));

        narrationScrollView.post(() -> {
            int scrollAmount;
            final int targetScrollY;
            if (narrationTextViewWasScrolledToBottom) {
                scrollAmount =
                        narrationTextView.getBottom() - oldNarrationTextViewBottom;
                final int maxScroll =
                        Math.min(narrationScrollView.getHeight()
                                        - 2 * narrationScrollView.getPaddingBottom(),
                                oldTextHeight)
                                // weil noch Text der aktuellen Zeile hinzugefügt werden könnte
                                - narrationTextView.getLineHeight();

                if (scrollAmount > maxScroll) {
                    scrollAmount = maxScroll;
                }

                targetScrollY = oldScrollY + scrollAmount;
            } else {
                //  Wenn wir ohnehin schon nicht am Ende waren, scrollen wir bis zum
                // Ende durch.
                targetScrollY = narrationTextView.getBottom() -
                        narrationScrollView.getHeight()
                        + narrationScrollView.getPaddingBottom();
                scrollAmount = targetScrollY - narrationScrollView.getScrollY();
            }

            if (scrollAmount > 0) {
                final int scrollDuration;
                if (narrationTextViewWasScrolledToBottom) {
                    scrollDuration = calcScrollDuration(
                            scrollAmount / narrationScrollView.getPaddingBottom());
                } else {
                    scrollDuration = 300;
                }

                narrationScrollViewAnimator = ObjectAnimator
                        .ofInt(narrationScrollView, "scrollY", targetScrollY)
                        .setDuration(scrollDuration);
                narrationScrollViewAnimator.setAutoCancel(true);
                narrationScrollViewAnimator.start();
            }
        });
    }

    private static int calcScrollDuration(final float scrollRatio) {
        if (scrollRatio <= 0) {
            return 0;
        }

        return (int) (100 * scrollRatio);
    }

    @NonNull
    private SpannableString buildNarrationSpannableString(final String newText,
                                                          final String oldText) {
        final SpannableString ss = new SpannableString(newText);

        final ForegroundColorSpan fscOld = buildForegroundColorSpan(R.color.colorOldNarration);
        final ForegroundColorSpan fscNew = buildForegroundColorSpan(R.color.colorNewNarration);

        if (newText.startsWith(oldText)) {
            ss.setSpan(fscOld, 0, oldText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            ss.setSpan(fscNew, oldText.length(), newText.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            ss.setSpan(fscNew, 0, newText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return ss;
    }

    @NonNull
    private ForegroundColorSpan buildForegroundColorSpan(
            @ColorRes final int p) {
        return new ForegroundColorSpan(getResources().getColor(p, getTheme()));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuCompat.setGroupDividerEnabled(menu, true);

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
            case R.id.walk_anfang_bis_rapunzelruf:
                mainViewModel.walkActions(Walkthrough.ANFANG_BIS_ERSTE_RAPUNZELRUF_BEOBACHTUNG);
                return true;
            case R.id.walk_anfang_bis_oben_bei_rapunzel:
                mainViewModel.walkActions(Walkthrough.ANFANG_BIS_OBEN_BEI_RAPUNZEL);
                return true;
            case R.id.walk_full:
                mainViewModel.walkActions(Walkthrough.FULL);
                return true;
            case R.id.walk_sep_1_rapunzel_hoeren_und_in_den_wald:
                mainViewModel.walkActions(Walkthrough.SEP_1_RAPUNZEL_HOEREN_UND_IN_DEN_WALD);
                return true;
            case R.id.walk_sep_1_bis_oben_bei_rapunzel:
                mainViewModel.walkActions(Walkthrough.SEP_1_BIS_OBEN_BEI_RAPUNZEL);
                return true;
            case R.id.walk_sep_1_nur_rapunzel:
                mainViewModel.walkActions(Walkthrough.SEP_1_NUR_RAPUNZEL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createActionsRecyclerView() {
        final RecyclerView actionsRecyclerView = binding.recyclerView;
        actionsRecyclerView.addItemDecoration(
                new VerticalSpaceItemDecoration(convertDpToPixel(24)));

        guiActionsAdapter = new GuiActionsAdapter();
        actionsRecyclerView.setAdapter(guiActionsAdapter);

        actionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into
     *           pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    private int convertDpToPixel(final float dp) {
        return Math.round(
                dp * ((float) getResources().getDisplayMetrics().densityDpi /
                        DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Keep screen on while developing in Android Studio
        // See https://stackoverflow.com/questions/8840954/how-do-i-keep-my-screen-unlocked
        // -during-usb-debugging
        if (BuildConfig.DEBUG) { // don't even consider it otherwise
            if (Debug.isDebuggerConnected()) {
                LOGGER.d(
                        "Keeping screen on for debugging, detach debugger and force an onResume "
                                + "to turn it off.");
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                LOGGER.d("Keeping screen on for debugging is now deactivated.");
            }
        }
    }
}
