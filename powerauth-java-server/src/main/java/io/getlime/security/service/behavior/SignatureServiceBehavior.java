package io.getlime.security.service.behavior;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.io.BaseEncoding;

import io.getlime.security.powerauth.VerifySignatureResponse;
import io.getlime.security.powerauth.lib.config.PowerAuthConfiguration;
import io.getlime.security.powerauth.lib.provider.CryptoProviderUtil;
import io.getlime.security.powerauth.server.keyfactory.PowerAuthServerKeyFactory;
import io.getlime.security.powerauth.server.signature.PowerAuthServerSignature;
import io.getlime.security.repository.ActivationRepository;
import io.getlime.security.repository.ApplicationVersionRepository;
import io.getlime.security.repository.model.ActivationStatus;
import io.getlime.security.repository.model.entity.ActivationRecordEntity;
import io.getlime.security.repository.model.entity.ApplicationVersionEntity;
import io.getlime.security.service.util.ModelUtil;

@Component
public class SignatureServiceBehavior {
	
	@Autowired
	private AuditingServiceBehavior auditingServiceBehavior;
	
	@Autowired
	private ActivationRepository powerAuthRepository;
	
	@Autowired
	private ApplicationVersionRepository applicationVersionRepository;
	
	private final PowerAuthServerSignature powerAuthServerSignature = new PowerAuthServerSignature();
	private final PowerAuthServerKeyFactory powerAuthServerKeyFactory = new PowerAuthServerKeyFactory();

	public VerifySignatureResponse verifySignature(String activationId, String signatureType, String signature, String dataString, String applicationKey, CryptoProviderUtil keyConversionUtilities) throws UnsupportedEncodingException, InvalidKeySpecException, InvalidKeyException {
		// Prepare current timestamp in advance
		Date currentTimestamp = new Date();

		// Fetch related activation
		ActivationRecordEntity activation = powerAuthRepository.findFirstByActivationId(activationId);

		// Only validate signature for existing ACTIVE activation records
		if (activation != null) {

			// Check the activation - application relationship and version support
			ApplicationVersionEntity applicationVersion = applicationVersionRepository.findByApplicationKey(applicationKey);

			if (applicationVersion == null || applicationVersion.getSupported() == false || applicationVersion.getApplication().getId() != activation.getApplication().getId()) {

				// Get the data and append application KEY in this case, just for auditing reasons
				byte[] data = (dataString + "&" + applicationKey).getBytes("UTF-8");

				// Increment the counter
				activation.setCounter(activation.getCounter() + 1);
				
				// Update failed attempts and block the activation, if necessary
				activation.setFailedAttempts(activation.getFailedAttempts() + 1);
				Long remainingAttempts = (activation.getMaxFailedAttempts() - activation.getFailedAttempts());
				if (remainingAttempts <= 0) {
					activation.setActivationStatus(ActivationStatus.BLOCKED);
				}

				// Update the last used date
				activation.setTimestampLastUsed(currentTimestamp);

				// Save the activation
				powerAuthRepository.save(activation);

				auditingServiceBehavior.logSignatureAuditRecord(activation, signatureType, signatureType, data, false, "activation_invalid_application", currentTimestamp);

				// return the data
				VerifySignatureResponse response = new VerifySignatureResponse();
				response.setActivationId(activationId);
				response.setActivationStatus(ModelUtil.toServiceStatus(ActivationStatus.REMOVED));
				response.setRemainingAttempts(BigInteger.valueOf(0));
				response.setSignatureValid(false);
				response.setUserId("UNKNOWN");

				return response;
			}

			String applicationSecret = applicationVersion.getApplicationSecret();
			byte[] data = (dataString + "&" + applicationSecret).getBytes("UTF-8");

			if (activation.getActivationStatus() == ActivationStatus.ACTIVE) {

				// Get the server private and device public keys
				byte[] serverPrivateKeyBytes = BaseEncoding.base64().decode(activation.getServerPrivateKeyBase64());
				byte[] devicePublicKeyBytes = BaseEncoding.base64().decode(activation.getDevicePublicKeyBase64());
				PrivateKey serverPrivateKey = keyConversionUtilities.convertBytesToPrivateKey(serverPrivateKeyBytes);
				PublicKey devicePublicKey = keyConversionUtilities.convertBytesToPublicKey(devicePublicKeyBytes);

				// Compute the master secret key
				SecretKey masterSecretKey = powerAuthServerKeyFactory.generateServerMasterSecretKey(serverPrivateKey, devicePublicKey);

				// Get the signature keys according to the signature type
				List<SecretKey> signatureKeys = powerAuthServerKeyFactory.keysForSignatureType(signatureType, masterSecretKey);

				// Verify the signature with given lookahead
				boolean signatureValid = false;
				long ctr = activation.getCounter();
				long lowestValidCounter = ctr;
				for (long iterCtr = ctr; iterCtr < ctr + PowerAuthConfiguration.SIGNATURE_VALIDATION_LOOKAHEAD; iterCtr++) {
					signatureValid = powerAuthServerSignature.verifySignatureForData(data, signature, signatureKeys, iterCtr);
					if (signatureValid) {
						// set the lowest valid counter and break at the lowest
						// counter where signature validates
						lowestValidCounter = iterCtr;
						break;
					}
				}
				if (signatureValid) {

					// Set the activation record counter to the lowest counter
					// (+1, since the client has incremented the counter)
					activation.setCounter(lowestValidCounter + 1);

					// Reset failed attempt count
					activation.setFailedAttempts(0L);

					// Update the last used date
					activation.setTimestampLastUsed(currentTimestamp);

					// Save the activation
					powerAuthRepository.save(activation);

					auditingServiceBehavior.logSignatureAuditRecord(activation, signatureType, signatureType, data, true, "signature_ok", currentTimestamp);

					// return the data
					VerifySignatureResponse response = new VerifySignatureResponse();
					response.setActivationId(activationId);
					response.setActivationStatus(ModelUtil.toServiceStatus(ActivationStatus.ACTIVE));
					response.setRemainingAttempts(BigInteger.valueOf(activation.getMaxFailedAttempts()));
					response.setSignatureValid(true);
					response.setUserId(activation.getUserId());

					return response;

				} else {

					// Increment the activation record counter
					activation.setCounter(activation.getCounter() + 1);

					// Update failed attempts and block the activation, if
					// necessary
					activation.setFailedAttempts(activation.getFailedAttempts() + 1);
					Long remainingAttempts = (activation.getMaxFailedAttempts() - activation.getFailedAttempts());
					if (remainingAttempts <= 0) {
						activation.setActivationStatus(ActivationStatus.BLOCKED);
					}

					// Update the last used date
					activation.setTimestampLastUsed(currentTimestamp);

					// Save the activation
					powerAuthRepository.save(activation);

					auditingServiceBehavior.logSignatureAuditRecord(activation, signatureType, signatureType, data, false, "signature_does_not_match", currentTimestamp);

					// return the data
					VerifySignatureResponse response = new VerifySignatureResponse();
					response.setActivationId(activationId);
					response.setActivationStatus(ModelUtil.toServiceStatus(activation.getActivationStatus()));
					response.setRemainingAttempts(BigInteger.valueOf(remainingAttempts));
					response.setSignatureValid(false);
					response.setUserId(activation.getUserId());

					return response;

				}

			} else {

				// Despite the fact activation is not in active state, increase
				// the counter
				activation.setCounter(activation.getCounter() + 1);

				// Update the last used date
				activation.setTimestampLastUsed(currentTimestamp);

				// Save the activation
				powerAuthRepository.save(activation);

				auditingServiceBehavior.logSignatureAuditRecord(activation, signatureType, signatureType, data, false, "activation_invalid_state", currentTimestamp);

				// return the data
				VerifySignatureResponse response = new VerifySignatureResponse();
				response.setActivationId(activationId);
				response.setActivationStatus(ModelUtil.toServiceStatus(ActivationStatus.REMOVED));
				response.setRemainingAttempts(BigInteger.valueOf(0));
				response.setSignatureValid(false);
				response.setUserId("UNKNOWN");

				return response;

			}

		} else { // Activation does not exist

			// return the data
			VerifySignatureResponse response = new VerifySignatureResponse();
			response.setActivationId(activationId);
			response.setActivationStatus(ModelUtil.toServiceStatus(ActivationStatus.REMOVED));
			response.setRemainingAttempts(BigInteger.valueOf(0));
			response.setSignatureValid(false);
			response.setUserId("UNKNOWN");

			return response;

		}
	}

}
