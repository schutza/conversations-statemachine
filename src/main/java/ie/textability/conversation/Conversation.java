package ie.textability.conversation;

import ie.textability.dialog.DialogEvent;
import ie.textability.dialog.state.DialogState;
import ie.textability.dialog.state.StateMachineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;

import java.util.UUID;


public class Conversation {
    private String id;
    private StateMachine<DialogState, DialogEvent> stateMachine;
    private Transcript currentTranscript;
    private ConversationContext conversationContext;


    private ConversationRepositoryCustom conversationRepository;

    private static Logger LOGGER = LoggerFactory.getLogger(Conversation.class.getName());

    public Conversation(ConversationRepositoryCustom conversationRepository) throws Exception {
        this.conversationRepository = conversationRepository;
        this.conversationContext = new SimpleConversationContext();
        //
        id = UUID.randomUUID().toString();
        stateMachine = StateMachineFactory.buildStateMachine();
        stateMachine.start();
        //
        LOGGER.info("Created Conversation [{}] with state [{}]", id, getState() );
        save();
    }

    private Conversation(ConversationRepositoryCustom conversationRepository, boolean noop) {
        this.conversationRepository = conversationRepository;
    }


    public String getId() {
        return id;
    }

    public DialogState getState() {
        return stateMachine.getState().getId();
    }

    public ConversationContext getContext() {
        return conversationContext;
    }

    public void addTranscript(String text) {
        currentTranscript = Transcript.makeTranscript(text);
        conversationContext.addTranscript(currentTranscript);
        stateMachine.sendEvent(DialogEvent.NL_INPUT);
        save();
    }

    public void save() {
        conversationRepository.save(this);
    }

    public static Conversation find(ConversationRepositoryCustom repository, String id) {
        LOGGER.info("Retrieving conversation [{}]", id);
        return new Conversation(repository, false).conversationRepository.findOne(id);
    }


}
