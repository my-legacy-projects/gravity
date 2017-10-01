package club.wontfix.gravity.events.impl.verify;

import club.wontfix.gravity.easy.User;
import club.wontfix.gravity.events.Cancellable;
import club.wontfix.gravity.json.VerifyRequest;
import club.wontfix.gravity.json.VerifyResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ro.pippo.core.route.RouteContext;

@RequiredArgsConstructor
public class SuccessAuthEvent extends Cancellable {

    @Getter
    @NonNull
    private final RouteContext context;

    @Getter
    @NonNull
    private final VerifyRequest request;

    @Getter
    @NonNull
    private final VerifyResponse response;

    @Getter
    @NonNull
    private final User user;

}
