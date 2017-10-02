package club.wontfix.gravity.easy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EasyMode {

    UNKNOWN(-1, "Unknown"),
    FAILURE(0, "Failure"),
    SUCCESS(1, "Success"),

    VERIFYID_UNIQUEID_MISMATCH(2, "VerifyID and UniqueID mismatched"),
    UNKNOWN_VERIFYID(3, "Unknown VerifyID"),

    BANNED_UNIQUEID(4, "This UniqueID is permanently untrusted."),
    KILLSWITCHED(5, "This build is permanently untrusted."),

    // v  These error codes are only internally available and should never be thrown  v
    // v  They are only here for compatibility reasons between client & server        v

    __INTERNAL__ILLEGAL_RESPONSE(100, "Illegal Response received"),
    __INTERNAL__CURLPP_RUNTIME_ERROR(101, "CurlPP Runtime Error occured"),
    __INTERNAL__CURLPP_LOGIC_ERROR(102, "CurlPP Logic Error occured");

    int id;

    String normalizedName;

}
