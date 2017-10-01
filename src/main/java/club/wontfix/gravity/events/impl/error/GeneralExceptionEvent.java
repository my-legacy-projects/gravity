package club.wontfix.gravity.events.impl.error;

import club.wontfix.gravity.events.Cancellable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class GeneralExceptionEvent extends Cancellable {

    @Getter
    @NonNull
    private final Exception exception;

    @Getter
    @Setter
    private int responseCode = 500;

    @Getter
    @Setter
    private String templateToRender = "error.html";

}
