package de.nb.aventiure2.activity.main;

import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.NORTH;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.SOUTH;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.nb.aventiure2.R;
import de.nb.aventiure2.activity.main.viewmodel.GuiAction;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.databinding.ActionBinding;

class GuiActionsAdapter extends RecyclerView.Adapter<GuiActionsAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button actionButton;

        private ViewHolder(final ActionBinding binding) {
            super(binding.getRoot());
            actionButton = binding.actionView;
        }
    }

    private final List<GuiAction> guiActions = new ArrayList<>();

    @NonNull
    @Override
    public GuiActionsAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                           final int viewType) {
        // See https://chetangupta.net/viewbinding/ .
        final ActionBinding binding =
                ActionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );
        return new ViewHolder(binding);
    }

    GuiActionsAdapter() {
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final GuiAction current = guiActions.get(position);
        holder.actionButton.setText(current.getDisplayName());

        @Nullable final CardinalDirection cardinalDirection = current.getCardinalDirection();
        holder.actionButton.setBackgroundResource(getDrawable(cardinalDirection));
        setPadding(holder, cardinalDirection);
        setColor(holder, current.getActionType());

        holder.actionButton.setOnClickListener(v -> current.execute());
    }

    private static void setColor(final ViewHolder holder, final String colorName) {
        final @ColorRes int colorResourceId =
                holder.actionButton.getResources().getIdentifier(
                        colorName, "color",
                        holder.actionButton.getContext().getPackageName());

        final @ColorInt int color = ContextCompat.getColor(holder.actionButton.getContext(),
                colorResourceId);

        final int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_enabled}
        };

        final @ColorInt int darkerColor = darken(color);
        final @ColorInt int[] colors = new int[]{
                darkerColor,
                darkerColor,
                color
        };

        final ColorStateList tintList = new ColorStateList(states, colors);
        holder.actionButton.setBackgroundTintList(tintList);
    }

    @ColorInt
    private static int darken(final @ColorInt int color) {
        return ColorUtils.blendARGB(color, Color.BLACK, 0.2f);
    }

    private static void setPadding(final ViewHolder holder,
                                   final CardinalDirection cardinalDirection) {
        final int paddingTop = cardinalDirection == NORTH ? dp(holder.actionButton, 24) :
                dp(holder.actionButton, 4);
        final int paddingLeft = holder.actionButton.getPaddingLeft();
        final int paddingRight = holder.actionButton.getPaddingRight();
        final int paddingBottom = cardinalDirection == SOUTH ? dp(holder.actionButton, 24) :
                dp(holder.actionButton, 4);

        holder.actionButton.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private static int dp(final View view, final int dp) {
        final float scale = view.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @DrawableRes
    private static int getDrawable(@Nullable final CardinalDirection cardinalDirection) {
        // IDEA Hier könnte man verschiedene
        //  Symbole für verschiedene Aktion(stypen)
        //  verwenden. Vielleicht Font Awesome verwenden?
        //  https://fontawesome.com/start
        //

        if (cardinalDirection == null) {
            return R.drawable.ic_button_arrow_none;
        }

        switch (cardinalDirection) {
            case NORTH:
                return R.drawable.ic_button_arrow_up;
            case EAST:
                return R.drawable.ic_button_arrow_right;
            case SOUTH:
                return R.drawable.ic_button_arrow_down;
            case WEST:
                return R.drawable.ic_button_arrow_left;
            default:
                throw new IllegalArgumentException(
                        "Unexpected cardinal direction: " + cardinalDirection);
        }
    }

    @Override
    public int getItemCount() {
        return guiActions.size();
    }

    void setGuiActions(final List<GuiAction> guiActions) {
        this.guiActions.clear();

        final ArrayList<GuiAction> tmp = new ArrayList<>(guiActions);

        tmp.sort((one, other) ->
                calcOrder(one.getCardinalDirection()) - calcOrder(other.getCardinalDirection()));

        this.guiActions.addAll(tmp);
        notifyDataSetChanged();
    }

    private static int calcOrder(@Nullable final CardinalDirection cardinalDirection) {
        if (cardinalDirection == null) {
            return 2;
        }
        switch (cardinalDirection) {
            case NORTH:
                return 0;
            case EAST:
                return 1;
            case WEST:
                return 3;
            case SOUTH:
                return 4;
            default:
                throw new IllegalArgumentException(
                        "Unexcepted cardinalDirection: " + cardinalDirection);
        }
    }
}
