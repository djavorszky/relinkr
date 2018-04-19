/**
 * Copyright (c) 2017-present Laszlo Csontos All rights reserved.
 *
 * This file is part of springuni-particles.
 *
 * springuni-particles is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * springuni-particles is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with springuni-particles.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.springuni.hermes.core.security.authn.jwt;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.lang.System.currentTimeMillis;

import com.springuni.hermes.core.util.IdentityGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.www.NonceExpiredException;

/**
 * Created by lcsontos on 5/17/17.
 */
public class JwtTokenServiceImpl implements JwtTokenService {

    private static final String AUTHORITIES = "authorities";

    private final byte[] secretKey;
    private final IdentityGenerator identityGenerator;

    public JwtTokenServiceImpl(byte[] secretKey, IdentityGenerator identityGenerator) {
        this.secretKey = secretKey;
        this.identityGenerator = identityGenerator;
    }

    @Override
    public String createJwtToken(Authentication authentication, int minutes) {
        Claims claims = Jwts.claims()
                .setId(String.valueOf(identityGenerator.generate()))
                .setSubject(authentication.getName())
                .setExpiration(new Date(currentTimeMillis() + minutes * 60 * 1000))
                .setIssuedAt(new Date());

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toUpperCase)
                .collect(Collectors.joining(","));

        claims.put(AUTHORITIES, authorities);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(HS512, secretKey)
                .compact();
    }

    @Override
    public Authentication parseJwtToken(String jwtToken) throws AuthenticationException {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken)
                    .getBody();

            return JwtAuthenticationToken.of(claims);
        } catch (ExpiredJwtException e) {
            throw new NonceExpiredException(e.getMessage(), e);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            throw new BadCredentialsException(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

}
