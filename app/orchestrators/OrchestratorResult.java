package orchestrators;

public class OrchestratorResult<T> {
    private T Data;
    private String OAuthToken;
    private String Message;
    private Boolean Success;

    private OrchestratorResult(T data, String message, Boolean success, String oAuthToken) {
        Data = data;
        Message = message;
        Success = success;
        OAuthToken = oAuthToken;
    }

    static <U> OrchestratorResult<U> fromSucccess(U data) {
        return new OrchestratorResult<> (data, null, true, null);
    }

    static <U> OrchestratorResult<U> fromSucccess(U data, String oAuthToken) {
        return new OrchestratorResult<> (data, null, true, oAuthToken);
    }


    static <U> OrchestratorResult<U> fromFailure(String message) {
        return new OrchestratorResult<>(null, message, false, null);
    }

    static <U> OrchestratorResult<U> fromFailure(String message, String oAuthToken) {
        return new OrchestratorResult<>(null, message, false, oAuthToken);
    }

    public Boolean success() {return Success;}

    public T get() {
        if (!Success) {
            throw new IllegalStateException("Can't get data: operation was unsuccessful");
        }
        return Data;
    }

    public String message() {return Message; }

    public String auth() {
        return OAuthToken;
    }
}
