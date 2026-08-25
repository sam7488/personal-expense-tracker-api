package np.sumit.PersonalExpenseTrackerAPI.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void shouldGenerateJwtToken() {
        // arrange
        when(authentication.getName())
                .thenReturn("john");

        when(authentication.getAuthorities())
                .thenAnswer(invocation -> List.of(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                ));

        String expectedToken = "jwt-token";

        ReflectionTestUtils.setField(
                jwtService,
                "issuer",
                "http://localhost:8080"
        );
        ReflectionTestUtils.setField(jwtService, "expiration", 3600L);

        Jwt jwt = Jwt.withTokenValue(expectedToken)
                .header("alg", "HS256")
                .claim("sub", "john")
                .build();

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        // act
        String result = jwtService.generateToken(authentication);

        //assert
        assertEquals(expectedToken, result);

        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(captor.capture());

        JwtEncoderParameters parameters = captor.getValue();

        JwtClaimsSet claims = parameters.getClaims();

        assertEquals("john", claims.getSubject());
        assertEquals("http://localhost:8080", claims.getIssuer().toString());

        assertEquals(
                List.of("ROLE_USER", "ROLE_ADMIN"),
                claims.getClaim("authorities")
        );

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiresAt());

        assertEquals(
                3600L,
                claims.getExpiresAt().getEpochSecond()
                        - claims.getIssuedAt().getEpochSecond()
        );

        assertEquals(
                "HS256",
                parameters.getJwsHeader().getAlgorithm().getName()
        );
    }
}
