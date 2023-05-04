package net.nimajnebec.costumizer.api;

import net.nimajnebec.costumizer.Costumizer;
import net.nimajnebec.costumizer.api.json.CostumeData;
import net.nimajnebec.costumizer.api.json.CostumeName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

public class CostumizerApiService {
    private final URL baseUrl;
    private final URL listCostumesUrl;
    private final URL costumeDataUrl;

    public CostumizerApiService(Costumizer plugin) throws MalformedURLException {
        this.baseUrl = plugin.getConfiguration().getUiUrl();
        this.listCostumesUrl = new URL(baseUrl, "/api/costume/list");
        this.costumeDataUrl = new URL(baseUrl, "/api/costume/data");
    }

    public URL addParameter(URL url, String parameter) {
        try {
            return new URI(url.getProtocol(), url.getAuthority(), url.getPath(),
                url.getQuery() == null ? parameter : url.getQuery() + "&" + parameter, "").toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public CostumeName[] listCostumes(UUID uuid) {
        CostumizerRequest request = new CostumizerRequest(listCostumesUrl);
        request.authenticated(uuid);
        try {
            request.execute("GET");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return request.deserialize(CostumeName[].class);
    }

    public CostumeData getCostumeData(UUID uuid, String name) throws IOException {
        URL url = addParameter(costumeDataUrl, "name="+name);
        CostumizerRequest request = new CostumizerRequest(url);
        request.authenticated(uuid);
        request.execute("GET");
        return request.deserialize(CostumeData.class);
    }

}
