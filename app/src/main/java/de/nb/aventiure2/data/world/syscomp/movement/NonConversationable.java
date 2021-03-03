package de.nb.aventiure2.data.world.syscomp.movement;

public class NonConversationable implements IConversationable {
    @Override
    public boolean isInConversation() {
        return false;
    }
}
