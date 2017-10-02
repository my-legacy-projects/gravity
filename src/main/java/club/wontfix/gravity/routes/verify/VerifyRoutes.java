package club.wontfix.gravity.routes.verify;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.easy.User;
import club.wontfix.gravity.events.impl.verify.FailedAuthEvent;
import club.wontfix.gravity.events.impl.verify.SuccessAuthEvent;
import club.wontfix.gravity.json.VerifyRequest;
import club.wontfix.gravity.json.VerifyResponse;
import com.google.common.hash.Hashing;
import com.google.gson.JsonSyntaxException;
import ro.pippo.core.route.RouteContext;
import ro.pippo.core.route.RouteGroup;

import java.nio.charset.Charset;
import java.util.Random;

import static club.wontfix.gravity.easy.EasyMode.*;

public class VerifyRoutes extends RouteGroup {

    public static final String DEVELOPER_VERIFY_ID = "DEVELOPER_PREVIEW_QUICKBUILD";

    public VerifyRoutes() {
        super("/verify");

        // Before Filters
        addBeforeFilters();

        // Routes
        GET("/", this::routeVerify);
    }

    private void routeVerify(RouteContext context) {
        if (context.getRequest().getBody().length() > 0) {
            VerifyRequest request;

            try {
                request = Gravity.getInstance().getGson().fromJson(context.getRequest().getBody(), VerifyRequest.class);
            } catch (JsonSyntaxException ex) {
                errorFailedAuth(context);
                return;
            }

            if (Gravity.getInstance().getEasyDatabase().isVerifyIDBound(request.getVerifyID())) {
                User user = Gravity.getInstance().getEasyDatabase().getUserFromVerifyID(request.getVerifyID());
                if (user.getUniqueID().equals(request.getUniqueID())) {
                    if (!user.isKillSwitched()) {
                        // Success!
                        String hash = Hashing.sha256().hashString(user.getUniqueID(), Charset.forName("UTF-8")).toString();
                        boolean remember = user.isDev() || request.getTrace().getMachineName().equalsIgnoreCase(user.getMachineName());

                        VerifyResponse response = VerifyResponse.create(SUCCESS.name(), hash, remember, "Success!");

                        SuccessAuthEvent authEvent = new SuccessAuthEvent(context, request, response, user);
                        Gravity.getInstance().getEventBus().post(authEvent);

                        if (!authEvent.isCancelled()) {
                            String json = Gravity.getInstance().getGson().toJson(authEvent.getResponse());
                            String jsonHash = Hashing.sha256().hashString(json, Charset.forName("UTF-8")).toString();

                            context.status(200);
                            context.setHeader("X-Spartan-Gravity", "Isaac Newton");
                            context.setHeader("X-Spartan-Weeb", "Marc"); // Important touch for the reverse-ers.
                            context.setHeader("X-Spartan-SHA256", jsonHash);
                            context.send(json);
                        } else {
                            errorFailedAuth(authEvent.getContext());
                        }
                    } else {
                        errorKillSwitched(context);
                    }
                } else {
                    errorVerifyIDUniqueIDMismatch(context);
                }
            } else {
                if (request.getVerifyID().equals(DEVELOPER_VERIFY_ID)) {
                    if (Gravity.getInstance().getEasyDatabase().isUniqueIDBound(request.getUniqueID())) {
                        User user = Gravity.getInstance().getEasyDatabase().getUserFromUniqueID(request.getUniqueID());
                        if (!user.isKillSwitched()) {
                            if (user.isDev()) {
                                // Success!
                                String hash = Hashing.sha256().hashString(user.getUniqueID(), Charset.forName("UTF-8")).toString();

                                VerifyResponse response = VerifyResponse.create(SUCCESS.name(), hash, true, "Success!");

                                SuccessAuthEvent authEvent = new SuccessAuthEvent(context, request, response, user);
                                Gravity.getInstance().getEventBus().post(authEvent);

                                if (!authEvent.isCancelled()) {
                                    String json = Gravity.getInstance().getGson().toJson(authEvent.getResponse());
                                    String jsonHash = Hashing.sha256().hashString(json, Charset.forName("UTF-8")).toString();

                                    context.status(200);
                                    context.setHeader("X-Spartan-Dev", "true");
                                    context.setHeader("X-Spartan-Gravity", "Isaac Newton");
                                    context.setHeader("X-Spartan-Weeb", "Marc"); // Important touch for the reverse-ers.
                                    context.setHeader("X-Spartan-SHA256", jsonHash);
                                    context.send(json);
                                } else {
                                    errorFailedAuth(authEvent.getContext());
                                }
                            } else {
                                errorUnknownVerifyID(context);
                            }
                        } else {
                            errorKillSwitched(context);
                        }
                    } else {
                        errorVerifyIDUniqueIDMismatch(context);
                    }
                } else {
                    if (Gravity.getInstance().getEasyDatabase().isVerifyIDWaiting(request.getVerifyID())) {
                        User user = User.create("Dummy", request.getUniqueID(), request.getVerifyID(),
                                request.getTrace().getMachineName(), false, false);

                        user = Gravity.getInstance().getEasyDatabase().registerUserUsingWaitingVerifyID(
                                request.getVerifyID(), user
                        );

                        if (user != User.NULL) {
                            if (!user.isKillSwitched()) {
                                // Success!
                                String hash = Hashing.sha256().hashString(user.getUniqueID(), Charset.forName("UTF-8")).toString();

                                VerifyResponse response = VerifyResponse.create(SUCCESS.name(), hash, false, "Success!");

                                SuccessAuthEvent authEvent = new SuccessAuthEvent(context, request, response, user);
                                Gravity.getInstance().getEventBus().post(authEvent);

                                if (!authEvent.isCancelled()) {
                                    String json = Gravity.getInstance().getGson().toJson(authEvent.getResponse());
                                    String jsonHash = Hashing.sha256().hashString(json, Charset.forName("UTF-8")).toString();

                                    String[] giftOptions = {"Coffee Pot", "Welcome Mat", "Dakimakura", "Phone"};
                                    String gift = giftOptions[new Random().nextInt(giftOptions.length)];

                                    context.status(200);
                                    context.setHeader("X-Spartan-Welcome-Gift", gift);
                                    context.setHeader("X-Spartan-Gravity", "Isaac Newton");
                                    context.setHeader("X-Spartan-Weeb", "Marc"); // Important touch for the reverse-ers.
                                    context.setHeader("X-Spartan-SHA256", jsonHash);
                                    context.send(json);
                                } else {
                                    errorFailedAuth(authEvent.getContext());
                                }
                            } else {
                                errorKillSwitched(context);
                            }
                        } else {
                            errorUnknownVerifyID(context);
                        }
                    } else {
                        errorUnknownVerifyID(context);
                    }
                }
            }
        } else {
            errorFailedAuth(context);
        }
    }

    private void addBeforeFilters() {
        ANY("/.*", context -> {
            if (!context.getRequest().getUserAgent().toLowerCase().startsWith("project-spartan")) {
                Gravity.getInstance().getErrorHandler().handle(404, context);
            }

            if (context.getRequest().getHeader("X-Spartan-SHA256") == null) {
                Gravity.getInstance().getErrorHandler().handle(404, context);
            }
        });
    }

    // ---------------------------------------------------------------------------------------

    private void errorKillSwitched(RouteContext context) {
        VerifyResponse response = VerifyResponse.create(KILLSWITCHED.name(), "null", false,
                "Your UniqueID and VerifyID are permanently untrusted."
        );

        FailedAuthEvent authEvent = new FailedAuthEvent(context, response);
        Gravity.getInstance().getEventBus().post(authEvent);
        String json = Gravity.getInstance().getGson().toJson(response);

        context.status(500);
        context.send(json);
    }

    private void errorFailedAuth(RouteContext context) {
        VerifyResponse response = VerifyResponse.create(FAILURE.name(), "null", false,
                "A illegal auth request has been sent. This incident will be reported."
        );

        FailedAuthEvent authEvent = new FailedAuthEvent(context, response);
        Gravity.getInstance().getEventBus().post(authEvent);
        String json = Gravity.getInstance().getGson().toJson(response);

        context.status(500);
        context.send(json);
    }

    private void errorUnknownVerifyID(RouteContext context) {
        VerifyResponse response = VerifyResponse.create(UNKNOWN_VERIFYID.name(), "null", false,
                "The sent Verify ID has not been found in the database."
        );

        FailedAuthEvent authEvent = new FailedAuthEvent(context, response);
        Gravity.getInstance().getEventBus().post(authEvent);
        String json = Gravity.getInstance().getGson().toJson(response);

        context.status(500);
        context.send(json);
    }

    private void errorVerifyIDUniqueIDMismatch(RouteContext context) {
        VerifyResponse response = VerifyResponse.create(VERIFYID_UNIQUEID_MISMATCH.name(), "null", false,
                "The sent Verify ID mismatches with the registered UniqueID. \n" +
                        "Please message a Coder and tell him to update your UniqueID if you changed your device."
        );

        FailedAuthEvent authEvent = new FailedAuthEvent(context, response);
        Gravity.getInstance().getEventBus().post(authEvent);
        String json = Gravity.getInstance().getGson().toJson(response);

        context.status(500);
        context.send(json);
    }

}
