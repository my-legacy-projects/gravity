package club.wontfix.gravity.events;

import lombok.Getter;
import lombok.Setter;

public abstract class Cancellable {

    @Getter @Setter
    private boolean cancelled = false;

}
