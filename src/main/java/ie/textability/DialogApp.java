package ie.textability;


import ie.textability.conversation.Conversation;
import ie.textability.conversation.ConversationRepositoryCustom;
import ie.textability.dialog.state.DialogState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayDeque;
import java.util.Queue;

@SpringBootApplication(scanBasePackages = "ie.textability")
public class DialogApp implements CommandLineRunner {

    private static Logger LOGGER = LoggerFactory.getLogger(DialogApp.class.getName());

    @Autowired
    private ConversationRepositoryCustom conversationRepository;

    public static void main(String[] args) {
        SpringApplication.run(DialogApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        Queue<String> myConversations = new ArrayDeque<>();

        // buildStateMachine some state machines
        for(int i = 0; i < 5; ++i) {
            Conversation conversation = new Conversation(conversationRepository);
            myConversations.add(conversation.getId());
            LOGGER.info("Added to Queue: [{}]", conversation.getId());
        }

        // let them run until they all finish
        int reInsertions = 0;
        while (!myConversations.isEmpty()) {

            String conversationId = myConversations.poll();
            Conversation conversation = Conversation.find(conversationRepository, conversationId);
            //
            conversation.addTranscript("Hello");
            //
            if (!conversation.getState().equals(DialogState.FINISHED)) {
                LOGGER.info("Reinserted to Queue: [{}] at state [{}]",
                    conversation.getId(), conversation.getState());
                ++reInsertions;
                myConversations.add(conversation.getId());
            } else {
                LOGGER.info("Conversation finished: [{}] - Transcripts: {}",
                    conversation.getId(), conversation.getContext().getTranscripts());
            }
        }

        LOGGER.info("Application finished with [{}] re-insertions", reInsertions);

    }

}
