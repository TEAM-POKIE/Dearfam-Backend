package com.example.dearfam.common.jwt.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dearfam.common.dto.token.TokenDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtVerifier {
    private final Algorithm tokenAlgorithm;

    public TokenDto verify(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null;
        }
        return verifyToken(bearerToken);
    }

    private TokenDto verifyToken(String bearerToken) {
        try {
            String token = bearerToken.substring(7);

            JWTVerifier tokenVerifier = JWT
                    .require(tokenAlgorithm)
                    .withClaimPresence("userId")
                    .withClaimPresence("userRole")
                    .build();

            DecodedJWT verifiedJWT = tokenVerifier.verify(token);

            return new TokenDto(
                    verifiedJWT.getClaim("userId").asLong(),
                    verifiedJWT.getClaim("userRole").asString());

        } catch (TokenExpiredException e) {
            // 토큰이 만료된 경우 (401 Unauthorized 응답)
            throw new TokenExpiredException("JWT 토큰이 만료되었습니다. 다시 로그인해주세요.", Instant.now());

        } catch (SignatureVerificationException e) {
            // 서명 검증 실패 (JWT가 변조된 경우)
            throw new JWTVerificationException("JWT 서명 검증에 실패했습니다. 토큰이 변조되었을 가능성이 있습니다.", e);

        } catch (AlgorithmMismatchException e) {
            // JWT의 알고리즘이 서버에서 설정한 알고리즘과 다른 경우
            throw new JWTVerificationException("JWT 알고리즘이 서버에서 설정한 방식과 일치하지 않습니다. 토큰 서명 방식을 확인하세요.", e);

        } catch (MissingClaimException e) {
            // 필요한 클레임이 JWT에 없는 경우
            throw new JWTVerificationException("JWT에 필요한 클레임 정보가 없습니다. 'userId' 및 'userRole' 값이 포함되어 있는지 확인하세요.", e);

        } catch (IncorrectClaimException e) {
            // 클레임 값이 예상과 다른 경우
            throw new JWTVerificationException("JWT의 클레임 값이 올바르지 않습니다. 'userId' 및 'userRole' 값이 정확한지 확인하세요.", e);

        }
        catch (JWTDecodeException e) {
            // JWT 디코딩 실패 (잘못된 형식의 JWT)
            throw new JWTVerificationException("JWT 형식이 올바르지 않습니다. 토큰을 디코딩할 수 없습니다.", e);

        }
        catch (JWTVerificationException e) {
            // 기타 검증 오류
            throw new JWTVerificationException("JWT 검증에 실패했습니다. 토큰이 유효하지 않거나 변조되었을 가능성이 있습니다.", e);

        } catch (Exception e) {
            // 예상치 못한 예외 처리 (서버 내부 오류)
            throw new RuntimeException("JWT 검증 중 예상치 못한 오류가 발생했습니다.", e);
        }
    }
}
