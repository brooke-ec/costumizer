package net.nimajnebec.costumizer.api;

import net.nimajnebec.costumizer.Costumizer;
import net.nimajnebec.costumizer.api.json.CostumeData;
import net.nimajnebec.costumizer.api.json.CostumeName;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

public class CostumizerApiService {
    private final AuthenticationService authenticator;
    private final Costumizer plugin;
    private URL listCostumesUrl;
    private URL costumeDataUrl;
    private URL loginUrl;

    public CostumizerApiService(Costumizer plugin) {
        this.authenticator = new AuthenticationService(plugin.getConfiguration());
        this.plugin = plugin;
    }

    public void initialise() throws MalformedURLException {
        URL baseUrl = plugin.getConfiguration().getUrl();
        this.listCostumesUrl = new URL(baseUrl, "/api/costume/list");
        this.costumeDataUrl = new URL(baseUrl, "/api/costume/data");
        this.loginUrl = new URL(baseUrl, "/login");
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
        request.authenticated(authenticator.generateToken(uuid));
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
        request.authenticated(authenticator.generateToken(uuid));
        request.execute("GET");
        return request.deserialize(CostumeData.class);
    }

    public String getLoginUrl(UUID uuid) {
        String token = authenticator.generateToken(uuid);
        URL url = addParameter(loginUrl, "token="+token);
        return url.toString();
    }

}
