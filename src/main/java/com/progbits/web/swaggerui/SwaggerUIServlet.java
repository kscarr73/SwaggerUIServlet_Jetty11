package com.progbits.web.swaggerui;

import com.progbits.web.ServletSetup;
import com.progbits.web.WebUtils;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scarr
 */
@Component(name = "SwaggerUIServlet",
		property = {"alias=/api", "name=SwaggerUIServlet"},
		service = {HttpServlet.class}
)
public class SwaggerUIServlet extends HttpServlet {

	private Logger log = LoggerFactory.getLogger(SwaggerUIServlet.class);

	private final DateTimeFormatter _dateHeader = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

	private ServletSetup _servlet = null;

	private String _alias = "/api";

	private Map<String, String> provideUrls = null;

	public SwaggerUIServlet() {

	}

	public SwaggerUIServlet(String alias) {
		_alias = alias;
	}

	/**
	 * Create a SwaggerUIServlet with URLs to be provided to the UI
	 *
	 * @param provideUrls Name, URL map of the urls to provide
	 */
	public SwaggerUIServlet(String alias, Map<String, String> provideUrls) {
		_alias = alias;
		this.provideUrls = provideUrls;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		_servlet = new ServletSetup();
		_servlet.setBasePath("/api");
		_servlet.setCacheTime(500);
		_servlet.setContext(config.getServletContext());
		_servlet.setLoader(this.getClass());
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (req.getRequestURI().endsWith("/") || req.getRequestURI().endsWith("index.html")) {

			URL url = this.getClass().getResource("/api/index.html");
			URLConnection conn = url.openConnection();

			resp.setContentType(_servlet.getContext().getMimeType("/api/index.html"));
			resp.setContentLength(conn.getContentLength());
			resp.setHeader("Cache-Control", "max-age=" + _servlet.getCacheTime());

			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

			WebUtils.writeFile(conn.getInputStream(), byteArray);

			String indexFile = new String(byteArray.toByteArray(), "UTF-8");

			StringBuilder sbReplace = new StringBuilder();

			if (provideUrls == null) {
				sbReplace.append("url: \"https://petstore.swagger.io/v2/swagger.json\", \n");
			} else {
				sbReplace.append("urls: [\n");

				int iCnt = 0;

				for (Map.Entry<String, String> entry : provideUrls.entrySet()) {
					if (iCnt > 0) {
						sbReplace.append(",\n");
					}

					sbReplace.append(String.format("{ \"name\": \"%s\", \"url\": \"%s\" }", entry.getKey(), entry.getValue()));
					iCnt++;
				}

				sbReplace.append("],\n");
			}

			String newIndex = indexFile.replaceAll("%%REPLACEME%%", sbReplace.toString());
			resp.setContentLength(newIndex.length());
			resp.getWriter().append(newIndex);

		} else {
			WebUtils.sendFile(_servlet, _servlet.getContext().getContextPath() + _alias, req, resp);
		}

	}

}
