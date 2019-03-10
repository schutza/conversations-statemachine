package ie.textability.dialog.state;

import ie.textability.dialog.DialogEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
@EnableStateMachine
public class DialogStateMachineConfiguration extends StateMachineConfigurerAdapter<DialogState, DialogEvent> {

    private static final double INTENT_CONFIRMATION_THRESHOLD = 0.6;

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
            .initial(DialogState.EXPECT_ANY_INPUT)
            .junction(DialogState.EXPECT_ANY_INPUT)
            .state(DialogState.EXPECT_CONFIRMATION)
            .junction(DialogState.EXPECT_CONFIRMATION)
            .state(DialogState.EXPECT_SLOT)
            .junction(DialogState.EXPECT_SLOT)
            .end(DialogState.FINISHED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<DialogState, DialogEvent> transitions) throws Exception {

        transitions
            .withExternal()
            .source(DialogState.EXPECT_ANY_INPUT)
            .target(DialogState.EXPECT_CONFIRMATION)
            .event(DialogEvent.NL_INPUT)
            //
            .and()
            .withExternal()
            .source(DialogState.EXPECT_ANY_INPUT)
            .target(DialogState.EXPECT_SLOT)
            .event(DialogEvent.NL_INPUT)
            //
            .and()
            .withExternal()
            .source(DialogState.EXPECT_ANY_INPUT)
            .target(DialogState.FINISHED)
            .event(DialogEvent.NL_INPUT)
            //
            .and()
            .withExternal()
            .source(DialogState.EXPECT_CONFIRMATION)
            .target(DialogState.EXPECT_SLOT)
            .event(DialogEvent.NL_INPUT)
            //
            .and()
            .withExternal()
            .source(DialogState.EXPECT_SLOT)
            .target(DialogState.EXPECT_SLOT)
            .event(DialogEvent.NL_INPUT)
            //
            .and()
            .withExternal()
            .source(DialogState.EXPECT_SLOT)
            .target(DialogState.FINISHED)
            .event(DialogEvent.NL_INPUT)
        ;
    }

    @Bean
    public Guard<DialogState, DialogEvent> isYesAnswer() {
        return ctx -> (boolean) ctx.getExtendedState()
            .getVariables()
            .getOrDefault("answerYes", false);
    }

    @Bean
    public Guard<DialogState, DialogEvent> isNoAnswer() {
        return ctx -> (boolean) ctx.getExtendedState()
            .getVariables()
            .getOrDefault("answerNo", false);
    }

    @Bean
    public Guard<DialogState, DialogEvent> intentPredictedWithHighConfidence() {
        return ctx -> (double) ctx.getExtendedState()
            .getVariables()
            .getOrDefault("confidence", 0) >= INTENT_CONFIRMATION_THRESHOLD;
    }

    @Bean
    public Guard<DialogState, DialogEvent> intentPredictedWithLowConfidence() {
        return ctx -> (double) ctx.getExtendedState()
            .getVariables()
            .getOrDefault("confidence", 0) < INTENT_CONFIRMATION_THRESHOLD;
    }

    @Bean
    public Guard<DialogState, DialogEvent> intentPredictedWithSlotsToFill() {
        return ctx -> (int) ctx.getExtendedState()
            .getVariables()
            .getOrDefault("slotsToFill", 0) > 0;
    }

    @Bean
    public Guard<DialogState, DialogEvent> isSlotInputUnderstood() {
        return ctx -> (boolean) ctx.getExtendedState()
            .getVariables()
            .getOrDefault("slotValueUnderstood", false);
    }

}
