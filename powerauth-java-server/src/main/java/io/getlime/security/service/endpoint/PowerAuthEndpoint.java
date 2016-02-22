/**
 * Copyright 2015 Lime - HighTech Solutions s.r.o.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getlime.security.service.endpoint;

import io.getlime.security.powerauth.BlockActivationRequest;
import io.getlime.security.powerauth.BlockActivationResponse;
import io.getlime.security.powerauth.CommitActivationRequest;
import io.getlime.security.powerauth.CommitActivationResponse;
import io.getlime.security.powerauth.CreateApplicationRequest;
import io.getlime.security.powerauth.CreateApplicationResponse;
import io.getlime.security.powerauth.CreateApplicationVersionRequest;
import io.getlime.security.powerauth.CreateApplicationVersionResponse;
import io.getlime.security.powerauth.GetActivationListForUserRequest;
import io.getlime.security.powerauth.GetActivationListForUserResponse;
import io.getlime.security.powerauth.GetActivationStatusRequest;
import io.getlime.security.powerauth.GetActivationStatusResponse;
import io.getlime.security.powerauth.GetApplicationDetailRequest;
import io.getlime.security.powerauth.GetApplicationDetailResponse;
import io.getlime.security.powerauth.GetApplicationListRequest;
import io.getlime.security.powerauth.GetApplicationListResponse;
import io.getlime.security.powerauth.InitActivationRequest;
import io.getlime.security.powerauth.InitActivationResponse;
import io.getlime.security.powerauth.PrepareActivationRequest;
import io.getlime.security.powerauth.PrepareActivationResponse;
import io.getlime.security.powerauth.RemoveActivationRequest;
import io.getlime.security.powerauth.RemoveActivationResponse;
import io.getlime.security.powerauth.SignatureAuditRequest;
import io.getlime.security.powerauth.SignatureAuditResponse;
import io.getlime.security.powerauth.SupportApplicationVersionRequest;
import io.getlime.security.powerauth.SupportApplicationVersionResponse;
import io.getlime.security.powerauth.UnblockActivationRequest;
import io.getlime.security.powerauth.UnblockActivationResponse;
import io.getlime.security.powerauth.UnsupportApplicationVersionRequest;
import io.getlime.security.powerauth.UnsupportApplicationVersionResponse;
import io.getlime.security.powerauth.VaultUnlockRequest;
import io.getlime.security.powerauth.VaultUnlockResponse;
import io.getlime.security.powerauth.VerifySignatureRequest;
import io.getlime.security.powerauth.VerifySignatureResponse;
import io.getlime.security.service.PowerAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PowerAuthEndpoint {

    private static final String NAMESPACE_URI = "http://getlime.io/security/powerauth";

    @Autowired
    private PowerAuthService powerAuthService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "InitActivationRequest")
    @ResponsePayload
    public InitActivationResponse initActivation(@RequestPayload InitActivationRequest request) throws Exception {
        return powerAuthService.initActivation(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PrepareActivationRequest")
    @ResponsePayload
    public PrepareActivationResponse prepareActivation(@RequestPayload PrepareActivationRequest request) throws Exception {
        return powerAuthService.prepareActivation(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CommitActivationRequest")
    @ResponsePayload
    public CommitActivationResponse commitActivation(@RequestPayload CommitActivationRequest request) throws Exception {
        return powerAuthService.commitActivation(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetActivationStatusRequest")
    @ResponsePayload
    public GetActivationStatusResponse getActivationStatus(@RequestPayload GetActivationStatusRequest request) throws Exception {
        return powerAuthService.getActivationStatus(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RemoveActivationRequest")
    @ResponsePayload
    public RemoveActivationResponse removeActivation(@RequestPayload RemoveActivationRequest request) throws Exception {
        return powerAuthService.removeActivation(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetActivationListForUserRequest")
    @ResponsePayload
    public GetActivationListForUserResponse getActivatioListForUser(@RequestPayload GetActivationListForUserRequest request) throws Exception {
        return powerAuthService.getActivatioListForUser(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "VerifySignatureRequest")
    @ResponsePayload
    public VerifySignatureResponse verifySignature(@RequestPayload VerifySignatureRequest request) throws Exception {
        return powerAuthService.verifySignature(request);
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SignatureAuditRequest")
    @ResponsePayload
    public SignatureAuditResponse vaultUnlock(@RequestPayload SignatureAuditRequest request) throws Exception {
        return powerAuthService.getSignatureAuditLog(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "BlockActivationRequest")
    @ResponsePayload
    public BlockActivationResponse blockActivation(@RequestPayload BlockActivationRequest request) throws Exception {
        return powerAuthService.blockActivation(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UnblockActivationRequest")
    @ResponsePayload
    public UnblockActivationResponse unblockActivation(@RequestPayload UnblockActivationRequest request) throws Exception {
        return powerAuthService.unblockActivation(request);
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "VaultUnlockRequest")
    @ResponsePayload
    public VaultUnlockResponse vaultUnlock(@RequestPayload VaultUnlockRequest request) throws Exception {
        return powerAuthService.vaultUnlock(request);
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetApplicationListRequest")
    @ResponsePayload
    public GetApplicationListResponse getApplicationList(@RequestPayload GetApplicationListRequest request) throws Exception {
        return powerAuthService.getApplicationList(request);
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetApplicationDetailRequest")
    @ResponsePayload
    public GetApplicationDetailResponse getApplicationDetail(@RequestPayload GetApplicationDetailRequest request) throws Exception {
        return powerAuthService.getApplicationDetail(request);
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateApplicationRequest")
    @ResponsePayload
    public CreateApplicationResponse createApplication(@RequestPayload CreateApplicationRequest request) throws Exception {
        return powerAuthService.createApplication(request);
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateApplicationVersionRequest")
    @ResponsePayload
    public CreateApplicationVersionResponse createApplicationVersion(@RequestPayload CreateApplicationVersionRequest request) throws Exception {
        return powerAuthService.createApplicationVersion(request);
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UnsupportApplicationVersionRequest")
    @ResponsePayload
    public UnsupportApplicationVersionResponse unsupportApplicationVersion(@RequestPayload UnsupportApplicationVersionRequest request) throws Exception {
        return powerAuthService.unsupportApplicationVersion(request);
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SupportApplicationVersionRequest")
    @ResponsePayload
    public SupportApplicationVersionResponse supportApplicationVersion(@RequestPayload SupportApplicationVersionRequest request) throws Exception {
        return powerAuthService.supportApplicationVersion(request);
    }

}
