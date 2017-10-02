package club.wontfix.gravity.events.impl.error;

import club.wontfix.gravity.events.Cancellable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.SQLException;

@Getter
@RequiredArgsConstructor
public class SQLExceptionEvent extends Cancellable {

    @NonNull
    private final SQLException exception;

    @Setter
    private int responseCode = 500;

    @Setter
    private String templateToRender = "error.html";

}
