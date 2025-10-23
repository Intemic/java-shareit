package ru.practicum.shareit.client;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.Objects;

@Builder
@RequiredArgsConstructor
class Request<T> {
    private final HttpMethod method;
    private final String path;
    private final Long userId;
    private final Map<String, Object> parameters;
    private final T body;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Request<?> request = (Request<?>) o;
        return Objects.equals(method, request.method) && Objects.equals(path, request.path)
                && Objects.equals(userId, request.userId) && Objects.equals(parameters, request.parameters)
                && Objects.equals(body, request.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, userId, parameters, body);
    }
}
