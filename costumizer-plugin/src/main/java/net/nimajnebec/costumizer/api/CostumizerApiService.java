package net.nimajnebec.costumizer.api;

import net.nimajnebec.costumizer.Costumizer;
import net.nimajnebec.costumizer.api.json.CostumeName;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class CostumizerApiService {
    private final URL baseUrl;
    private final URL listCostumesUrl;

    public CostumizerApiService(Costumizer plugin) throws MalformedURLException {
        this.baseUrl = plugin.getConfiguration().getUiUrl();
        this.listCostumesUrl = new URL(baseUrl, "/api/costume/list");
    }

    public CostumeName[] listCostumes(UUID uuid) {
        CostumizerRequest request = new CostumizerRequest(listCostumesUrl);
        request.authenticated(uuid);
        request.get();
        return request.deserialize(CostumeName[].class);
    }

}
