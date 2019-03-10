package ie.textability.dialog.state;

import ie.textability.dialog.DialogEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.Random;

@Configuration
@EnableStateMachine
public class DialogStateMachineConfiguration extends StateMachineConfigurerAdapter<DialogState, DialogEvent> {

    private static final double INTENT_CONFIRMATION_THRESHOLD = 0.6;
    private static final Random random = new Random();

    @Override
    public void configure(StateMachineConfigurationConfigurer<DialogState, DialogEvent> config) throws Exception {
        config
            .withConfiguration()
            .autoStartup(true)
            .listener(new DialogStateMachineListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<DialogState, DialogEvent> states) throws Exception {

        states
            .withStates()
            .initial(DialogState.ANY_INPUT)
            .junction(DialogState.EVALUATE_INPUT)
            .state(DialogState.ASK_CONFIRMATION)
            .junction(DialogState.EVALUATE_CONFIRMATION)
            .state(DialogState.SLOT_FILLING)
            .junction(DialogState.EVALUATE_SLOT)
            .end(DialogState.FINISHED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<DialogState, DialogEvent> transitions) throws Exception {

        transitions
            .withExternal()
            .source(DialogState.ANY_INPUT)
            .target(DialogState.EVALUATE_INPUT)
            .event(DialogEvent.NL_INPUT)
            .and()
            // ANY_INPUT transitions
            .withJunction()
                .source(DialogState.EVALUATE_INPUT)
                .first(DialogState.ASK_CONFIRMATION, intentPredictedWithLowConfidence())
                .then(DialogState.SLOT_FILLING, intentPredictedWithSlotsToFill())
                .then(DialogState.FINISHED, intentPredictedWithNoSlotsToFill())
                .last(DialogState.ANY_INPUT) // not understood
            // INTENT_CONFIRMATION transitions
            .and()
            .withExternal()
            .source(DialogState.ASK_CONFIRMATION)
            .target(DialogState.EVALUATE_CONFIRMATION)
            .event(DialogEvent.NL_INPUT)
            .and()
            .withJunction()
                .source(DialogState.EVALUATE_CONFIRMATION)
                .first(DialogState.SLOT_FILLING, isYesAnswer())
                .then(DialogState.ANY_INPUT, isNoAnswer())
                .last(DialogState.ANY_INPUT) // not understood
            // SLOT_FILLING transitions
            .and()
            .withExternal()
            .source(DialogState.SLOT_FILLING)
            .target(DialogState.EVALUATE_SLOT)
            .event(DialogEvent.NL_INPUT)
            .and()
            .withJunction()
                .source(DialogState.EVALUATE_SLOT)
                .first(DialogState.FINISHED, noSlotsToFill())
                .then(DialogState.SLOT_FILLING, guardSlotsToFill())
                .last(DialogState.SLOT_FILLING); // not understood
    }

    @Bean
    public Guard<DialogState, DialogEvent> isYesAnswer() {
//        return ctx -> (boolean) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("answerYes", false);
        return ctx -> {
            return random.nextBoolean();
        };
    }

    @Bean
    public Guard<DialogState, DialogEvent> isNoAnswer() {
//        return ctx -> (boolean) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("answerNo", false);
        return ctx -> {
            return random.nextBoolean();
        };
    }

    @Bean
    public Guard<DialogState, DialogEvent> intentPredictedWithHighConfidence() {
//        return ctx -> (double) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("confidence", 0d) >= INTENT_CONFIRMATION_THRESHOLD;
        return ctx -> {
            return random.nextDouble() >= INTENT_CONFIRMATION_THRESHOLD;
        };
    }

    @Bean
    public Guard<DialogState, DialogEvent> intentPredictedWithLowConfidence() {
//        return ctx -> (double) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("confidence", 0d) < INTENT_CONFIRMATION_THRESHOLD;
        return ctx -> {
            return random.nextDouble() < INTENT_CONFIRMATION_THRESHOLD;
        };
    }

    @Bean
    public Guard<DialogState, DialogEvent> intentPredictedWithNoSlotsToFill() {
//        return ctx -> (int) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("slotsToFill", 0) > 0;
        return ctx -> {
            return slotsToFill();
        };
    }

    @Bean
    public Guard<DialogState, DialogEvent> intentPredictedWithSlotsToFill() {
//        return ctx -> (int) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("slotsToFill", 0) > 0;
        return ctx -> {
            return slotsToFill();
        };
    }

    @Bean
    public Guard<DialogState, DialogEvent> noSlotsToFill() {
//        return ctx -> (int) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("slotsToFill", 0) == 0;
        return ctx -> {
            return !slotsToFill();
        };
    }

    @Bean
    public Guard<DialogState, DialogEvent> guardSlotsToFill() {
//        return ctx -> (int) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("slotsToFill", 0) > 0;
        return ctx -> {
            return slotsToFill();
        };
    }

    @Bean
    public Guard<DialogState, DialogEvent> isSlotInputUnderstood() {
//        return ctx -> (boolean) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("slotValueUnderstood", false);
        return ctx -> {
            return random.nextBoolean();
        };
    }

    @Bean
    public Guard<DialogState, DialogEvent> isNotUnderstood() {
//        return ctx -> (boolean) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("understood", false);
        return ctx -> {
            return random.nextBoolean();
        };
    }

    private boolean slotsToFill() {
        return random.nextInt(3) > 0;
    }

}
