package com.codigo.msregister.constants;

public class Constants {
    private Constants() {
    }

    //CODE
    public static final Integer CODE_SUCCESS=200;
    public static final Integer CODE_ERROR=400;

    //MESSAGES
    public static final String MESS_SUCCESS="Ejecución correcta";
    public static final String MESS_ERROR="Error en la Ejecución";
    public static final String MESS_INVALID_DATA = "Error: Datos no válidos";
    public static final String DATA_NOT_FOUND = "Error: Datos no encontrados";
    public static final String MESS_ZERO_ROWS = "Error: No hay datos para mostrar";
    public static final String MESS_ERROR_NOT_UPDATE ="Error: No se puedo ejecutar la actualización, empresa no existente";
    public static final String MESS_NON_DATA_RENIEC="No existe algún registro(s) en el API de Reniec";

    //for enterprises
    public static final String MESS_DELETE_ENTERPRISE_SUCCESS = "Satisfactorio: Se eliminó correctamente a la empresa";
    public static final String MESS_ERROR_NOT_DELETE_ENTERPRISE ="Error: No se pudo actualizar los datos de la empresa, datos erróneos o no existe";
    public static final String MESS_ENTERPRISE_DATA_NOT_FOUND = "Error: Empresa no encontrada";
    //for person
    public static final String MESS_UPDATE_PERSON_SUCCESS = "Satisfactorio: Se actualizó correctamente los datos de la persona";
    public static final String MESS_DELETE_PERSON_SUCCESS = "Satisfactorio: Se eliminó correctamente a la persona";
    public static final String MESS_PERSONA_DATA_NOT_FOUND = "Error: Persona no encontrada";
    public static final String MESS_ERROR_NOT_UPDATE_PERSON ="Error: No se pudo actualizar los datos de la persona, datos erróneos o no existe";
    public static final String MESS_ERROR_NOT_DELETE_PERSON ="Error: No se pudo eliminar a la persona, datos erróneos o no existe";
    //data
    public static final Integer LENGTH_RUC=11;
    public static final Integer LENGTH_DNI=8;

    //Status
    public static final Integer STATUS_ACTIVE=1;
    public static final Integer STATUS_INACTIVE=0;

    //AUDIT
    public static final String AUDIT_ADMIN="PRODRIGUEZ";

}
