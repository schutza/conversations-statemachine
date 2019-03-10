package ie.textability;


import ie.textability.dialog.DialogEvent;
import ie.textability.dialog.state.DialogState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

@SpringBootApplication
public class MyDialogApp implements CommandLineRunner {

    @Autowired
    private StateMachine<DialogState, DialogEvent> stateMachine;

    public static void main(String[] args) {
        SpringApplication.run(MyDialogApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        stateMachine.start();
        stateMachine.sendEvent(DialogEvent.NL_INPUT);
        stateMachine.sendEvent(DialogEvent.NL_INPUT);
        stateMachine.sendEvent(DialogEvent.NL_INPUT);
        System.out.println(stateMachine.getState());
    }

}
