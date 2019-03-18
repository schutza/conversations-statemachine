package ie.textability.conversation;

import java.util.List;

public interface ConversationContext {

    void addTranscript(Transcript transcript);

    List<Transcript> getTranscripts();

}
