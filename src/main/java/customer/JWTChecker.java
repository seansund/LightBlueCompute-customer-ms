package customer;

import java.util.Calendar;

import org.apache.commons.codec.binary.Base64;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

public class JWTChecker {
	private byte[] secret;
	private boolean enabled;
	
	public JWTChecker(String secretKey) {
		this(true, secretKey);
	}
	public JWTChecker(boolean enabled, String secretKey) {
		this.enabled = enabled;
		this.secret = new Base64(true).decode(secretKey);
	}
	public JWTChecker(byte[] secret) {
		this(true, secret);
	}
	public JWTChecker(boolean enabled, byte[] secret) {
		this.enabled = enabled;
		this.secret = secret;
	}

	public String checkJWTHeader(String authHeader) {
		// split the string after the bearer and validate it
		try {
			final String[] arr = authHeader.split("\\s+");
			final String jwt = arr[1];

			// TODO I think this code is unreachable
			if (jwt.length() == 0)
				return "Invalid authorization header";
			
			final SignedJWT signedJWT = SignedJWT.parse(jwt);
			final JWSVerifier verifier = new MACVerifier(secret);

			if (!signedJWT.verify(verifier) || signedJWT.getJWTClaimsSet().getIssuer() == null
					|| !signedJWT.getJWTClaimsSet().getIssuer().equals("apic")) {
				return "Unable to verify JWT token";
			} else if (signedJWT.getJWTClaimsSet().getExpirationTime() == null
					|| signedJWT.getJWTClaimsSet().getExpirationTime().before(Calendar.getInstance().getTime())) {
				return "JWT token expired";
			} else if (signedJWT.getJWTClaimsSet().getNotBeforeTime() != null
					&& signedJWT.getJWTClaimsSet().getNotBeforeTime().after(Calendar.getInstance().getTime())) {
				return "JWT token invalid";
			}
		} catch (Exception e) {
			return "Invalid JWT token";
		}

		return "";
	}
	public boolean isEnabled() {
		return enabled;
	}

}
