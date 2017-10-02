package club.wontfix.gravity.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Cancellable {

    private boolean cancelled = false;

}
