package club.wontfix.gravity.easy;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public class User {

    public static final User NULL = User.create("NULL", "NULL", "NULL", "NULL", false);

    @NonNull @Getter
    private final String name;

    @NonNull @Getter
    private final String uniqueID;

    @NonNull @Getter
    private final String verifyID;

    @NonNull @Getter
    private final String machineName;

    @NonNull @Getter
    private boolean dev;

}
