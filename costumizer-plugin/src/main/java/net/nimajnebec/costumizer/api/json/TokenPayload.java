package net.nimajnebec.costumizer.api.json;

import java.util.UUID;

public class TokenPayload {

    public TokenPayload(UUID uuid) {
        this.uuid = uuid.toString().replace("-", "");
    }

    public String uuid;
}
