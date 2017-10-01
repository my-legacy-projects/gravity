package club.wontfix.gravity.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor(staticName = "create")
public class VerifyResponse {

    @Getter(onMethod = @__(@NonNull))
    @Setter(onMethod = @__(@NonNull))
    private String mode;

    @Getter(onMethod = @__(@NonNull))
    @Setter(onMethod = @__(@NonNull))
    private String uniqueID; // Server-calculated SHA-256 of the found uniqueID (not the sent one!!!)

    @Getter(onMethod = @__(@NonNull))
    @Setter(onMethod = @__(@NonNull))
    private boolean remember = false;

    @Getter(onMethod = @__(@NonNull))
    @Setter(onMethod = @__(@NonNull))
    private String message = "null";

}
