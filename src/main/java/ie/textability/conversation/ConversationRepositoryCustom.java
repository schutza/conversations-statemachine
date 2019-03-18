package ie.textability.conversation;

public interface ConversationRepositoryCustom {

    Conversation findOne(String id);

    void save(Conversation conversation);

}
