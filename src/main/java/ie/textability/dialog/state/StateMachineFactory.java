package ie.textability.dialog.state;

import ie.textability.dialog.DialogEvent;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class StateMachineFactory {

    private static final double INTENT_CONFIRMATION_THRESHOLD = 0.6;
    private static final Random random = new Random();

    public static StateMachine<DialogState, DialogEvent> buildStateMachine() throws Exception {
        StateMachineBuilder.Builder<DialogState, DialogEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
            .withConfiguration()
            .autoStartup(true)
            .listener(new DialogStateMachineListener());


        builder.configureStates()
            .withStates()
            .initial(DialogState.ANY_INPUT)
            .junction(DialogState.EVALUATE_INPUT)
            .state(DialogState.ASK_CONFIRMATION)
            .junction(DialogState.EVALUATE_CONFIRMATION)
            .state(DialogState.SLOT_FILLING)
            .junction(DialogState.EVALUATE_SLOT)
            .end(DialogState.FINISHED);

        builder.configureTransitions()
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

        StateMachine<DialogState, DialogEvent> stateMachine = builder.build();
        return stateMachine;
    }


    public static Guard<DialogState, DialogEvent> isYesAnswer() {
//        return ctx -> (boolean) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("answerYes", false);
        return ctx -> {
            return random.nextBoolean();
        };
    }

    public static Guard<DialogState, DialogEvent> isNoAnswer() {
//        return ctx -> (boolean) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("answerNo", false);
        return ctx -> {
            return random.nextBoolean();
        };
    }

    public static Guard<DialogState, DialogEvent> intentPredictedWithHighConfidence() {
//        return ctx -> (double) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("confidence", 0d) >= INTENT_CONFIRMATION_THRESHOLD;
        return ctx -> {
            return random.nextDouble() >= INTENT_CONFIRMATION_THRESHOLD;
        };
    }

    public static Guard<DialogState, DialogEvent> intentPredictedWithLowConfidence() {
//        return ctx -> (double) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("confidence", 0d) < INTENT_CONFIRMATION_THRESHOLD;
        return ctx -> {
            return random.nextDouble() < INTENT_CONFIRMATION_THRESHOLD;
        };
    }

    public static Guard<DialogState, DialogEvent> intentPredictedWithNoSlotsToFill() {
//        return ctx -> (int) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("slotsToFill", 0) > 0;
        return ctx -> {
            return slotsToFill();
        };
    }

    public static Guard<DialogState, DialogEvent> intentPredictedWithSlotsToFill() {
//        return ctx -> (int) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("slotsToFill", 0) > 0;
        return ctx -> {
            return slotsToFill();
        };
    }

    public static Guard<DialogState, DialogEvent> noSlotsToFill() {
//        return ctx -> (int) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("slotsToFill", 0) == 0;
        return ctx -> {
            return !slotsToFill();
        };
    }

    public static Guard<DialogState, DialogEvent> guardSlotsToFill() {
//        return ctx -> (int) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("slotsToFill", 0) > 0;
        return ctx -> {
            return slotsToFill();
        };
    }

    public static Guard<DialogState, DialogEvent> isSlotInputUnderstood() {
//        return ctx -> (boolean) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("slotValueUnderstood", false);
        return ctx -> {
            return random.nextBoolean();
        };
    }

    public static Guard<DialogState, DialogEvent> isNotUnderstood() {
//        return ctx -> (boolean) ctx.getExtendedState()
//            .getVariables()
//            .getOrDefault("understood", false);
        return ctx -> {
            return random.nextBoolean();
        };
    }

    private static boolean slotsToFill() {
        return random.nextInt(3) > 0;
    }

}
