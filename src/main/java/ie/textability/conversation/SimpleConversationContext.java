package ie.textability.conversation;

import java.util.ArrayList;
import java.util.List;

public class SimpleConversationContext implements ConversationContext {

    List<Transcript> transcripts;

    public SimpleConversationContext() {
        transcripts = new ArrayList<>();
    }

    @Override
    public List<Transcript> getTranscripts() {
        return transcripts;
    }

    @Override
    public void addTranscript(Transcript transcript) {
        transcripts.add(transcript);
    }
}
