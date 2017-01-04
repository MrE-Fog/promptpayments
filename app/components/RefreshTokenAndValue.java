package components;

public class RefreshTokenAndValue<T>{
    public final String refreshToken;
    public final T value;

    public RefreshTokenAndValue(String refreshToken, T value) {
        this.refreshToken = refreshToken;
        this.value = value;
    }
}
