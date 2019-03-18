package ie.textability.conversation;

import java.time.ZonedDateTime;

public class Transcript {

    private ZonedDateTime createdAt;
    private String utterance;

    private Transcript(String text) {
        utterance = text;
        createdAt = ZonedDateTime.now();
    }

    public static Transcript makeTranscript(String text) {
        return new Transcript(text);
    }

    public String getUtterance() {
        return utterance;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", createdAt, utterance);
    }
}
