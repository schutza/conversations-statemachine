package ie.textability.dialog.conversation;

import java.util.HashMap;
import java.util.Map;

public class InMemoryConversationStorage implements ConversationStorage {

    private static volatile InMemoryConversationStorage instance;

    private Map<String, Conversation> storage;

    private InMemoryConversationStorage() {
        storage = new HashMap<>();
    }

    public static InMemoryConversationStorage getInstance() {
        if (instance == null) {
            synchronized (InMemoryConversationStorage.class) {
                if (instance == null) {
                    instance = new InMemoryConversationStorage();
                }
            }
        }
        return instance;
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
