package com.cema.users.services.validation.administration;

import com.cema.users.domain.audit.Audit;
import lombok.SneakyThrows;

public interface AdministrationClientService {

    @SneakyThrows
    void validateEstablishment(String cuig, String token);

    void sendAuditRequest(Audit audit);
}
