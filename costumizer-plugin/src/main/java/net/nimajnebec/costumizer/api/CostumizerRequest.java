package net.nimajnebec.costumizer.api;

import com.google.gson.Gson;
import net.nimajnebec.costumizer.Costumizer;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CostumizerRequest {
    @Nullable private String token;
    private final URL url;
    private String text;


    public CostumizerRequest(URL url) {
        this.url = url;
    }

    public void authenticated(UUID uuid) {
        this.token = Costumizer.getInstance().getAuthenticationService().generateToken(uuid);
    }

    public void get() {
        this.execute("GET");
    }

    public void execute(String method) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            if (token != null) conn.setRequestProperty("Authorization", "Bearer " + token);
            text = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}
