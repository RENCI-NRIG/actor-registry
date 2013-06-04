package orca.registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

public class RegistryServlet extends XmlRpcServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
	//private static final String CONTENT_TYPE = "application/octet-stream";

	private static ThreadLocal<String> clientIpAddress = new ThreadLocal<String>();
	private static ThreadLocal<String> sslSessionId = new ThreadLocal<String>();
	private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();
	public static final String registryLogProperties="orca.registry.registry";
	private Logger log = null;

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		ClassLoader loader = this.getClass().getClassLoader();
		Properties p = PropertyLoader.loadProperties(registryLogProperties, loader);
		PropertyConfigurator.configure(p);
		log = Logger.getLogger(XmlrpcHandler.class);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String var0show = "";
		String var1show = "";
		try
		{
			var0show = request.getParameter("showString");
			var1show = request.getParameter("showFile");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		PrintWriter out = response.getWriter();

		if(var0show != null){
			//response.setContentType("text/html; charset=windows-1252");
			//response.setContentType("text/plain; charset=ISO-8859-1");
			response.setContentType("text/plain; charset=UTF-8");
			//out.println("<html>");
			//out.println("<head><title>Registry Query Results</title></head>");
			//out.println("<body>");
			out.println(var0show);
			//out.println("</body></html>");
			out.close();
		}

		if(var1show != null){
			response.setContentType("application/rdf+xml; charset=UTF-8");
			String filename = "/WEB-INF/" + var1show;
			ServletContext context = getServletContext();

			InputStream inp = context.getResourceAsStream(filename);
			if (inp != null) {
				InputStreamReader isr = new InputStreamReader(inp);
				BufferedReader reader = new BufferedReader(isr);
				String text = "";

				while ((text = reader.readLine()) != null) {
					out.println(text);
				}
			}
			//out.println(var1show);
		}

		//	  Object certChain = request.getAttribute("javax.servlet.request.X509Certificate");
		//	  if (certChain != null)
		//		  System.out.println("Certificate chain in GET of the client is of type " + certChain.getClass().getCanonicalName());
		//	  else
		//		  System.out.println("Certificate chain in GET of the client " + request.getRemoteAddr() + " is null");

		// if comms are secure, session id will be set
		sslSessionId.set((String)request.getAttribute("javax.servlet.request.ssl_session"));

		// insecure comms are not allowed
		if (sslSessionId.get() == null) {
			log.error("Client " + request.getRemoteAddr() + " is not using secure communications, registration and updates are not allowed");
			return;
		}

		super.doGet(request, response);

		/*
    Enumeration e = request.getParameterNames();
    while(e.hasMoreElements()){
	out.println("<p>" + e.nextElement() + "</p>");	
    }
    out.println("<p>The servlet has received a GET. This is the reply.</p>");
		 */
	}

	public static String getClientIpAddress() {
		return (String) request.get().getRemoteAddr();
	}
	//  
	//  public static String getSslSessionId() {
	//	  return (String) sslSessionId.get();
	//  }

	protected static HttpServletRequest getThreadRequest() {
		return (HttpServletRequest)request.get();
	}

	@Override
	public void doPost(HttpServletRequest pRequest, HttpServletResponse pResponse)
	throws IOException, ServletException {

		// save request on the thread
		request.set(pRequest);

		//	  clientIpAddress.set(pRequest.getRemoteAddr());
		//	  
		//	  // if comms are secure, session id will be set
		//	  sslSessionId.set((String)pRequest.getAttribute("javax.servlet.request.ssl_session"));
		//	  
		//	  // insecure comms are not allowed
		//	  if (sslSessionId.get() == null) {
		//		  log.error("Client " + pRequest.getRemoteAddr() + " is not using secure communications, registration and updates are not allowed");
		//		  return;
		//	  }

		//	  System.out.println("Available attributes: --------");
		//	  Enumeration<?> names = pRequest.getAttributeNames();
		//	  while(names.hasMoreElements())
		//		  System.out.println(names.nextElement());
		//	  System.out.println("--------");
		//	  
		//	  Object certChain = pRequest.getAttribute("javax.servlet.request.X509Certificate");
		//	  if (certChain != null)
		//		  System.out.println("Certificate chain in POST of the client " + pRequest.getRemoteAddr() + " is of type " + certChain.getClass().getCanonicalName());
		//	  else
		//		  System.out.println("Certificate chain in POST of the client " + pRequest.getRemoteAddr() + " is null");
		//	  
		//	  String sslID = (String)pRequest.getAttribute("javax.servlet.request.ssl_session");
		//	  if (sslID == null)
		//		  System.out.println("ssl id in POST of the client " + pRequest.getRemoteAddr() + " is null");
		//	  else
		//		  System.out.println("ssl id in POST of the client " + pRequest.getRemoteAddr() + " is " + sslID);

		super.doPost(pRequest, pResponse);
	}


}
