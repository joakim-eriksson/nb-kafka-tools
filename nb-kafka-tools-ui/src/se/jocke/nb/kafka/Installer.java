package se.jocke.nb.kafka;

import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import se.jocke.nb.kafka.client.AdminClientService;
import se.jocke.nb.kafka.client.NBKafkaProducer;

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
        
        Lookup.Result<AdminClientService> admins = Lookup.getDefault().lookupResult(AdminClientService.class);
        admins.allInstances().stream().forEach(AdminClientService::close);
        
        Lookup.Result<NBKafkaProducer> producers = Lookup.getDefault().lookupResult(NBKafkaProducer.class);
        producers.allInstances().stream().forEach(NBKafkaProducer::close);
    }

}
