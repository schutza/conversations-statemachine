package ie.textability.conversation;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryConversationRepository implements ConversationRepositoryCustom {

    private Map<String, Conversation> storage;

    public InMemoryConversationRepository() {
        storage = new HashMap<>();
    }

    @Override
    public Conversation findOne(String id) {
        return storage.get(id);
    }

    @Override
    public void save(Conversation conversation) {
        String id = conversation.getId();
        storage.put(id, conversation);
    }
}
