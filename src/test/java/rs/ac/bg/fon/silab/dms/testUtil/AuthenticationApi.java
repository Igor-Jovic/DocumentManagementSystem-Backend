package rs.ac.bg.fon.silab.dms.testUtil;

import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;
import rs.ac.bg.fon.silab.dms.rest.model.ApiResponse;
import rs.ac.bg.fon.silab.dms.rest.services.authentication.login.dto.LoginRequest;
import rs.ac.bg.fon.silab.dms.rest.services.authentication.login.dto.LoginResponse;

import java.io.IOException;
import java.util.LinkedHashMap;

import static rs.ac.bg.fon.silab.dms.testUtil.JsonConverter.fromJson;

public class AuthenticationApi {

    private String basePath;

    public AuthenticationApi(String basePath) {
        this.basePath = basePath;
    }

    public LoginResponse loginAs(String username, String password) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        ApiResponse response = restTemplate.postForObject(basePath, new LoginRequest(username, password), ApiResponse.class);
        return getLoginResponse(response);
    }

    private LoginResponse getLoginResponse(ApiResponse response) throws IOException {
        LinkedHashMap content = (LinkedHashMap) response.getContent();
        String json = new JSONObject(content).toString();
        return fromJson(json, LoginResponse.class);
    }
}
