package club.wontfix.gravity.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "create")
public class VerifyResponse {

    private String mode;

    private String uniqueID; // Server-calculated SHA-256 of the found uniqueID (not the sent one!!!)

    private boolean remember = false;

    private String message = "null";

}
