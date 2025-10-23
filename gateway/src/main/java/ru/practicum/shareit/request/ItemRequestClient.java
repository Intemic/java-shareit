package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClientCache;
import ru.practicum.shareit.request.dto.ItemRequestCreate;

@Service
public class ItemRequestClient extends BaseClientCache {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getRequest(long requestId) {
        return get("/" + requestId);
    }

    public ResponseEntity<Object> getYourRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getRequestsExcludingYour(long excludingUserId) {
        return get("/all", excludingUserId);
    }

    public ResponseEntity<Object> createRequest(ItemRequestCreate item, long userId) {
        return post("", userId, item);
    }
}
