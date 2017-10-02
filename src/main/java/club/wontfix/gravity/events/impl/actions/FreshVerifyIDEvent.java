package club.wontfix.gravity.events.impl.actions;

import club.wontfix.gravity.events.Cancellable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FreshVerifyIDEvent extends Cancellable {

    @NonNull
    private final String name;

    @NonNull
    private final String verifyID;

    @NonNull
    private final boolean dev;

}
