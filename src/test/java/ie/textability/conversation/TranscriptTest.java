package ie.textability.conversation;

import org.junit.Test;

public class TranscriptTest {

    @Test
    public void testToString() {
        Transcript transcript = Transcript.makeTranscript("FooBarBaz");
        System.out.println(transcript);
    }
}
