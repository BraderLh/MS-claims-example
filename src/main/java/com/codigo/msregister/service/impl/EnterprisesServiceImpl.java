package com.codigo.msregister.service.impl;

import com.codigo.msregister.aggregates.request.RequestEnterprises;
import com.codigo.msregister.aggregates.response.ResponseBase;
import com.codigo.msregister.constants.Constants;
import com.codigo.msregister.entity.DocumentsTypeEntity;
import com.codigo.msregister.entity.EnterprisesEntity;
import com.codigo.msregister.entity.EnterprisesTypeEntity;
import com.codigo.msregister.repository.EnterprisesRepository;
import com.codigo.msregister.service.EnterprisesService;
import com.codigo.msregister.util.EnterprisesValidations;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class EnterprisesServiceImpl implements EnterprisesService {

    private final EnterprisesRepository enterprisesRepository;
    private final EnterprisesValidations enterprisesValidations;

    public EnterprisesServiceImpl(EnterprisesRepository enterprisesRepository, EnterprisesValidations enterprisesValidations) {
        this.enterprisesRepository = enterprisesRepository;
        this.enterprisesValidations = enterprisesValidations;
    }

    @Override
    public ResponseBase createEnterprise(RequestEnterprises requestEnterprises) {
        boolean validate = enterprisesValidations.validateInput(requestEnterprises);
        if(validate){
            EnterprisesEntity enterprises = getEntity(requestEnterprises);
            EnterprisesEntity generic = enterprisesRepository.save(enterprises);
            return new ResponseBase(Constants.CODE_SUCCESS,Constants.MESS_SUCCESS, Optional.of(generic));
        } else {
            return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_INVALID_DATA,null);
        }
    }

    @Override
    public ResponseBase findOneEnterprise(Integer id) {
        Optional<EnterprisesEntity> enterprises = enterprisesRepository.findById(id);
        if (enterprises.isPresent()) {
            return new ResponseBase(Constants.CODE_SUCCESS, Constants.MESS_SUCCESS, enterprises);
        } else {
            return new ResponseBase(Constants.CODE_ERROR, Constants.DATA_NOT_FOUND, null);
        }
    }

    @Override
    public ResponseBase findAllEnterprises() {
        Optional<List<EnterprisesEntity>> allEnterprises = Optional.of(enterprisesRepository.findAll());
        if (allEnterprises.stream().findAny().isPresent()) {
            return new ResponseBase(Constants.CODE_SUCCESS, Constants.MESS_SUCCESS, Optional.of(allEnterprises));
        } else {
            return new ResponseBase(Constants.CODE_SUCCESS, Constants.MESS_ZERO_ROWS, Optional.empty());
        }
    }

    @Override
    public ResponseBase updateEnterprise(Integer id, RequestEnterprises requestEnterprises) {
        boolean existEnterprise = enterprisesRepository.existsById(id);
        if(existEnterprise){
            Optional<EnterprisesEntity> enterprises = enterprisesRepository.findById(id);
            boolean validationEntity = enterprisesValidations.validateInput(requestEnterprises);
            if(validationEntity && enterprises.isPresent()){
                EnterprisesEntity enterprisesUpdate = getEntityUpdate(requestEnterprises,enterprises.get());
                enterprisesRepository.save(enterprisesUpdate);
                return new ResponseBase(Constants.CODE_SUCCESS,Constants.MESS_SUCCESS,Optional.of(enterprisesUpdate));
            }else {
                return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_INVALID_DATA,Optional.empty());
            }
        }else {
            return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_ERROR_NOT_UPDATE,Optional.empty());
        }
    }

    @Override
    public ResponseBase safeDeleteEnterprise(Integer id) {
        boolean existsEnterprise = enterprisesRepository.existsById(id);
        if (existsEnterprise) {
            Optional<EnterprisesEntity> enterprisesEntity = enterprisesRepository.findById(id);
            if (enterprisesEntity.isPresent()) {
                EnterprisesEntity enterprisesToDelete = enterprisesEntity.get();
                enterprisesToDelete.setStatus(Constants.STATUS_INACTIVE);
                enterprisesRepository.save(enterprisesToDelete);
                return new ResponseBase(Constants.CODE_SUCCESS, Constants.MESS_DELETE_ENTERPRISE_SUCCESS, Optional.of(enterprisesToDelete));
            }
            else {
                return new ResponseBase(Constants.CODE_ERROR, Constants.MESS_ERROR_NOT_DELETE_ENTERPRISE, Optional.empty());
            }
        } else {
            return new ResponseBase(Constants.CODE_ERROR, Constants.MESS_ENTERPRISE_DATA_NOT_FOUND, Optional.empty());
        }
    }

    @Override
    public ResponseBase permanentDeleteEnterprise(Integer id) {
        if (enterprisesRepository.existsById(id)) {
            Optional<EnterprisesEntity> enterprisesEntity = enterprisesRepository.findById(id);
            if (enterprisesEntity.isPresent()) {
                enterprisesRepository.deleteById(id);
                return new ResponseBase(Constants.CODE_SUCCESS, Constants.MESS_DELETE_ENTERPRISE_SUCCESS, enterprisesEntity);
            } else {
                return new ResponseBase(Constants.CODE_ERROR, Constants.MESS_ERROR_NOT_DELETE_ENTERPRISE, Optional.empty());
            }
        } else {
            return new ResponseBase(Constants.CODE_ERROR, Constants.MESS_ENTERPRISE_DATA_NOT_FOUND, Optional.empty());
        }
    }

    private EnterprisesEntity getEntity(RequestEnterprises requestEnterprises){
        EnterprisesEntity entity = new EnterprisesEntity();
        entity.setNumDocument(requestEnterprises.getNumDocument());
        entity.setBusinessName(requestEnterprises.getBusinessName());
        entity.setTradeName(enterprisesValidations.isNullOrEmpty(requestEnterprises.getTradeName()) ? requestEnterprises.getBusinessName() : requestEnterprises.getTradeName());
        entity.setStatus(Constants.STATUS_ACTIVE);
        //Añadiendo FK
        //Añadiendo FK
        entity.setEnterprisesTypeEntity(getEnterprisesType(requestEnterprises));
        entity.setDocumentsTypeEntity(getDocumentsType(requestEnterprises));
        //Auditoria
        entity.setUserCreate(Constants.AUDIT_ADMIN);
        entity.setDateCreate(getTimestamp());
        return entity;
    }

    private EnterprisesEntity getEntityUpdate(RequestEnterprises requestEnterprises, EnterprisesEntity enterprisesEntity) {
        enterprisesEntity.setNumDocument(requestEnterprises.getNumDocument());
        enterprisesEntity.setBusinessName(requestEnterprises.getBusinessName());
        enterprisesEntity.setTradeName(enterprisesValidations.isNullOrEmpty(requestEnterprises.getTradeName()) ?
                requestEnterprises.getBusinessName() : requestEnterprises.getTradeName());
        enterprisesEntity.setEnterprisesTypeEntity(getEnterprisesType(requestEnterprises));
        enterprisesEntity.setDocumentsTypeEntity(getDocumentsType(requestEnterprises));
        enterprisesEntity.setUserModif(Constants.AUDIT_ADMIN);
        enterprisesEntity.setDateModif(getTimestamp());
        return enterprisesEntity;
    }

    private EnterprisesTypeEntity getEnterprisesType(RequestEnterprises requestEnterprises) {
        EnterprisesTypeEntity typeEntity = new EnterprisesTypeEntity();
        typeEntity.setIdEnterprisesType(requestEnterprises.getEnterprisesTypeEntity());
        return typeEntity;
    }

    private DocumentsTypeEntity getDocumentsType(RequestEnterprises requestEnterprises) {
        DocumentsTypeEntity documentsTypeEntity = new DocumentsTypeEntity();
        documentsTypeEntity.setIdDocumentsType(requestEnterprises.getDocumentsTypeEntity());
        return documentsTypeEntity;
    }

    private Timestamp getTimestamp() {
        long currentTime = System.currentTimeMillis();
        return new Timestamp(currentTime);
    }
}
