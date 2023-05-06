package net.nimajnebec.costumizer.api;

import com.google.gson.Gson;
import net.nimajnebec.costumizer.configuration.CostumizerConfiguration;
import net.nimajnebec.costumizer.api.json.TokenHeader;
import net.nimajnebec.costumizer.api.json.TokenPayload;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class AuthenticationService {

    private final CostumizerConfiguration configuration;
    private final Base64.Encoder encoder = Base64.getUrlEncoder();

    public AuthenticationService(CostumizerConfiguration configuration) {
        this.configuration = configuration;
    }

    public String generateSignature(String data) {
        try {
            Mac HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(configuration.getSecret(), "HmacSHA256");
            HMAC.init(key);

            return base64Encode(HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        }
        catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public String base64Encode(byte[] data) {
        return encoder.encodeToString(data).replace("=", "");
    }

    public String base64Encode(String data) {
        return base64Encode(data.getBytes(StandardCharsets.UTF_8));
    }


    public String generateToken(UUID uuid) {
        Gson gson = new Gson();
        String header = base64Encode(gson.toJson(new TokenHeader("HS256")));
        String payload = base64Encode(gson.toJson(new TokenPayload(uuid)));
        String partial = header + "." + payload;

        String signature = generateSignature(partial);
        return partial + "." + signature;
    }
}
