package ie.textability.dialog.conversation;

public interface ConversationStorage {

    Conversation findOne(String id);

    void save(Conversation conversation);

}
