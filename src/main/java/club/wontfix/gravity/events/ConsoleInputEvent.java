package club.wontfix.gravity.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ConsoleInputEvent {

    @Getter @NonNull
    public final String command;

    @Getter @Setter
    private String response;

}
