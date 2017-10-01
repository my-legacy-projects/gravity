package club.wontfix.gravity.easy;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(staticName = "create")
public class User {

    public static final User NULL = User.create("NULL", "NULL", "NULL", "NULL", false, false);

    @NonNull
    @Getter
    @Setter
    private String name;

    @NonNull
    @Getter
    @Setter
    private String uniqueID;

    @NonNull
    @Getter
    @Setter
    private String verifyID;

    @NonNull
    @Getter
    @Setter
    private String machineName;

    @NonNull
    @Getter
    @Setter
    private boolean dev;

    @NonNull
    @Getter
    @Setter
    private boolean killSwitched;

}
