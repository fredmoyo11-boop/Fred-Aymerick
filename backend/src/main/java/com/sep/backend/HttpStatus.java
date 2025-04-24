package com.sep.backend;

public class HttpStatus {
    public static final String CONTINUE = "100";

    public static final String SWITCHING_PROTOCOLS = "101";

    public static final String OK = "200";

    public static final String CREATED = "201";

    public static final String ACCEPTED = "202";

    public static final String NON_AUTHORITATIVE_INFORMATION = "203";

    public static final String NO_CONTENT = "204";

    public static final String RESET_CONTENT = "205";

    public static final String PARTIAL_CONTENT = "206";

    public static final String MULTIPLE_CHOICES = "300";

    public static final String MOVED_PERMANENTLY = "301";

    public static final String FOUND = "302";

    public static final String SEE_OTHER = "303";

    public static final String NOT_MODIFIED = "304";

    public static final String USE_PROXY = "305";

    public static final String TEMPORARY_REDIRECT = "307";

    public static final String BAD_REQUEST = "400";

    public static final String UNAUTHORIZED = "401";

    public static final String PAYMENT_REQUIRED = "402";

    public static final String FORBIDDEN = "403";

    public static final String NOT_FOUND = "404";

    public static final String METHOD_NOT_ALLOWED = "405";

    public static final String NOT_ACCEPTABLE = "406";

    public static final String PROXY_AUTHENTICATION_REQUIRED = "407";

    public static final String REQUEST_TIMEOUT = "408";

    public static final String CONFLICT = "409";

    public static final String GONE = "410";

    public static final String LENGTH_REQUIRED = "411";

    public static final String PRECONDITION_FAILED = "412";

    public static final String REQUEST_ENTITY_TOO_LARGE = "413";

    public static final String REQUEST_URI_TOO_LONG = "414";

    public static final String UNSUPPORTED_MEDIA_TYPE = "415";

    public static final String REQUESTED_RANGE_NOT_SATISFIABLE = "416";

    public static final String EXPECTATION_FAILED = "417";

    public static final String INTERNAL_SERVER_ERROR = "500";

    public static final String NOT_IMPLEMENTED = "501";

    public static final String BAD_GATEWAY = "502";

    public static final String SERVICE_UNAVAILABLE = "503";

    public static final String GATEWAY_TIMEOUT = "504";

    public static final String HTTP_VERSION_NOT_SUPPORTED = "505";

    public HttpStatus() {
        throw new UnsupportedOperationException("HttpStatus cannot be instantiated");
    }
}
