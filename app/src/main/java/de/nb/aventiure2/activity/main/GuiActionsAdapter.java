package de.nb.aventiure2.activity.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.nb.aventiure2.R;

public class GuiActionsAdapter extends RecyclerView.Adapter<GuiActionsAdapter.ViewHolder> {
    private final List<GuiAction> guiActions = new ArrayList<>();
    private final LayoutInflater inflater;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button actionButton;

        private ViewHolder(final View itemView) {
            super(itemView);
            actionButton = itemView.findViewById(R.id.actionView);
        }
    }

    GuiActionsAdapter(final Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public GuiActionsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                           final int viewType) {
        final FrameLayout v = (FrameLayout) inflater.inflate(R.layout.action, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final GuiAction current = guiActions.get(position);
        holder.actionButton.setText(current.getName());
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
