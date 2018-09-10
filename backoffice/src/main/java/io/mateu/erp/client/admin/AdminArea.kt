package io.mateu.erp.client.admin

import com.vaadin.icons.VaadinIcons
import io.mateu.mdd.core.app.AbstractArea
import io.mateu.mdd.core.app.AbstractModule
import java.util.*

/**
 * Created by miguel on 3/1/17.
 */
class AdminArea : AbstractArea(VaadinIcons.COGS, "Admin") {

    override fun buildModules(): List<AbstractModule> {
        return Arrays.asList<AbstractModule>(AdminModule())
    }
}
