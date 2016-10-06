package orchestrators;

public class OrchestratorResult<T> {
    private T Data;
    private String Message;
    private Boolean Success;

    private OrchestratorResult(T data, String message, Boolean success) {
        Data = data;
        Message = message;
        Success = success;
    }

    static <U> OrchestratorResult<U> fromSucccess(U data) {
        return new OrchestratorResult<> (data, null, true);
    }

    static <U> OrchestratorResult<U> fromFailure(String message) {
        return new OrchestratorResult<>(null, message, false);
    }

    public Boolean success() {return Success;}

    public T get() {
        if (!Success) {
            throw new IllegalStateException("Can't get data: operation was unsuccessful");
        }
        return Data;
    }

    public String message() {return Message; }
}
