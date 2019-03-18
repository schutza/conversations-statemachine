package ie.textability;


import ie.textability.dialog.conversation.Conversation;
import ie.textability.dialog.conversation.InMemoryConversationStorage;
import ie.textability.dialog.state.DialogState;
import ie.textability.dialog.state.StateMachineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayDeque;
import java.util.Queue;

@SpringBootApplication
public class MyDialogAppWithFactory implements CommandLineRunner {

    @Autowired
    private StateMachineFactory factory;

    public static void main(String[] args) {
        SpringApplication.run(MyDialogAppWithFactory.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        Queue<String> conversations = new ArrayDeque<>();

        // buildStateMachine some state machines
        for(int i = 0; i < 5; ++i) {
            Conversation conversation = new Conversation(InMemoryConversationStorage.getInstance());

            conversations.add(conversation.getId());
            System.out.println("Added to Queue: "+ conversation.getId()+"]");
        }

        // let them run until they all finish
        int reInsertions = 0;
        while (!conversations.isEmpty()) {

            String conversationId = conversations.poll();
            Conversation conversation = Conversation.find(conversationId);
            //
            conversation.addTranscript("Hello");
            //
            if (!conversation.getState().equals(DialogState.FINISHED)) {
                System.out.println("Reinserted to Queue: ["+ conversation.getId() +"] at state "+ conversation.getState() +
                    "]");
                ++reInsertions;
                conversations.add(conversation.getId());
            }
        }

        System.out.println("Finished with "+ reInsertions + " re-insertions");

    }

}
