//import com.ufuk.accountprovider.Entity.CustomClientDetails;
//import com.ufuk.accountprovider.Service.CustomClientDetailsService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Arrays;
//import java.util.HashSet;
//
//@RunWith(SpringRunner.class)
//public class ClientDetailServiceTest {
//
//
//    static final String GRANT_TYPE_PASSWORD = "password";
//    static final String AUTHORIZATION_CODE = "authorization_code";
//    static final String REFRESH_TOKEN = "refresh_token";
//    static final String IMPLICIT = "implicit";
//    static final String SCOPE_READ = "read";
//    static final String SCOPE_WRITE = "write";
//    static final String TRUST = "trust";
//
//
//    @Test
//    public void createOauthClients() {
//
//        CustomClientDetails client = new CustomClientDetails();
//        client.setId("someId");
//
//        client.setResourceIds(new HashSet<>(Arrays.asList("resource_id")) );
//        client.setClientId("android-client");
//        client.setClientSecret("android-secret");
//        client.setAuthorizedGrantTypes(new HashSet<>(Arrays.asList(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT)));
//        client.setScope(new HashSet<>(Arrays.asList(SCOPE_READ, SCOPE_WRITE, TRUST)));
//        client.setSecretRequired(true);
//        client.setAccessTokenValiditySeconds(50000);
//        client.setRefreshTokenValiditySeconds(50000);
//        client.setScoped(false);
//
//        customClientDetailsRepository.save(client);
//    }
//}
