package ru.practicum.shareit.client;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseClientCache extends BaseClient {
    private final Map<Request<?>, ResponseEntity<Object>> cache = new HashMap<>();
    // Идемпотентные методы
    private final Set<HttpMethod> cachedMethods = Set.of(HttpMethod.GET, HttpMethod.DELETE,
            HttpMethod.PUT, HttpMethod.HEAD);

    public BaseClientCache(RestTemplate rest) {
        super(rest);
    }

    @Override
    protected <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                            @Nullable Map<String, Object> parameters, @Nullable T body) {
        ResponseEntity<Object> result = null;
        Request<?> request = null;

        if (cachedMethods.contains(method)) {
            request = Request.builder()
                    .method(method)
                    .path(path)
                    .userId(userId)
                    .parameters(parameters)
                    .build();

            result = cache.get(request);
        }

        if (result == null) {
            result = super.makeAndSendRequest(method, path, userId, parameters, body);
            if (cachedMethods.contains(method))
                cache.put(request, result);
        }
        return result;
    }
}
