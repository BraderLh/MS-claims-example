package com.codigo.msregister.service.impl;

import com.codigo.msregister.aggregates.request.RequestPersons;
import com.codigo.msregister.aggregates.response.ResponseBase;
import com.codigo.msregister.aggregates.response.ResponseReniec;
import com.codigo.msregister.constants.Constants;
import com.codigo.msregister.entity.DocumentsTypeEntity;
import com.codigo.msregister.entity.EnterprisesEntity;
import com.codigo.msregister.entity.PersonsEntity;
import com.codigo.msregister.feignClient.ReniecClient;
import com.codigo.msregister.repository.PersonsRepository;
import com.codigo.msregister.service.PersonsService;
import com.codigo.msregister.util.PersonsValidations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PersonsServiceImpl implements PersonsService {
    private final ReniecClient reniecClient;
    private final PersonsValidations personsValidations;
    private final PersonsRepository personsRepository;

    @Value("${token.api.reniec}")
    private String tokenReniec;

    public PersonsServiceImpl(ReniecClient reniecClient, PersonsValidations personsValidations, PersonsRepository personsRepository) {
        this.reniecClient = reniecClient;
        this.personsValidations = personsValidations;
        this.personsRepository = personsRepository;
    }

    @Override
    public ResponseBase getReniecInfo(String numero) {
        ResponseReniec reniec = getExecutionReniec(numero);
        if(reniec != null){
            return new ResponseBase(Constants.CODE_SUCCESS,Constants.MESS_SUCCESS, Optional.of(reniec));
        }else{
            return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_NON_DATA_RENIEC, Optional.empty());
        }

    }

    @Override
    public ResponseBase createPersons(RequestPersons requestPersons) {
        boolean validatePersons = personsValidations.validateInput(requestPersons);
        if(validatePersons){
            PersonsEntity personsEntity = getPersonsEntity(requestPersons);
            if(personsEntity != null){
                personsRepository.save(personsEntity);
                return new ResponseBase(Constants.CODE_SUCCESS,Constants.MESS_SUCCESS, Optional.of(personsEntity));
            }else {
                return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_ERROR,Optional.empty());
            }
        }else{
            return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_INVALID_DATA,Optional.empty());
        }
    }

    @Override
    public ResponseBase findOnePerson(int id) {
        Optional<PersonsEntity> personsEntity = personsRepository.findById(id);
        if (personsEntity.isPresent()) {
            return new ResponseBase(Constants.CODE_SUCCESS, Constants.MESS_SUCCESS, Optional.of(personsEntity));
        } else {
            return new ResponseBase(Constants.CODE_ERROR, Constants.MESS_PERSONA_DATA_NOT_FOUND, Optional.empty());
        }
    }

    @Override
    public ResponseBase findAllPersons() {
        List<PersonsEntity> personsEntities = personsRepository.findAll();
        if (!personsEntities.isEmpty()) {
            return new ResponseBase(Constants.CODE_SUCCESS, Constants.MESS_SUCCESS, Optional.of(personsEntities));
        } else {
            return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_ZERO_ROWS,Optional.empty());
        }
    }

    @Override
    public ResponseBase updatePersons(int id, RequestPersons requestPersons) {
        boolean existsPerson = personsRepository.existsById(id);
        if (existsPerson) {
            Optional<PersonsEntity> personsEntity = personsRepository.findById(id);
            boolean validateInput = personsValidations.validateInput(requestPersons);
            if (validateInput && personsEntity.isPresent()) {
                PersonsEntity personToUpdate = getPerson(requestPersons, personsEntity.get(), true);
                personsRepository.save(personToUpdate);
                return new ResponseBase(Constants.CODE_SUCCESS, Constants.MESS_UPDATE_PERSON_SUCCESS, Optional.of(personToUpdate));
            } else {
                return new ResponseBase(Constants.CODE_ERROR, Constants.MESS_INVALID_DATA, Optional.empty());
            }
        } else {
            return new ResponseBase(Constants.CODE_ERROR, Constants.MESS_ERROR_NOT_UPDATE_PERSON, Optional.empty());
        }
    }

    @Override
    public ResponseBase deletePerson(int id) {
        boolean existsPerson = personsRepository.existsById(id);
        if (existsPerson) {
            Optional<PersonsEntity> personsEntity = personsRepository.findById(id);
            if (personsEntity.isPresent()) {
                PersonsEntity personToDelete = personsEntity.get();
                personToDelete.setStatus(Constants.STATUS_INACTIVE);
                personsRepository.save(personToDelete);
                return new ResponseBase(Constants.CODE_SUCCESS, Constants.MESS_DELETE_PERSON_SUCCESS, Optional.of(personToDelete));
            } else {
                return new ResponseBase(Constants.CODE_ERROR, Constants.MESS_ERROR_NOT_DELETE_PERSON, Optional.empty());
            }
        } else {
            return new ResponseBase(Constants.CODE_ERROR, Constants.MESS_PERSONA_DATA_NOT_FOUND, Optional.empty());
        }
    }

    private PersonsEntity getPersonsEntity(RequestPersons requestPersons){
        PersonsEntity personsEntity = new PersonsEntity();
        personsEntity.setNumDocument(requestPersons.getNumDocument());
        //Executing Reniec
        ResponseReniec reniec = getExecutionReniec(requestPersons.getNumDocument());
        if(reniec != null){
            personsEntity.setName(reniec.getNombres());
            personsEntity.setLastName(reniec.getApellidoPaterno() + " " + reniec.getApellidoMaterno());
        }else{
            return null;
        }
        return  getPerson(requestPersons, personsEntity, false);
    }
    private PersonsEntity getPerson(RequestPersons requestPersons, PersonsEntity personsEntity, boolean isUpdate){
        personsEntity.setNumDocument(requestPersons.getNumDocument());
        personsEntity.setEmail(requestPersons.getEmail());
        personsEntity.setTelephone(requestPersons.getTelephone());
        personsEntity.setStatus(Constants.STATUS_ACTIVE);
        personsEntity.setDocumentsTypeEntity(getDocumentsType(requestPersons));
        personsEntity.setEnterprisesEntity(getEnterprisesEntity(requestPersons));
        if(isUpdate){
            personsEntity.setUserModif(Constants.AUDIT_ADMIN);
            personsEntity.setDateModif(getTimestamp());
        }else {
            personsEntity.setUserCreate(Constants.AUDIT_ADMIN);
            personsEntity.setDateCreate(getTimestamp());
        }

        return personsEntity;
    }

    @Cacheable(value = "REGISTER")
    public ResponseReniec getExecutionReniec(String numero){
        String authorization = "Bearer " + tokenReniec;
        return reniecClient.getReniecInfo(numero,authorization);
    }

    private DocumentsTypeEntity getDocumentsType(RequestPersons requestPersons){
        DocumentsTypeEntity typeEntity = new DocumentsTypeEntity();
        typeEntity.setIdDocumentsType(requestPersons.getDocuments_type_id_documents_type());
        return typeEntity;
    }

    private EnterprisesEntity getEnterprisesEntity(RequestPersons requestPersons){
        EnterprisesEntity enterprisesEntity = new EnterprisesEntity();
        enterprisesEntity.setIdEnterprises(requestPersons.getEnterprises_id_enterprises());
        return enterprisesEntity;
    }

    private Timestamp getTimestamp(){
        long currentTime = System.currentTimeMillis();
        return new Timestamp(currentTime);
    }
}