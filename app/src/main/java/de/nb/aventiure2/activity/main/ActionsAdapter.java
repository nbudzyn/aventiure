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
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

public class ActionsAdapter extends RecyclerView.Adapter<ActionsAdapter.ViewHolder> {
    private final List<AbstractPlayerAction> playerActions = new ArrayList<>();
    private final LayoutInflater inflater;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button actionButton;

        private ViewHolder(final View itemView) {
            super(itemView);
            actionButton = itemView.findViewById(R.id.actionView);
        }
    }

    ActionsAdapter(final Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ActionsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                        final int viewType) {
        final FrameLayout v = (FrameLayout) inflater.inflate(R.layout.action, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AbstractPlayerAction current = playerActions.get(position);
        holder.actionButton.setText(current.getName());
        holder.actionButton.setOnClickListener(v -> current.execute());
    }

    @Override
    public int getItemCount() {
        return playerActions.size();
    }

    void setPlayerActions(final List<AbstractPlayerAction> playerActions) {
        this.playerActions.clear();
        this.playerActions.addAll(playerActions);
        notifyDataSetChanged();
    }
}
