package club.wontfix.gravity.easy;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "create")
public class User {

    public static final User NULL = User.create("NULL", "NULL", "NULL", "NULL", false, false);

    @NonNull
    private String name;

    @NonNull
    private String uniqueID;

    @NonNull
    private String verifyID;

    @NonNull
    private String machineName;

    @NonNull
    private boolean dev;

    @NonNull
    private boolean killSwitched;

}
