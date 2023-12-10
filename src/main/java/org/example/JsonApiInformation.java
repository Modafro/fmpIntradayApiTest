package org.example;

import java.util.HashMap;
import java.util.Map;

public class JsonApiInformation {
    private final String url;
    private final String uri;
    private final Map<String, String> uriParameters;
    private final Map<String, String> uriHeaders;

    private JsonApiInformation(Builder builder) {
        this.url = builder.url;
        this.uri = builder.uri;
        this.uriParameters = builder.uriParameters;
        this.uriHeaders = builder.uriHeaders;
    }

    public Map<String, String> getUriParameters() {
        return uriParameters;
    }

    public Map<String, String> getUriHeaders() {
        return uriHeaders;
    }

    public String getUrl() {
        return url;
    }

    public String getUri() {
        return uri;
    }

    public static class Builder {
        private final Map<String, String> uriParameters = new HashMap<>();
        private final Map<String, String> uriHeaders = new HashMap<>();
        private String url;
        private String uri;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder setParameter(String key, String value) {
            this.uriParameters.put(key, value);
            return this;
        }

        public Builder setHeader(String key, String value) {
            this.uriHeaders.put(key, value);
            return this;
        }

        public JsonApiInformation build() throws Exception {
            if (this.uri != null && this.url != null) {
                throw new Exception("Cannot have a JsonApiInformation with both URI and URL");
            }

            return new JsonApiInformation(this);
        }
    }
}
