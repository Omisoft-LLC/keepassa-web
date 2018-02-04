<#-- @ftlvariable name="data" type="com.omisoft.keepassa.dto.feedback.FeedbackDTO" -->
<html>
<head>
    <meta http-equiv="Content-Type"
          content="text/html; charset=UTF-8"/>
    <title>Error Email from Keepassa</title>
    <style>

    </style>
</head>
<body>
<p>Date posted: ${data.datePosted}</p>
<p>IP: ${data.ip}</p>
<p>User email: ${data.userEmail}
<p>Browser: ${data.browser}</p>
<p>Resolution: ${data.windowWidth}x${data.windowHeight}</p>
<p>URL: ${data.url}</p>
<p>Description: ${data.note}</p>
<#if data.imageCid??><p>Image: <img width="640" src="cid:${data.imageCid}" alt="Screenshot"/></p><#else>&nbsp;
</#if>
</body>
</html>
