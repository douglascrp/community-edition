﻿------------------------------------------------------------------------------
ドキュメント名:   ${document.name}
------------------------------------------------------------------------------

   <#if document.properties.title?exists>
タイトル:   ${document.properties.title}
   <#else>
タイトル:   なし
   </#if>
   <#if document.properties.description?exists>
説明:     ${document.properties.description}
   <#else>
説明:   なし
   </#if>
作成者:   ${document.properties.creator}
作成日時: ${document.properties.created?datetime}
修正者:   ${document.properties.modifier}
修正日時: ${document.properties.modified?datetime}
サイズ:    ${document.size / 1024} KB


コンテンツリンク

コンテンツ フォルダー:   ${contentFolderUrl}
コンテンツ URL:      ${contextUrl}/service/api/node/content/${document.storeType}/${document.storeId}/${document.id}/${document.name}
ダウンロード URL:     ${contextUrl}/service/api/node/content/${document.storeType}/${document.storeId}/${document.id}/${document.name}?a=true
WebDAV URL:       ${contextUrl}${document.webdavUrl}


