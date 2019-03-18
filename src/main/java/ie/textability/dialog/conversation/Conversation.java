package ie.textability.dialog.conversation;

import ie.textability.dialog.DialogEvent;
import ie.textability.dialog.state.DialogState;
import ie.textability.dialog.state.StateMachineFactory;
import org.springframework.statemachine.StateMachine;

import java.util.UUID;

public class Conversation {
    private String id;
    private StateMachine<DialogState, DialogEvent> stateMachine;
    private static ConversationStorage storage;

    public Conversation(ConversationStorage storage) throws Exception {
        this.storage = storage;
        //
        id = UUID.randomUUID().toString();
        stateMachine = StateMachineFactory.buildStateMachine();
        stateMachine.start();
        //
        System.out.println("Created Conversation ["+ id +"] with state ["+ getState() +"]" );
        save();
    }

    public String getId() {
        return id;
    }

    public DialogState getState() {
        return stateMachine.getState().getId();
    }

    public void addTranscript(String text) {
        stateMachine.sendEvent(DialogEvent.NL_INPUT);
        save();
    }

    public void save() {
        storage.save(this);
    }

    public static Conversation find(String id) {
        System.out.println("Retrieving conversation ["+ id +"]");
        return storage.findOne(id);
    }


}
