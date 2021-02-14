package de.nb.aventiure2.activity.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.nb.aventiure2.activity.main.viewmodel.GuiAction;
import de.nb.aventiure2.databinding.ActionBinding;

public class GuiActionsAdapter extends RecyclerView.Adapter<GuiActionsAdapter.ViewHolder> {
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
        holder.actionButton.setText(
                "Den Wald verlassen und in den Schlossgarten gehen"
                // FIXME current.getDisplayName()
        );

        /* FIXME Verschiedene Formen / Farben
        final String colorName = current.getActionType();
        final int colorResourceId =
                holder.actionButton.getResources().getIdentifier(
                        colorName, "color",
                        holder.actionButton.getContext().getPackageName());

        holder.actionButton.setBackgroundColor(
                ContextCompat.getColor(holder.actionButton.getContext(), colorResourceId));
         */
        holder.actionButton.setOnClickListener(v -> current.execute());
    }

    @Override
    public int getItemCount() {
        return guiActions.size();
    }

    void setGuiActions(final List<GuiAction> guiActions) {
        this.guiActions.clear();
        this.guiActions.addAll(guiActions);
        notifyDataSetChanged();
    }
}
