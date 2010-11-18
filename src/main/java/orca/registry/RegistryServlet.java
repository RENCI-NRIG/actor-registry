package orca.registry;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.xmlrpc.webserver.*;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Enumeration;

public class RegistryServlet extends XmlRpcServlet{

  //private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  //private static final String CONTENT_TYPE = "application/octet-stream";

  private static ThreadLocal clientIpAddress = new ThreadLocal();

  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
  }

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

    /*
    Enumeration e = request.getParameterNames();
    while(e.hasMoreElements()){
	out.println("<p>" + e.nextElement() + "</p>");	
    }
    out.println("<p>The servlet has received a GET. This is the reply.</p>");
    */
  }

  public static String getClientIpAddress() {
        return (String) clientIpAddress.get();
  }

  public void doPost(HttpServletRequest pRequest, HttpServletResponse pResponse)
            throws IOException, ServletException {
        clientIpAddress.set(pRequest.getRemoteAddr());
        super.doPost(pRequest, pResponse);
  }


}
