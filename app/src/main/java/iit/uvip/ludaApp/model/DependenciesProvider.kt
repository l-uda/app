package iit.uvip.ludaApp.model

object DependenciesProvider {

    private val udaService:UdaService = UdaService.create("")
    val remoteConnector:RemoteConnector = RemoteConnector(udaService)
}