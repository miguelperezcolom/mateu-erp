package io.mateu.erp.model.util;

import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;

import java.util.Hashtable;

/**
 * Created by miguel on 13/10/16.
 */
public class MiPostgreSQLPlatform extends PostgreSQLPlatform {

    public MiPostgreSQLPlatform() {
        super();
    }

    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable t = super.buildFieldTypes();
        t.put(String.class, new FieldTypeDefinition("TEXT", false));
        return t;
    }
}
