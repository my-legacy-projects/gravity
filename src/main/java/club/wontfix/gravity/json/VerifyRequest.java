package club.wontfix.gravity.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyRequest {

    private final String uniqueID;

    private final String verifyID;

    private final String timestamp; // ISO-8601 Timestamp

    private final Trace trace;

    @Getter
    @AllArgsConstructor
    public static class Trace {

        private final String machineName;

        private final String machineNameHash; // SHA-256

        private final String cpuHash;

        private final Mac mac;

        private final String ipAddress;

        private boolean dev;

        @Getter
        @AllArgsConstructor
        public static class Mac {

            private final String one;

            private final String two;

        }

    }

}
