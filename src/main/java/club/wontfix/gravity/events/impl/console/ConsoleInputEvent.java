package club.wontfix.gravity.events.impl.console;

import club.wontfix.gravity.events.Responseable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsoleInputEvent extends Responseable {

    @Getter @NonNull
    private final String command;

}
