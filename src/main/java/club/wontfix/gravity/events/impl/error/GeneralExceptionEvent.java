package club.wontfix.gravity.events.impl.error;

import club.wontfix.gravity.events.Cancellable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class GeneralExceptionEvent extends Cancellable {

    @NonNull
    private final Exception exception;

    @Setter
    private int responseCode = 500;

    @Setter
    private String templateToRender = "error.html";

}
