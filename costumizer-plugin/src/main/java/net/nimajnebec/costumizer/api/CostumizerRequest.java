package net.nimajnebec.costumizer.api;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CostumizerRequest {
    @Nullable private String token;
    private final URL url;
    private String text;
    private int status;

    public CostumizerRequest(URL url) {
        this.url = url;
    }

    public void authenticated(String token) {
        this.token = token;
    }

    public void execute(String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        if (token != null) conn.setRequestProperty("Authorization", "Bearer " + token);
        text = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
        status = conn.getResponseCode();
    }

    public <T> T deserialize(Class<T> typeof) {
        return new Gson().fromJson(this.text, typeof);
    }

    public String getText() {
        return this.text;
    }

    public URL getUrl() {
        return this.url;
    }

    public int getStatus() {
        return this.status;
    }
}
