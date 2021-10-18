package iit.uvip.ludaApp.model

import org.albaspazio.core.accessory.SingletonHolder


class DependenciesProviderParam private constructor(url: String) {

    private val udaService:UdaService   = UdaService.create(url)
//    val remoteConnector                 = RemoteConnector(udaService)
    val remoteConnector                 = RemoteConnector()

    companion object : SingletonHolder<DependenciesProviderParam, String>(::DependenciesProviderParam)
}


