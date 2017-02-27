package io.mateu.ui.mdd.shared;

import io.mateu.ui.core.communication.Service;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 11/1/17.
 */
@Service(url = "erp")
public interface ERPService {

    public Object[][] select(String jpql) throws Exception;

    public Object selectSingleValue(String jpql) throws Exception;

    public Data selectPaginated(Data parameters) throws Exception;

    public int executeUpdate(String jpql) throws Exception;

    Data set(String serverSideControllerKey, String entityClassName, Data data) throws Exception;

    Data get(String serverSideControllerKey, String entityClassName, Object id) throws IllegalAccessException, InstantiationException, Exception;

    Data getMetaData(String entityClassName) throws Exception;

    Object runInServer(String className, String methodName, Data parameters) throws Exception;

}
