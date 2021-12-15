package com.cema.users.services.validation.administration;

import com.cema.users.domain.audit.Audit;

public interface AdministrationClientService {

    void validateEstablishment(String cuig);

    void sendAuditRequest(Audit audit);
}
