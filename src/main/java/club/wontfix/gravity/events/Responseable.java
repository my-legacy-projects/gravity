package club.wontfix.gravity.events;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class Responseable {

    @Getter
    private List<String> responses = new ArrayList<>();

    public void addResponse(String response) {
        responses.add(response);
    }

    public void removeResponse(String response) {
        if (hasResponse(response)) {
            responses.remove(response);
        }
    }

    public boolean hasResponse(String response) {
        return responses.contains(response);
    }

    public boolean hasResponseIgnoreCase(String response) {
        for (int i = 0; i < response.length(); i++) {
            if (responses.get(i).equalsIgnoreCase(response)) {
                return true;
            }
        }

        return false;
    }

}
