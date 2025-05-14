package org.jeff.web.response;

import java.util.HashMap;
import java.util.Map;

public enum ResponseStatus
{
    Continue(100, "Continue"),
    SwitchingProtocols(101, "Switching Protocols"),
    Processing(102, "Processing"),
    OK(200, "OK"),
    Created(201, "Created"),
    Accepted(202, "Accepted"),
    NonAuthoritativeInformation(203, "Non-Authoritative Information"),
    NoContent(204, "No Content"),
    ResetContent(205, "Reset Content"),
    PartialContent(206, "Partial Content"),
    MultipleChoices(300, "Multiple Choices"),
    MovedPermanently(301, "Moved Permanently"),
    Found(302, "Found"),
    SeeOther(303, "See Other"),
    NotModified(304, "Not Modified"),
    UseProxy(305, "Use Proxy"),
    TemporaryRedirect(307, "Temporary Redirect"),
    BadRequest(400, "Bad Request"),
    Unauthorized(401, "Unauthorized"),
    PaymentRequired(402, "Payment Required"),
    Forbidden(403, "Forbidden"),
    NotFound(404, "Not Found"),
    MethodNotAllowed(405, "Method Not Allowed"),
    NotAcceptable(406, "Not Acceptable"),
    ProxyAuthenticationRequired(407, "Proxy Authentication Required"),
    RequestTimeout(408, "Request Timeout"),
    Conflict(409, "Conflict"),
    Gone(410, "Gone"),
    LengthRequired(411, "Length Required"),
    PreconditionFailed(412, "Precondition Failed"),
    RequestEntityTooLarge(413, "Request Entity Too Large"),
    URITooLong(414, "URIToo Long"),
    UnsupportedMediaType(415, "Unsupported Media Type"),
    RangeNotSatisfiable(416, "Range Not Satisfiable"),
    ExpectationFailed(417, "Expectation Failed"),
    UnprocessableEntity(422, "Unprocessable Entity"),
    TooManyRequests(429, "Too Many Requests"),
    InternalServerError(500, "Internal Server Error"),
    NotImplemented(501, "Not Implemented"),
    BadGateway(502, "Bad Gateway"),
    ServiceUnavailable(503, "Service Unavailable"),
    GatewayTimeout(504, "Gateway Timeout"),
    HTTPVersionNotSupported(505, "HTTP Version Not Supported"),
    ;
    private final int code;
    private final String message;
    ResponseStatus(int code, String message)
    {
        this.code = code;
        this.message = message;
    }
    public int code()
    {
        return code;
    }
    public String reason()
    {
        return message;
    }

    private static final Map<Integer, ResponseStatus> RESPONSE_STATUS_MAP = new HashMap<>();
    static
    {
        for(ResponseStatus status: values()) RESPONSE_STATUS_MAP.put(status.code(), status);
    }

    public static ResponseStatus fromCode(int code)
    {
        return ResponseStatus.RESPONSE_STATUS_MAP.get(code);
    }

    @Override
    public String toString()
    {
        return String.format("%d %s", code, message);
    }
}
