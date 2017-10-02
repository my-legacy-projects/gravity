package club.wontfix.gravity.events.impl.verify;

import club.wontfix.gravity.json.VerifyResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ro.pippo.core.route.RouteContext;

@Getter
@RequiredArgsConstructor
public class FailedAuthEvent {

    @NonNull
    private final RouteContext requestContext;

    @NonNull
    private final VerifyResponse response;

}
