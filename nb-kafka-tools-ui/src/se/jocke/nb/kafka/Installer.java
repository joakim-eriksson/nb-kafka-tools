package se.jocke.nb.kafka;

import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import se.jocke.nb.kafka.client.AdminClientService;

public class Installer extends ModuleInstall {

    @Override
    public void close() {
        closeClients();
    }

    @Override
    public void uninstalled() {
        closeClients();
    }

    public void closeClients() {
        Lookup.Result<AdminClientService> lookupResult = Lookup.getDefault().lookupResult(AdminClientService.class);
        lookupResult.allInstances().stream().forEach(AdminClientService::close);
    }

}
