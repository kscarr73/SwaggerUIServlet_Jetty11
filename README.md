# Purpose

This jar bundles swagger-ui 3.47.1.  It is implemented as a Jetty11 Servlet.

You can add it to your jetty embeded [JettyEmbeded](https://github.com/kscarr73/JettyEmbedded) via:

```java
Map<String, String> apiDocs = new LinkedHashMap<>();
apiDocs.put("Default", "http://localhost:8080/api-doc.yaml");

Map<String, Servlet> servlets = new LinkedHashMap<>();

// Make sure the Alias matches the root of the Context path for the servlet
servlets.put("/api/*", new SwaggerUIServlet("/api", apiDocs));

// Add whatever other servlets you want.
servlets.put("/*", new MyServlet());
```

This is included on the Progbits Maven Repo:

https://archiva.progbits.com/coffer/repository/internal/

```xml
<dependency>
    <groupId>com.progbits.web</groupId>
    <artifactId>SwaggerUIServlet_Jetty11</artifactId>
    <version>3.47.1</version>
</dependency>
```

```groovy
implementation group: 'com.progbits.web', name: 'SwaggerUIServlet_Jetty11', version: '3.47.1'
```