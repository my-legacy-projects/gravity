package club.wontfix.gravity.events.impl.error;

import club.wontfix.gravity.events.Cancellable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.SQLException;

@RequiredArgsConstructor
public class SQLExceptionEvent extends Cancellable {

    @Getter
    @NonNull
    private final SQLException exception;

    @Getter
    @Setter
    private int responseCode = 500;

    @Getter
    @Setter
    private String templateToRender = "error.html";

}
