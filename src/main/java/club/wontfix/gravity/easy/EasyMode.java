package club.wontfix.gravity.easy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EasyMode {

    UNKNOWN(-1, "UNKNOWN", "Unknown"),
    FAILURE(0, "FAILURE", "Failure"),
    SUCCESS(1, "SUCCESS", "Success"),

    VERIFYID_UNIQUEID_MISMATCH(2, "VERIFYID_UNIQUEID_MISMATCH", "VerifyID and UniqueID mismatched"),
    UNKNOWN_VERIFYID(3, "UNKNOWN_VERIFYID", "Unknown VerifyID"),

    BANNED_UNIQUEID(4, "BANNED_UNIQUEID", "This UniqueID is permanently untrusted."),
    KILLSWITCHED(5, "KILLSWITCHED", "This build is permanently untrusted."),

    // v  These error codes are only internally available and should never be thrown  v
    // v  They are only here for compatibility reasons between client & server        v

    __INTERNAL__ILLEGAL_RESPONSE(100, "ILLEGAL_RESPONSE", "Illegal Response received"),
    __INTERNAL__CURLPP_RUNTIME_ERROR(101, "CURLPP_RUNTIME_ERROR", "CurlPP Runtime Error occured"),
    __INTERNAL__CURLPP_LOGIC_ERROR(102, "CURLPP_LOGIC_ERROR", "CurlPP Logic Error occured");

    @Getter
    int id;

    @Getter
    String internalName;

    @Getter
    String normalizedName;

}
