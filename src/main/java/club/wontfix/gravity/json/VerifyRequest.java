package club.wontfix.gravity.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class VerifyRequest {

    @Getter
    private final String uniqueID;

    @Getter
    private final String verifyID;

    @Getter
    private final String timestamp; // ISO-8601 Timestamp

    @Getter
    private final Trace trace;

    @AllArgsConstructor(staticName = "create")
    public class Trace {

        @Getter
        private final String machineName;

        @Getter
        private final String machineNameHash; // SHA-256

        @Getter
        private final String cpuHash;

        @Getter
        private final Mac mac;

        @Getter
        private final String ipAddress;

        @Getter
        private boolean dev;

        @AllArgsConstructor(staticName = "create")
        public class Mac {

            @Getter
            private final String one;

            @Getter
            private final String two;

        }

    }

}
