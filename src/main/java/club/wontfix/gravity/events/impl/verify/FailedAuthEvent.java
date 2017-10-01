package club.wontfix.gravity.events.impl.verify;

import club.wontfix.gravity.json.VerifyResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ro.pippo.core.route.RouteContext;

@RequiredArgsConstructor
public class FailedAuthEvent {

    @Getter
    @NonNull
    private final RouteContext requestContext;

    @Getter
    @NonNull
    private final VerifyResponse response;

}
