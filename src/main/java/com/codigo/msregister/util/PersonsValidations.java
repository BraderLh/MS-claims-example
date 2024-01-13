package com.codigo.msregister.util;

import com.codigo.msregister.aggregates.enums.ETypeDocuments;
import com.codigo.msregister.aggregates.request.RequestPersons;
import com.codigo.msregister.constants.Constants;
import com.codigo.msregister.entity.DocumentsTypeEntity;
import com.codigo.msregister.repository.DocumentsTypeRepository;
import com.codigo.msregister.repository.EnterprisesRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Slf4j
public class PersonsValidations {

    private final DocumentsTypeRepository typeRepository;
    private final EnterprisesRepository enterprisesRepository;

    public PersonsValidations(DocumentsTypeRepository typeRepository, EnterprisesRepository enterprisesRepository) {
        this.typeRepository = typeRepository;
        this.enterprisesRepository = enterprisesRepository;
    }


    public boolean validateInput(RequestPersons requestPersons) {
        if (requestPersons == null) {
            return false;
        }

        DocumentsTypeEntity documentType = typeRepository.findByCodType("01");
        if (requestPersons.getDocuments_type_id_documents_type() != Integer.valueOf(documentType.getCodType())
                || requestPersons.getNumDocument().length() != Constants.LENGTH_DNI) {
            return false;
        }

        if (isNullOrEmpty(requestPersons.getEmail()) || isNullOrEmpty(requestPersons.getNumDocument())
                || isNullOrEmpty(requestPersons.getTelephone())) {
            return false;
        }

        return enterprisesRepository.existsById(requestPersons.getEnterprises_id_enterprises());
    }

    public boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

}
