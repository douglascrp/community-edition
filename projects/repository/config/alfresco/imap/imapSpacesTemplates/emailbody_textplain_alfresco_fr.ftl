------------------------------------------------------------------------------
Nom du document :   ${document.name}
------------------------------------------------------------------------------

   <#if document.properties.title?exists>
Titre :   ${document.properties.title}
   <#else>
Titre :         AUCUN
   </#if>
   <#if document.properties.description?exists>
Description :   ${document.properties.description}
   <#else>
Description :   AUCUNE
   </#if>
Créateur :      ${document.properties.creator}
Créé :          ${document.properties.created?datetime}
Modificateur :  ${document.properties.modifier}
Modifié :       ${document.properties.modified?datetime}
Taille :        ${document.size / 1024} Ko


LIENS DU CONTENU

Dossier du contenu :     ${contentFolderUrl}
URL du contenu :         ${contextUrl}/service/api/node/content/${document.storeType}/${document.storeId}/${document.id}/${document.name}
URL de téléchargement :  ${contextUrl}/service/api/node/content/${document.storeType}/${document.storeId}/${document.id}/${document.name}?a=true
URL WebDAV :             ${contextUrl}${document.webdavUrl}
